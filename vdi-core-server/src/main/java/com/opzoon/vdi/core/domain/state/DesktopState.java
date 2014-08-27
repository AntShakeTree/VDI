package com.opzoon.vdi.core.domain.state;

import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_VISITOR_TYPE_GROUP;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_VISITOR_TYPE_ORGANIZATION;
import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_VISITOR_TYPE_USER;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.facade.FacadeHelper.exists;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.opzoon.vdi.core.cloud.ConnectionManager.ConnectionInfo;
import com.opzoon.vdi.core.domain.Connection;
import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.GroupElement;
import com.opzoon.vdi.core.domain.ResourceAssignment;
import com.opzoon.vdi.core.domain.User;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.facade.DatabaseFacade;
import com.opzoon.vdi.core.fsm.Request;
import com.opzoon.vdi.core.fsm.State;
import com.opzoon.vdi.core.request.AllocateDesktopRequest;
import com.opzoon.vdi.core.request.ConnectDesktopRequest;
import com.opzoon.vdi.core.request.DeallocateDesktopRequest;
import com.opzoon.vdi.core.request.DeleteDesktopRequest;
import com.opzoon.vdi.core.request.DisconnectDesktopRequest;
import com.opzoon.vdi.core.request.FinalizeDesktopRequest;
import com.opzoon.vdi.core.request.FinishCloningDesktopRequest;
import com.opzoon.vdi.core.request.RepealDesktopRequest;
import com.opzoon.vdi.core.request.StartCreatingDesktopRequest;
import com.opzoon.vdi.core.support.Configuration;

public class DesktopState implements State
{

  public static final int DESKTOP_PHASE_START = 1;
  public static final int DESKTOP_PHASE_CREATING = 2;
  public static final int DESKTOP_PHASE_NORMAL = 3;
  public static final int DESKTOP_PHASE_DEFICIENT = 4;
  public static final int DESKTOP_PHASE_DELETING = 5;
  public static final int DESKTOP_PHASE_END = 0;
  public static final int DESKTOP_STATUS_UNMANAGED = 1;
  public static final int DESKTOP_STATUS_UNKNOWN = 2;
  public static final int DESKTOP_STATUS_ERROR = 3;
  public static final int DESKTOP_STATUS_PROVISIONING = 4;
  public static final int DESKTOP_STATUS_STARTING = 5;
  public static final int DESKTOP_STATUS_RUNNING = 6;
  public static final int DESKTOP_STATUS_SERVING = 7;
  public static final int DESKTOP_STATUS_STOPPING = 8;
  public static final int DESKTOP_STATUS_STOPPED = 9;
  public static final int DESKTOP_CONNECTIVITY_STANDBY = 1;
  public static final int DESKTOP_CONNECTIVITY_CONNECTED = 2;

  private final int phase;
  private final int status;
  private final int ownerid;
  private final int connectivity;

  public DesktopState(int phase, int status, int ownerid, int connectivity)
  {
    this.phase = phase;
    this.status = status;
    this.ownerid = ownerid;
    this.connectivity = connectivity;
  }

  @Override
  public boolean accepts(DatabaseFacade databaseFacade, Request request)
  {
    if (request instanceof DeleteDesktopRequest)
    {
      return phase == DESKTOP_PHASE_CREATING
          || phase == DESKTOP_PHASE_NORMAL
          || phase == DESKTOP_PHASE_DEFICIENT;
    } else if (request instanceof FinalizeDesktopRequest)
    {
      return phase == DESKTOP_PHASE_DELETING;
    } else if (request instanceof DeallocateDesktopRequest)
    {
      return ownerid != -1;
    } else if (request instanceof DisconnectDesktopRequest)
    {
      return connectivity == DESKTOP_CONNECTIVITY_CONNECTED;
    } else if (request instanceof StartCreatingDesktopRequest)
    {
      return (phase == DESKTOP_PHASE_START
          || phase == DESKTOP_PHASE_CREATING
          || phase == DESKTOP_PHASE_NORMAL
          || phase == DESKTOP_PHASE_DEFICIENT)
          && connectivity == DESKTOP_CONNECTIVITY_STANDBY;
    } else if (request instanceof RepealDesktopRequest)
    {
      return phase == DESKTOP_PHASE_CREATING;
    } else if (request instanceof FinishCloningDesktopRequest)
    {
      return phase == DESKTOP_PHASE_CREATING;
    } else if (request instanceof AllocateDesktopRequest)
    {
      final AllocateDesktopRequest allocateDesktopRequest = (AllocateDesktopRequest) request;
      if (!this.isAssigned(databaseFacade, allocateDesktopRequest.getUserid(), allocateDesktopRequest.getDesktoppoolid()))
      {
        return false;
      }
      return ownerid == -1;
    } else if (request instanceof ConnectDesktopRequest)
    {
      return connectivity == DESKTOP_CONNECTIVITY_STANDBY;
    }
    return false;
  }

