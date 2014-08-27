package com.opzoon.vdi.core.operation;

import static com.opzoon.vdi.core.util.StringUtils.strcat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opzoon.ohvc.common.Job;
import com.opzoon.ohvc.common.JobStatus;
import com.opzoon.ohvc.service.VdiAgentClientImpl;
import com.opzoon.vdi.core.cloud.CloudManager;
import com.opzoon.vdi.core.cloud.CloudManagerHelper;
import com.opzoon.vdi.core.domain.CloudManagerEntity;
import com.opzoon.vdi.core.domain.Desktop;
import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.DesktopPoolStatus;
import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.domain.VMInstance;
import com.opzoon.vdi.core.event.OperationDelayedEvent;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.StateMatcher;
import com.opzoon.vdi.core.matcher.CloneableDesktopMatcher;
import com.opzoon.vdi.core.matcher.NormalDesktopPoolMatcher;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;
import com.opzoon.vdi.core.refresher.AgentJobRefresher;
import com.opzoon.vdi.core.refresher.CloudJobRefresher;
import com.opzoon.vdi.core.refresher.RDPRefresher;
import com.opzoon.vdi.core.refresher.RDPWithTimeoutRefresher;
import com.opzoon.vdi.core.request.FinishCloningDesktopRequest;
import com.opzoon.vdi.core.request.RepealDesktopRequest;
import com.opzoon.vdi.core.request.StartCreatingDesktopRequest;

public class CloneInstanceOperation implements Operation
{
  
  private static final Logger log = LoggerFactory.getLogger(CloneInstanceOperation.class);

  private final int desktoppoolid;
  private final int cloudmanagerid;
  private final int desktopid;
  private final String oldInstanceName;

  public CloneInstanceOperation(int desktoppoolid, int cloudmanagerid, int desktopid, String oldInstanceName)
  {
    this.desktoppoolid = desktoppoolid;
    this.cloudmanagerid = cloudmanagerid;
    this.desktopid = desktopid;
    this.oldInstanceName = oldInstanceName;
  }

  @Override
  public Object execute(OperationContext operationContext) throws CommonException
  {
    final DesktopPoolEntity desktopPool = operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(DesktopPoolEntity.class, desktoppoolid);
    final Desktop desktop = operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(Desktop.class, desktopid);
    operationContext.getOperationRegistry().publishStateRequest(new StartCreatingDesktopRequest(desktopid));
    final String instanceName;
    if (oldInstanceName == null) {
      instanceName = desktop.getVmname() + "-" + desktoppoolid + "-" + 1;
    } else {
      final String[] oldInstanceNameArray = oldInstanceName.split("\\-");
      instanceName = desktop.getVmname() + "-" + desktoppoolid + "-" + (Integer.parseInt(oldInstanceNameArray[oldInstanceNameArray.length - 1]) + 1);
    }
    final CloudManager cloudManager = CloudManagerHelper.findCloudManager(
        operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(CloudManagerEntity.class, desktopPool.getCloudmanagerid()));
    outter:
    try {
      Job<?> cloudJob = cloudManager.cloneVM(desktopPool.getTemplateid(), instanceName,desktopPool.getLink()==1?true:false);
      String vmid = cloudJob.getId();
      VMInstance instance = null;
      if (vmid.indexOf(',') > -1)
      {
        vmid = null;
        operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().update(
            "update Desktop set vmid = null where iddesktop = ?",
            desktopid);
      } else
      {
        try
        {
          instance = cloudManager.getVM(vmid);// FIXME SB V03.
        } catch (Exception e)
        {
          log.warn("Instance " + vmid + " not found", e);
        }
        if (instance == null) {// CS & LLS
            vmid = null;
            operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().update(
                "update Desktop set vmid = null where iddesktop = ?",
                desktopid);
		} else
		{
	        operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().update(
	                "update Desktop set vmid = ? where iddesktop = ?",
	                instance.getId(), desktopid);
		}
      }
      operationContext.getOperationRegistry().refreshFrequently(this, operationContext, new CloudJobRefresher(cloudManager, cloudJob));
      if (cloudJob.getStatus() == JobStatus.FAILED) {
        // TODO Auto-generated catch block
        operationContext.getOperationRegistry().publishStateRequest(new RepealDesktopRequest(desktopid));
        break outter;
      }
      instance = (VMInstance) cloudJob.getResult();
      operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().update(
          "update Desktop set vmid = ? where iddesktop = ?",
          instance.getId(), desktopid);
      cloudJob = cloudManager.startVM(instance.getId());
      operationContext.getOperationRegistry().refreshFrequently(this, operationContext, new CloudJobRefresher(cloudManager, cloudJob));
      boolean[] rdpStatus = new boolean[1];
      operationContext.getOperationRegistry().refreshFrequently(this, operationContext, new RDPWithTimeoutRefresher(instance.getIpaddress(), rdpStatus));
      if (!rdpStatus[0])
      {
        log.warn(String.format("Cloning %s failed for RDP timeout.", instance.getId()));
        operationContext.getOperationRegistry().publishStateRequest(new RepealDesktopRequest(desktopid));
        return null;
      }
      if (VdiAgentClientImpl.isJointype(instance.getIpaddress()))
      {
        log.warn(String.format("Cloning %s failed for template join domain.", instance.getId()));
        operationContext.getOperationRegistry().publishStateRequest(new RepealDesktopRequest(desktopid));
        return null;
      }
      cloudJob = VdiAgentClientImpl.setHostname(
          instance.getIpaddress(),
          guessPcname(desktopPool.getComputernamepattern(), desktop.getVmname()),
          2,// TODO Domain.
          "",
          "",
          true);
      operationContext.getOperationRegistry().refreshFrequently(this, operationContext, new AgentJobRefresher(cloudJob));
      if (desktopPool.getDomainname() != null) {
        operationContext.getOperationRegistry().refreshFrequently(this, operationContext, new RDPRefresher(instance.getIpaddress()));
        cloudJob = VdiAgentClientImpl.joinDomain(
            instance.getIpaddress(),
            desktopPool.getDomainname(),
            desktopPool.getDomainbinddn().replaceFirst("^.*\\\\", ""),
            desktopPool.getDomainbindpass(),
            true);
        operationContext.getOperationRegistry().refreshFrequently(this, operationContext, new AgentJobRefresher(cloudJob));
      }
      try
      {
        operationContext.getOperationRegistry().publishStateRequest(new FinishCloningDesktopRequest(desktopid));
      } catch (Exception e)
      {
        log.warn(String.format("FinishCloningDesktopRequest on desktop %d(%s) failed.", desktopid, instance.getId()), e);
        cloudManager.destroyVM(instance.getId());
        throw e;
      }
    } catch (Exception e)
    {
      log.warn(String.format("Repealing desktop %d.", desktopid), e);
      operationContext.getOperationRegistry().publishStateRequest(new RepealDesktopRequest(desktopid));
    }
//    // FIXME For no events. <
//    DesktopPoolState desktopPoolState = (DesktopPoolState) operationContext.getOperationRegistry().getStateMachine().loadState(new Entity(DesktopPoolStatus.class, desktoppoolid));
//    if (desktopPoolState.getPhase() != DesktopPoolState.DESKTOP_POOL_PHASE_NORMAL)
//    {
//      operationContext.getOperationRegistry().start(new DeleteDesktopOperation(
//          Integer.toHexString(desktop.getDesktoppoolid())
//          + "#" + Integer.toHexString(desktopPool.getCloudmanagerid())
//          + "#" + Integer.toHexString(desktopid), desktop.getVmid(), true));
//    }
//    // For no events. >
    return null;
  }

