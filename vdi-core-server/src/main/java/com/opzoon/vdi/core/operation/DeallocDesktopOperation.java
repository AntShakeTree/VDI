package com.opzoon.vdi.core.operation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opzoon.ohvc.common.Job;
import com.opzoon.ohvc.service.VdiAgentClientImpl;
import com.opzoon.vdi.core.domain.CloudManagerEntity;
import com.opzoon.vdi.core.domain.Desktop;
import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.domain.User;
import com.opzoon.vdi.core.domain.state.DesktopState;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.StateMatcher;
import com.opzoon.vdi.core.matcher.AllocatedDesktopMatcher;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;
import com.opzoon.vdi.core.refresher.AgentJobRefresher;
import com.opzoon.vdi.core.refresher.StandbyDesktopRefresher;
import com.opzoon.vdi.core.request.DeallocateDesktopRequest;

public class DeallocDesktopOperation implements Operation
{

  private final int desktoppoolid;
  private final int desktopid;
  private final boolean force;
  private final Integer prefetchedsessionid;
  private final String prefetchedusername;

  public DeallocDesktopOperation(String desktoppoolidNdesktopid, boolean force, Integer prefetchedsessionid, String prefetchedusername)
  {
    String[] ps = desktoppoolidNdesktopid.split("#");
    this.desktoppoolid = Integer.parseInt(ps[0], 16);
    this.desktopid = Integer.parseInt(ps[1], 16);
    this.force = force;
    this.prefetchedsessionid = prefetchedsessionid;
    this.prefetchedusername = prefetchedusername;
  }

  @Override
  public Object execute(OperationContext operationContext) throws CommonException
  {
    final DesktopPoolEntity desktopPool = operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(DesktopPoolEntity.class, desktoppoolid);
    final Desktop desktop = operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(Desktop.class, desktopid);
    if (force)
    {
      try
      {
        operationContext.getOperationRegistry().start(new DisconnectDesktopOperation(desktoppoolid, desktopid, prefetchedsessionid, prefetchedusername));
      } catch (CommonException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    operationContext.getOperationRegistry().refreshFrequently(this, operationContext, new StandbyDesktopRefresher(desktopid));
    final DesktopState desktopState = (DesktopState) operationContext.getOperationRegistry().getStateMachine().loadState(new Entity(DesktopStatus.class, desktopid));
    operationContext.getOperationRegistry().publishStateRequest(new DeallocateDesktopRequest(desktopid));
    operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().update(
        "update Desktop set ownername = null where iddesktop = ?",
        desktopid);
    final String username;
    if (prefetchedusername == null)
    {
      final User user = operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(User.class, desktopState.getOwnerid());
      username = user.getUsername();
    } else
    {
      username = prefetchedusername;
    }
    try {
      final String ip = OperationHelper.loadIP(
          operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(CloudManagerEntity.class, desktopPool.getCloudmanagerid()), desktopPool, desktop);
      Job<String> job = VdiAgentClientImpl.deleteUser(ip, desktopPool.getDomainname(), username);
      operationContext.getOperationRegistry().refreshFrequently(this, operationContext, new AgentJobRefresher(job));
    } catch (Exception e) {
      // TODO
      e.printStackTrace();
      return null;
    }
    if (desktopPool.getVmsource() == DesktopPoolEntity.DESKTOP_POOL_SOURCE_AUTO
        && desktopState.getPhase() != DesktopState.DESKTOP_PHASE_DELETING)
    {
      operationContext.getOperationRegistry().start(new ResetDesktopOperation(desktoppoolid, desktopPool.getCloudmanagerid(), desktopid, desktop.getVmid()));
    }
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
    if (operation instanceof AsyncStopDesktopOperation
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
    return false;
  }

  @Override
  public Map<Entity, StateMatcher> getNeededStates()
  {
    final Map<Entity, StateMatcher> neededStates = new HashMap<Entity, StateMatcher>();
    neededStates.put(new Entity(DesktopStatus.class, desktopid), new AllocatedDesktopMatcher());
    return neededStates;
  }

  @Override
  public List<Object> getParams()
  {
    return Arrays.asList(new Object[] { Integer.toHexString(desktoppoolid) + "#" + Integer.toHexString(desktopid), force, prefetchedsessionid, prefetchedusername });
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
    final DeallocDesktopOperation that = (DeallocDesktopOperation) object;
    return this.desktopid == that.desktopid;
  }

  @Override
  public int hashCode()
  {
    return this.desktopid;
  }

}