  @Override
  public State accept(DatabaseFacade databaseFacade, Request request) throws CommonException
  {
    if (request instanceof DeleteDesktopRequest)
    {
      return new DesktopState(DESKTOP_PHASE_DELETING, status, ownerid, connectivity);
    } else if (request instanceof FinalizeDesktopRequest)
    {
      return new DesktopState(DESKTOP_PHASE_END, status, ownerid, connectivity);
    } else if (request instanceof DeallocateDesktopRequest)
    {
      return new DesktopState(phase, status, -1, connectivity);
    } else if (request instanceof DisconnectDesktopRequest)
    {
      final DisconnectDesktopRequest disconnectDesktopRequest = (DisconnectDesktopRequest) request;
      final Connection connection = disconnectDesktopRequest.getConnection();
      int error = disconnectDesktopRequest.getConnectionManager().destroyConnection(
          connection.getBrokerport(),
          connection.getHostname(),
          connection.getHostport(),
          disconnectDesktopRequest.getBrokerip(),
          connection.getTunnelname() != null);
      if (numberNotEquals(error, NO_ERRORS)) {
        // Logging and ignoring the error.
        // TODO
//        log.warn("Destroy connection {} failed with error {}.", connection.getIdconnection(), error);
      }
      try{
      databaseFacade.update(
              "delete  from Connection where resourceid = ? and sessionid = ?",
              connection.getResourceid(),connection.getSessionid());
      databaseFacade.update(
          "delete from Connection where idconnection = ?",
          connection.getIdconnection());
      }catch(Exception e){
      }
      
     
      return new DesktopState(phase, status, ownerid, DESKTOP_CONNECTIVITY_STANDBY);
    } else if (request instanceof StartCreatingDesktopRequest)
    {
      return new DesktopState(DESKTOP_PHASE_CREATING, status, ownerid, connectivity);
    } else if (request instanceof RepealDesktopRequest)
    {
      return new DesktopState(DESKTOP_PHASE_DEFICIENT, status, ownerid, connectivity);
    } else if (request instanceof FinishCloningDesktopRequest)
    {
      return new DesktopState(DESKTOP_PHASE_NORMAL, DESKTOP_STATUS_SERVING, ownerid, connectivity);
    } else if (request instanceof AllocateDesktopRequest)
    {
      final AllocateDesktopRequest allocateDesktopRequest = (AllocateDesktopRequest) request;
      return new DesktopState(phase, status, allocateDesktopRequest.getUserid(), connectivity);
    } else if (request instanceof ConnectDesktopRequest)
    {
      final ConnectDesktopRequest connectDesktopRequest = (ConnectDesktopRequest) request;
      final Configuration configuration = connectDesktopRequest.getConfiguration();
      int brokerprotocol = connectDesktopRequest.getBrokerprotocol();
      final boolean spice =
          brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTPS
          || brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTP
          || brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE;
      final boolean tunnel =
          brokerprotocol == DesktopPoolEntity.PROTOCOL_RDP_OVER_HTTPS
          || brokerprotocol == DesktopPoolEntity.PROTOCOL_RDP_OVER_HTTP
          || brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTPS
          || brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTP;
      ConnectionInfo connectionInfo = connectDesktopRequest.getConnectionManager().establishConnection(
          connectDesktopRequest.getHostname(),
          connectDesktopRequest.getHostport(),
          configuration.getBrokerName(),
          configuration.getBrokerIP(),
          configuration.getBrokerPortMin(), configuration.getBrokerPortMax(),
          spice,
          tunnel);
      if (connectionInfo == null) {
        throw new CommonException(CommonException.UNKNOWN);
      }
      connectionInfo.setBrokerprotocol(brokerprotocol);
      String tunnelName = (brokerprotocol == DesktopPoolEntity.PROTOCOL_RDP_OVER_HTTPS
          || brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTPS) ? configuration.getHttpsTunnelName() :
            ((brokerprotocol == DesktopPoolEntity.PROTOCOL_RDP_OVER_HTTP
            || brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTP) ? configuration.getHttpTunnelName() : null);
      int tunnelPort = (brokerprotocol == DesktopPoolEntity.PROTOCOL_RDP_OVER_HTTPS
          || brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTPS) ? configuration.getHttpsTunnelPort() :
            ((brokerprotocol == DesktopPoolEntity.PROTOCOL_RDP_OVER_HTTP
            || brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTP) ? configuration.getHttpTunnelPort() : -1);
      if (brokerprotocol != DesktopPoolEntity.PROTOCOL_RDP && brokerprotocol != DesktopPoolEntity.PROTOCOL_SPICE) {
        if (tunnelName == null || tunnelName.length() < 1) {
          tunnelName = connectionInfo.getBrokername();
        }
      }
      Connection connection = new Connection();
      connection.copyFrom(connectionInfo);
      connection.setExpire(this.calculateNewExpire());
      connection.setSessionid(connectDesktopRequest.getSessionid());
      connection.setResourceid(connectDesktopRequest.getDesktoppoolid());
      connection.setResourcetype(ResourceAssignment.RESOURCE_TYPE_POOL);
      connection.setTunnelname(tunnelName);
      if (tunnelPort == -1) {
        connection.setTunnelport(null);
      } else {
        connection.setTunnelport(tunnelPort);
      }
      connection.setBrokerprotocol(brokerprotocol);
      databaseFacade.persist(connection);
      connectDesktopRequest.getConnectionIdWrapper()[0] = connection.getIdconnection();
      return new DesktopState(phase, status, ownerid, DESKTOP_CONNECTIVITY_CONNECTED);
    }
    throw new RuntimeException("Impossible");
  }

