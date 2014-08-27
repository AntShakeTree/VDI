package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.domain.state.CloudManagerState;
import com.opzoon.vdi.core.fsm.State;
import com.opzoon.vdi.core.fsm.Stateful;

/**
 * TODO Confirm HLD.
 * 虚拟化管理平台状态.
 */
@XmlRootElement(name = "cloudManagerStatus")
@Entity
public class CloudManagerStatus implements Serializable, Stateful {

	private static final long serialVersionUID = 1L;
	
	private Integer idcloudmanager;
  private int phase;
  private int status;

  @Override
  public State loadState()
  {
    return new CloudManagerState(phase, status);
  }

  @Override
  public void mergeState(State state)
  {
    final CloudManagerState cloudManagerState = (CloudManagerState) state;
    phase = cloudManagerState.getPhase();
    status = cloudManagerState.getStatus();
  }

	@Id
	public Integer getIdcloudmanager() {
		return idcloudmanager;
	}
	public void setIdcloudmanager(Integer idcloudmanager) {
		this.idcloudmanager = idcloudmanager;
	}
	public int getPhase()
  {
    return phase;
  }
  public void setPhase(int phase)
  {
    this.phase = phase;
  }
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
