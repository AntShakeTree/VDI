package com.opzoon.vdi.core.user.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "uservolume")
@Entity
public class UserVolume implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer iduservolume;
	private int userid;
	private int cloudmanagerid;
	private String storageid;
	private String volumename;
	private long totalsize;
	private long usedsize;
	// Attached pool & desktop.
	private Integer desktoppoolid;
	private Integer desktopid;
	private Integer lastdetachtime;
	private long size;
  private Integer volumeid;
  private Integer resourceid;
  private String cloudname;
  private String poolname;
  private String vmname;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIduservolume() {
		return iduservolume;
	}
	public void setIduservolume(Integer iduservolume) {
		this.iduservolume = iduservolume;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getCloudmanagerid() {
		return cloudmanagerid;
	}
	public void setCloudmanagerid(int cloudmanagerid) {
		this.cloudmanagerid = cloudmanagerid;
	}
	public String getStorageid() {
		return storageid;
	}
	public void setStorageid(String storageid) {
		this.storageid = storageid;
	}
	public String getVolumename() {
		return volumename;
	}
	public void setVolumename(String volumename) {
		this.volumename = volumename;
	}
	public long getTotalsize() {
		return totalsize;
	}
	public void setTotalsize(long totalsize) {
		this.totalsize = totalsize;
	}
	public long getUsedsize() {
		return usedsize;
	}
	public void setUsedsize(long usedsize) {
		this.usedsize = usedsize;
	}
	public Integer getDesktoppoolid() {
		return desktoppoolid;
	}
	public void setDesktoppoolid(Integer desktoppoolid) {
		this.desktoppoolid = desktoppoolid;
	}
	public Integer getDesktopid() {
		return desktopid;
	}
	public void setDesktopid(Integer desktopid) {
		this.desktopid = desktopid;
	}
	public Integer getLastdetachtime() {
		return lastdetachtime;
	}
	public void setLastdetachtime(Integer lastdetachtime) {
		this.lastdetachtime = lastdetachtime;
	}
	@Transient
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	@Transient
	public Integer getVolumeid() {
		return volumeid;
	}
	public void setVolumeid(Integer volumeid) {
		this.volumeid = volumeid;
	}
  @Transient
  public Integer getResourceid()
  {
    return resourceid;
  }
  public void setResourceid(Integer resourceid)
  {
    this.resourceid = resourceid;
  }
  @Transient
  public String getCloudname()
  {
    return cloudname;
  }
  public void setCloudname(String cloudname)
  {
    this.cloudname = cloudname;
  }
  @Transient
  public String getPoolname()
  {
    return poolname;
  }
  public void setPoolname(String poolname)
  {
    this.poolname = poolname;
  }
  @Transient
  public String getVmname()
  {
    return vmname;
  }
  public void setVmname(String vmname)
  {
    this.vmname = vmname;
  }
	
}
