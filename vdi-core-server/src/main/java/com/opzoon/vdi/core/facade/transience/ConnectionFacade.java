package com.opzoon.vdi.core.facade.transience;

import static com.opzoon.vdi.core.domain.ResourceAssignment.RESOURCE_TYPE_POOL;
import static com.opzoon.vdi.core.facade.FacadeHelper.count;
import static com.opzoon.vdi.core.facade.FacadeHelper.pagingFind;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.opzoon.vdi.core.cloud.ConnectionManager.ConnectionInfo;
import com.opzoon.vdi.core.domain.Connection;
import com.opzoon.vdi.core.domain.ResourceAssignment;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;
import com.opzoon.vdi.core.facade.StorageFacade;

/**
 * 资源和连接相关业务接口.
 */
public class ConnectionFacade {

	private StorageFacade storageFacade;

	@SuppressWarnings("unchecked")
	public List<Connection> findConnectionsBySession(Integer session) {
		return (List<Connection>) storageFacade.find(
				"from Connection where sessionid = ?",
				session);
	}

	public void removeConnection(Connection connection) {
		storageFacade.update("delete from Connection where idconnection = ?", connection.getIdconnection());
	}

	public Connection findConnection(String connectionticket, Integer idsession) {
		return (Connection) storageFacade.findFirst(
				"from Connection where connectionticket = ? and sessionid = ?",
				connectionticket, idsession);
	}

	@SuppressWarnings("unchecked")
	public List<Connection> findConnections(int resourceTypePool,
			Integer iddesktoppool) {
		return (List<Connection>) storageFacade.find(
				"from Connection where resourcetype = ? and resourceid = ?",
				RESOURCE_TYPE_POOL, iddesktoppool);
	}

	public Connection createNewConnection(ConnectionInfo connectionInfo,
			int resourcetype, int resourceid, int idsession, String tunnelName, int tunnelPort, int brokerprotocol) {
		Connection connection = new Connection();
		connection.copyFrom(connectionInfo);
		connection.setExpire(this.calculateNewExpire());
		connection.setSessionid(idsession);
		connection.setResourceid(resourceid);
		connection.setResourcetype(resourcetype);
		connection.setTunnelname(tunnelName);
		if (tunnelPort == -1) {
			connection.setTunnelport(null);
		} else {
			connection.setTunnelport(tunnelPort);
		}
		connection.setBrokerprotocol(brokerprotocol);
		storageFacade.persist(connection);
		return connection;
	}

	public Connection findConnection(int idconnection) {
		return storageFacade.load(Connection.class, idconnection);
	}

	//add by tanyunhua , for find desktop connection count;  start -----------
	public int findDesktopConnectionCount()
	{
		StringBuilder whereClause = new StringBuilder("from Connection where resourcetype = ?");
		List<Object> params = new ArrayList<Object>();
		params.add(1);									//resourcetype为1 表示是桌面desktop
		Object[] paramsArray = params.toArray();
		int[] amountContainer = new int[1];
		count(storageFacade, "idconnection", whereClause, paramsArray, amountContainer);
		return amountContainer[0];
	}
	/**
	 * 列举连接.
	 * 
	 * @param sessionid 会话ID.
	 * @param resourcetype 资源类型.
	 * @param resourceid 资源ID.
	 * @param brokername broker名称.
	 * @param pagingInfo 分页信息.
	 * @param amountContainer 总数量容器.
	 * @return 连接列表.
	 */
	@SuppressWarnings("unchecked")
	public List<Connection> findConnections(int sessionid, int resourcetype,
			int resourceid, String brokername,
			PagingInfo pagingInfo, int[] amountContainer) {
		StringBuilder whereClause = new StringBuilder("from Connection where 1 = 1");
		List<Object> params = new ArrayList<Object>();
		if (sessionid > -1) {
			whereClause.append(" and sessionid = ?");
			params.add(sessionid);
		}
		if (numberNotEquals(resourcetype, -1)) {
			whereClause.append(" and resourcetype = ?");
			params.add(resourcetype);
		}
		if (resourceid > -1) {
			whereClause.append(" and resourceid = ?");
			params.add(resourceid);
		}
		if (brokername != null) {
			whereClause.append(" and brokername = ?");
			params.add(brokername);
		}
		Object[] paramsArray = params.toArray();
		count(storageFacade, "idconnection", whereClause, paramsArray, amountContainer);

		List<Connection> connections = pagingFind(storageFacade, whereClause, paramsArray, pagingInfo);
		for (Connection connection : connections) {
			if (connection.getResourcetype() == ResourceAssignment.RESOURCE_TYPE_POOL) {
				connection.setResourcename((String) storageFacade.findFirst(
						"select poolname from DesktopPoolEntity where iddesktoppool = ?",
						connection.getResourceid()));
			} else {
				// Added by Ma Xiaochao.
				connection.setResourcename((String) storageFacade.findFirst(
						"select applicationname from RailApplication where idrailapplication = ?",
						connection.getResourceid()));
			}
		}
		return connections;
}

	public void setStorageFacade(StorageFacade storageFacade) {
		this.storageFacade = storageFacade;
	}

	private Date calculateNewExpire() {
		// TODO Postponed. Expire unsupported for now. All connections are available forever so we give it 1 century time.
		return new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365 * 100);
	}

}
