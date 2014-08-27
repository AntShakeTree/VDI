package com.opzoon.vdi.core.request;

import java.util.HashSet;
import java.util.Set;

import com.opzoon.vdi.core.cloud.ConnectionManager;
import com.opzoon.vdi.core.domain.Connection;
import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.Request;

public class DisconnectDesktopRequest implements Request
{

  private final int desktopid;
  private final Connection connection;
  private final ConnectionManager connectionManager;
  private final String brokerip;

  public DisconnectDesktopRequest(int desktopid, Connection connection,
      ConnectionManager connectionManager, String brokerip)
  {
    this.desktopid = desktopid;
    this.connection = connection;
    this.connectionManager = connectionManager;
    this.brokerip = brokerip;
  }

  @Override
  public Set<Entity> getTargetEntities()
  {
    final Set<Entity> targetEntities = new HashSet<Entity>();
    targetEntities.add(new Entity(DesktopStatus.class, desktopid));
    return targetEntities;
  }

  public int getDesktopid()
  {
    return desktopid;
  }

  public Connection getConnection()
  {
    return connection;
  }

  public ConnectionManager getConnectionManager()
  {
    return connectionManager;
  }

  public String getBrokerip()
  {
    return brokerip;
  }

}