  public int getPhase()
  {
    return phase;
  }

  public int getStatus()
  {
    return status;
  }

  public int getOwnerid()
  {
    return ownerid;
  }

  public int getConnectivity()
  {
    return connectivity;
  }

  private boolean isAssigned(DatabaseFacade databaseFacade, int userid, int desktoppoolid) {
    final List<AssignmentTarget> assignmentTargets = new LinkedList<AssignmentTarget>();
    assignmentTargets.add(new AssignmentTarget(RESOURCE_VISITOR_TYPE_USER, userid));
    final User user = databaseFacade.load(User.class, userid);
    @SuppressWarnings("unchecked")
    List<Integer> userGroups = (List<Integer>) databaseFacade.find(
        "select groupid from GroupElement where elementtype = ? and elementid = ?",
        GroupElement.ELEMENT_TYPE_USER, userid);
    for (final int userGroup : userGroups)
    {
      assignmentTargets.add(new AssignmentTarget(RESOURCE_VISITOR_TYPE_GROUP, userGroup));
    }
    List<Integer> organizations = this.findOrganizations(databaseFacade, user.getOrganizationid());
    for (final int organization : organizations)
    {
      assignmentTargets.add(new AssignmentTarget(RESOURCE_VISITOR_TYPE_ORGANIZATION, organization));
      @SuppressWarnings("unchecked")
      List<Integer> organizationGroups = (List<Integer>) databaseFacade.find(
          "select groupid from GroupElement where elementtype = ? and elementid = ?",
          GroupElement.ELEMENT_TYPE_ORGANIZATION, organization);
      for (final int organizationGroup : organizationGroups)
      {
        assignmentTargets.add(new AssignmentTarget(RESOURCE_VISITOR_TYPE_GROUP, organizationGroup));
      }
    }
    for (final AssignmentTarget assignmentTarget : assignmentTargets)
    {
      if (exists(databaseFacade.findFirst(
          "select count(idresourceassignment) from ResourceAssignment where resourceid = ? and visitortype = ? and visitorid = ?",
          desktoppoolid, assignmentTarget.getType(), assignmentTarget.getId()))) {
        return true;
      }
    }
    return false;
  }
  
  public List<Integer> findOrganizations(DatabaseFacade databaseFacade, Integer organizationid) {
    List<Integer> parents = new LinkedList<Integer>();
    if (organizationid == null)
    {
      return parents;
    }
    for (Integer checkingOrganization = organizationid; checkingOrganization > -1; ) {
      parents.add(checkingOrganization);
      checkingOrganization = (Integer) databaseFacade.findFirst(
          "select parent from Organization where idorganization = ?",
          checkingOrganization);
    }
    return parents;
  }
  
  private static class AssignmentTarget
  {
    private final int type;
    private final int id;
    public AssignmentTarget(int type, int id)
    {
      this.type = type;
      this.id = id;
    }
    public int getType()
    {
      return type;
    }
    public int getId()
    {
      return id;
    }
  }

  private Date calculateNewExpire() {
    // TODO Postponed. Expire unsupported for now. All connections are available forever so we give it 1 century time.
    return new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365 * 100);
  }

}
