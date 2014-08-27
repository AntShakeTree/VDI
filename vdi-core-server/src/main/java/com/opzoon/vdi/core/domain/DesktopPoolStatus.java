package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.opzoon.vdi.core.domain.state.DesktopPoolState;
import com.opzoon.vdi.core.fsm.State;
import com.opzoon.vdi.core.fsm.Stateful;

/**
 * 桌面池状态.
 */
@Entity
public class DesktopPoolStatus implements Serializable, Stateful {

	private static final long serialVersionUID = 1L;

	/**
	 * 桌面池状态: 正常服务状态.
	 */
	public static final int DESKTOPPOOL_STATUS_VALID = 128;
	/**
	 * 桌面池状态: 满，不再接纳新的连接.
	 */
	public static final int DESKTOPPOOL_STATUS_FULL = 129;
	/**
	 * 桌面池状态: 维护状态，不可连接.
	 */
	public static final int DESKTOPPOOL_STATUS_MAINTAIN = 130;
	/**
	 * 桌面池状态: Error.
	 */
	public static final int DESKTOPPOOL_STATUS_ERROR = 256;
	
	private Integer iddesktoppool;
  private int phase;
  private int status;
	private int abnormaldesktops;
	private int sparingdesktops;

  @Override
  public State loadState()
  {
    return new DesktopPoolState(
        phase,
        status);
  }
  
  @Override
  public void mergeState(State state)
  {
    final DesktopPoolState desktopPoolState = (DesktopPoolState) state;
    phase = desktopPoolState.getPhase();
    status = desktopPoolState.getStatus();
  }

	@Id
	public Integer getIddesktoppool() {
		return iddesktoppool;
	}
	public void setIddesktoppool(Integer iddesktoppool) {
		this.iddesktoppool = iddesktoppool;
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
	 * @return 桌面池状态. 参考{@link DesktopPoolStatus#DESKTOPPOOL_STATUS_VALID}, {@link DesktopPoolStatus#DESKTOPPOOL_STATUS_MAINTAIN}, {@link DesktopPoolStatus#DESKTOPPOOL_STATUS_FULL}.
	 */
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return 问题桌面数.
	 */
	public int getAbnormaldesktops() {
		return abnormaldesktops;
	}
	public void setAbnormaldesktops(int abnormaldesktops) {
		this.abnormaldesktops = abnormaldesktops;
	}
	/**
	 * @return 当前热备桌面数.
	 */
	public int getSparingdesktops() {
		return sparingdesktops;
	}
	public void setSparingdesktops(int sparingdesktops) {
		this.sparingdesktops = sparingdesktops;
	}
	
}