  @Override
  public boolean rejects(Operation operation)
  {
    return this.equals(operation);
  }

  @Override
  public boolean delays(Operation operation)
  {
    if (operation instanceof DeleteCloudManagerOperation
        && ((DeleteCloudManagerOperation) operation).getCloudmanagerid() == cloudmanagerid)
    {
      return true;
    } else if (operation instanceof DeleteDesktopPoolOperation
        && ((DeleteDesktopPoolOperation) operation).getDesktoppoolid() == desktoppoolid)
    {
      return true;
    } else if (operation instanceof DeleteDesktopOperation
        && ((DeleteDesktopOperation) operation).getDesktopid() == desktopid)
    {
      return true;
    } else if (operation instanceof AsyncStopDesktopOperation
        && ((AsyncStopDesktopOperation) operation).getDesktopid() == desktopid)
    {
      return true;
    } else if (operation instanceof AsyncRebootDesktopOperation
        && ((AsyncRebootDesktopOperation) operation).getDesktopid() == desktopid)
    {
      return true;
    }
    return false;
  }

  @Override
  public boolean onEvent(OperationContext operationContext, Event event)
  {
    if (event instanceof OperationDelayedEvent)
    {
      final OperationDelayedEvent operationDelayedEvent = (OperationDelayedEvent) event;
      final Operation delayedOperation = operationDelayedEvent.getOperation();
      if (delayedOperation instanceof DeleteCloudManagerOperation)
      {
        // TODO
        return true;
      } else if (delayedOperation instanceof DeleteDesktopPoolOperation)
      {
        // TODO
        return true;
      } else if (delayedOperation instanceof DeleteDesktopOperation)
      {
        // TODO
        return true;
      }
    }
    return false;
  }

  @Override
  public Map<Entity, StateMatcher> getNeededStates()
  {
    final Map<Entity, StateMatcher> neededStates = new HashMap<Entity, StateMatcher>();
    neededStates.put(new Entity(DesktopPoolStatus.class, desktoppoolid), new NormalDesktopPoolMatcher());
    neededStates.put(new Entity(DesktopStatus.class, desktopid), new CloneableDesktopMatcher());
    return neededStates;
  }

  @Override
  public List<Object> getParams()
  {
    return Arrays.asList(new Object[] { desktoppoolid, cloudmanagerid, desktopid, oldInstanceName });
  }

  @Override
  public boolean equals(Object object)
  {
    if (object == null)
    {
      return false;
    }
    if (!object.getClass().equals(this.getClass()))
    {
      return false;
    }
    final CloneInstanceOperation that = (CloneInstanceOperation) object;
    return this.desktopid == that.desktopid;
  }

  @Override
  public int hashCode()
  {
    return this.desktopid;
  }

  private static String guessPcname(String computernamepattern, String vmname) {
    String suffix = vmname.substring(vmname.lastIndexOf('-'));
    if (computernamepattern.length() + suffix.length() > 15) {
      computernamepattern = computernamepattern.substring(0, 15 - suffix.length());
    }
    return strcat(computernamepattern, suffix);
  }

}
