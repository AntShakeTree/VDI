package com.opzoon.vdi.core.request;

import java.util.HashSet;
import java.util.Set;

import com.opzoon.vdi.core.cloud.ConnectionManager;
import com.opzoon.vdi.core.domain.DesktopPoolStatus;
import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.Request;
import com.opzoon.vdi.core.support.Configuration;

public class ConnectDesktopRequest implements Request
{

  private final int desktoppoolid;
  private final int desktopid;
  private final int sessionid;
  private final int brokerprotocol;
  private final String hostname;
  private final int hostport;
  private final Configuration configuration;
  private final ConnectionManager connectionManager;
  private final int[] connectionIdWrapper;

  public ConnectDesktopRequest(int desktoppoolid, int desktopid, int sessionid, int brokerprotocol,
      String hostname, int hostport, Configuration configuration,
      ConnectionManager connectionManager, int[] connectionIdWrapper)
  {
    this.desktoppoolid = desktoppoolid;
    this.desktopid = desktopid;
    this.sessionid = sessionid;
    this.brokerprotocol = brokerprotocol;
    this.hostname = hostname;
    this.hostport = hostport;
    this.configuration = configuration;
    this.connectionManager = connectionManager;
    this.connectionIdWrapper = connectionIdWrapper;
  }

  @Override
  public Set<Entity> getTargetEntities()
  {
    final Set<Entity> targetEntities = new HashSet<Entity>();
    targetEntities.add(new Entity(DesktopPoolStatus.class, desktoppoolid));
    targetEntities.add(new Entity(DesktopStatus.class, desktopid));
    return targetEntities;
  }

  public int getDesktoppoolid()
  {
    return desktoppoolid;
  }

  public int getDesktopid()
  {
    return desktopid;
  }

  public int getSessionid()
  {
    return sessionid;
  }

  public int getBrokerprotocol()
  {
    return brokerprotocol;
  }

  public String getHostname()
  {
    return hostname;
  }

  public int getHostport()
  {
    return hostport;
  }

  public Configuration getConfiguration()
  {
    return configuration;
  }

  public ConnectionManager getConnectionManager()
  {
    return connectionManager;
  }

  public int[] getConnectionIdWrapper()
  {
    return connectionIdWrapper;
  }

}
