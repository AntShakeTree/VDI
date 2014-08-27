package com.opzoon.vdi.core.operation;

import static com.opzoon.vdi.core.facade.FacadeHelper.exists;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opzoon.vdi.core.cloud.CloudManager;
import com.opzoon.vdi.core.cloud.CloudManagerHelper;
import com.opzoon.vdi.core.domain.CloudManagerEntity;
import com.opzoon.vdi.core.domain.Connection;
import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.domain.FloatingDesktopExpire;
import com.opzoon.vdi.core.domain.Session;
import com.opzoon.vdi.core.domain.UserVolume;
import com.opzoon.vdi.core.domain.state.DesktopState;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.StateMatcher;
import com.opzoon.vdi.core.matcher.ConnectedDesktopMatcher;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;
import com.opzoon.vdi.core.request.DisconnectDesktopRequest;

public class DisconnectDesktopOperation implements Operation
{

  private final int desktoppoolid;
  private final int desktopid;
  private final Integer prefetchedsessionid;
  private final String prefetchedusername;

  public DisconnectDesktopOperation(int desktoppoolid, int desktopid, Integer prefetchedsessionid, String prefetchedusername)
  {
    this.desktoppoolid = desktoppoolid;
    this.desktopid = desktopid;
    this.prefetchedsessionid = prefetchedsessionid;
    this.prefetchedusername = prefetchedusername;
  }

  @Override
  public Object execute(final OperationContext operationContext) throws CommonException
  {
    final DesktopPoolEntity desktopPool = operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(DesktopPoolEntity.class, desktoppoolid);
//    final Desktop desktop = operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(Desktop.class, desktopid);
    DesktopState desktopState = (DesktopState) operationContext.getOperationRegistry().getStateMachine().loadState(new Entity(DesktopStatus.class, desktopid));
    final int sessionid;
    if (prefetchedsessionid == null)
    {
      sessionid = (Integer) operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().findFirst(
          "select idsession from Session where userid = ? and logintype = ? order by deleted",
          desktopState.getOwnerid(), Session.LOGIN_TYPE_USER);
    } else
    {
      sessionid = prefetchedsessionid.intValue();
    }
    @SuppressWarnings("unchecked")
	final List<Connection> connections =  (List<Connection>)operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().find(
        "from Connection where resourceid = ? and sessionid = ?",
        desktoppoolid, sessionid);
    for (Connection connection : connections) {
        String brokerIP = operationContext.getOperationRegistry().getConfiguration().getBrokerIP();
        operationContext.getOperationRegistry().publishStateRequest(new DisconnectDesktopRequest(
            desktopid,
            connection,
            operationContext.getOperationRegistry().getConnectionManager(),
            brokerIP));
	}

//    final User user = operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(User.class, desktopState.getOwnerid());
//    try {
//      final String ip = OperationHelper.loadIP(desktopPool, desktop);
//      VdiAgentClientImpl.logOff(ip, desktopPool.getDomainname(), user.getUsername());
//    } catch (Exception e) {
//      // TODO
//      e.printStackTrace();
//      return null;
//    }
    if (desktopPool.getVmsource() == DesktopPoolEntity.DESKTOP_POOL_SOURCE_AUTO)
    {
      @SuppressWarnings("unchecked")
      List<UserVolume> userVolumes = (List<UserVolume>) operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().find(
          "from UserVolume where userid = ? and desktopid = ?",
          desktopState.getOwnerid(), desktopid);
      final CloudManager cloudManager = CloudManagerHelper.findCloudManager(
          operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().load(CloudManagerEntity.class, desktopPool.getCloudmanagerid()));
      for (final UserVolume userVolume : userVolumes) {
        final String storageid = userVolume.getStorageid();
        operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().update(
            "update UserVolume set desktoppoolid = null, desktopid = null, lastdetachtime = ? where iduservolume = ?",
            (int) (System.currentTimeMillis() / 1000), userVolume.getIduservolume());
        try
        {
          cloudManager.detachVolume(storageid);
        } catch (Exception e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    if (desktopPool.getAssignment() == DesktopPoolEntity.DESKTOP_POOL_ASSIGNMENT_FLOATING)
    {
      Date expireDate = new Date(System.currentTimeMillis() + desktopPool.getUnassignmentdelay() * 60 * 1000);
      if (!exists(operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().update(
          "update FloatingDesktopExpire set expire = ? where desktopid = ?",
          expireDate, desktopid))) {
        FloatingDesktopExpire expire = new FloatingDesktopExpire();
        expire.setDesktoppoolid(desktoppoolid);
        expire.setDesktopid(desktopid);
        expire.setExpire(expireDate);
        operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().persist(expire);
      }
    }
    return null;
  }

  @Override
  public boolean rejects(Operation operation)
  {
    return false;
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
    neededStates.put(new Entity(DesktopStatus.class, desktopid), new ConnectedDesktopMatcher());
    return neededStates;
  }

  @Override
  public List<Object> getParams()
  {
    return Arrays.asList(new Object[] { desktoppoolid, desktopid, prefetchedsessionid, prefetchedusername });
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
    final DisconnectDesktopOperation that = (DisconnectDesktopOperation) object;
    return this.desktopid == that.desktopid;
  }

  @Override
  public int hashCode()
  {
    return this.desktopid;
  }

}
