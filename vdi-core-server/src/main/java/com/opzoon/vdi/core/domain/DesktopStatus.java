package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import com.opzoon.vdi.core.domain.state.DesktopState;
import com.opzoon.vdi.core.fsm.State;
import com.opzoon.vdi.core.fsm.Stateful;

/**
 * 桌面状态.
 * 
 *  ---------------       /stopDesktop
 *  |             |<------------------------------------------
 *  |    关闭              |       /stopDesktop                        |
 *  |             |<--------------------------                |
 *  ---------------                           |               |
 *       |  |                                 |               |
 *       |  |         ---------------         |               |
 *       |  |         |             |         |               |
 *       |  |         |  启动不服务    |         |               |
 *       |  |         |             |         |               |
 *       |  |         ---------------         |               |
 *       |  |                                 |               |
 *       |  |                          ---------------        |
 *       |  |  /startDesktop           |             |        |
 *       |  -------------------------->|   服务中            |<---------
 *       |                             |             |        | |
 *       |                             ---------------        | |  /logoutSession, /disconnectSession, or session expired
 *       |                                  |  |              | |
 *       |            /establishConnection  |  |        ---------------
 *       |                                  |  |        |             |
 *       |                                  ----------->|   已连接            |
 *       |  startDesktop failed                |        |             |
 *       |                                     |        ---------------
 *       |                          stop/rebootDesktop failed  |         ---------------
 *       |                                     |               --------->|             |
 *       |         establishConnection failed  ------------------------->|  问题桌面         |
 *       --------------------------------------------------------------->|             |
 *                                                                       ---------------
 */
@Entity
public class DesktopStatus implements Serializable, Stateful {

	private static final long serialVersionUID = 1L;

	/**
	 * 桌面状态: 关闭.
	 */
	public static final int DESKTOP_STATUS_STOPPED = 0;
	/**
	 * 桌面状态: 启动但不提供服务.
	 */
	public static final int DESKTOP_STATUS_RUNNING = 1;
	/**
	 * 桌面状态: 可提供RDP服务, 但未连接.
	 */
	public static final int DESKTOP_STATUS_SERVING = 2;
	/**
	 * 桌面状态: 已连接.
	 */
	public static final int DESKTOP_STATUS_CONNECTED = 3;
	/**
	 * 桌面状态: 正在启动.
	 */
	public static final int DESKTOP_STATUS_STARTING = 4;
	/**
	 * 桌面状态: 正在关闭.
	 */
	public static final int DESKTOP_STATUS_STOPPING = 5;
  public static final int DESKTOP_STATUS_UNKNOWN = 253;
	/**
	 * 桌面状态: 正在销毁.
	 */
	public static final int DESKTOP_STATUS_DESTROYING = 254;
	/**
	 * 桌面状态: 正在部署.
	 */
	public static final int DESKTOP_STATUS_PROVISIONING = 255;
	/**
	 * 桌面状态: 问题桌面, 需要管理员处理.
	 */
	public static final int DESKTOP_STATUS_ERROR = 256;
	
	private Integer iddesktop;
  private int phase;
	private int status;
  private int connected;
	private int ownerid;
	private Desktop desktop;

  @Override
  public State loadState()
  {
    return new DesktopState(
        phase,
        status,
        ownerid,
        connected != 0 ? DesktopState.DESKTOP_CONNECTIVITY_CONNECTED : DesktopState.DESKTOP_CONNECTIVITY_STANDBY);
  }
  
  @Override
  public void mergeState(State state)
  {
    final DesktopState desktopState = (DesktopState) state;
    phase = desktopState.getPhase();
    status = desktopState.getStatus();
    ownerid = desktopState.getOwnerid();
    connected = desktopState.getConnectivity() == DesktopState.DESKTOP_CONNECTIVITY_CONNECTED ? 1 : 0;
  }

	@Id
	public Integer getIddesktop() {
		return iddesktop;
	}
	public void setIddesktop(Integer iddesktop) {
		this.iddesktop = iddesktop;
	}
	public int getPhase()
  {
    return phase;
  }
  public void setPhase(int phase)
  {
    this.phase = phase;
  }
  /**
	 * @return 桌面状态. 参考{@link DesktopStatus#DESKTOP_STATUS_STOPPED}, {@link DesktopStatus#DESKTOP_STATUS_RUNNING}, {@link DesktopStatus#DESKTOP_STATUS_SERVING}, {@link DesktopStatus#DESKTOP_STATUS_CONNECTED}, {@link DesktopStatus#DESKTOP_STATUS_ERROR}.
	 */
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getConnected()
  {
    return connected;
  }
  public void setConnected(int connected)
  {
    this.connected = connected;
  }
  /**
	 * @return 分配的用户ID，尚未分配的为-1，浮动池与数据库中的数据有差别.
	 */
	public int getOwnerid() {
		return ownerid;
	}
	public void setOwnerid(int ownerid) {
		this.ownerid = ownerid;
	}
	/**
	 * @return 用于关联查询桌面池ID.
	 */
	@OneToOne(cascade = {}, fetch = FetchType.LAZY, targetEntity = Desktop.class)
	@PrimaryKeyJoinColumn
	public Desktop getDesktop() {
		return desktop;
	}
	public void setDesktop(Desktop desktop) {
		this.desktop = desktop;
	}
	
}
