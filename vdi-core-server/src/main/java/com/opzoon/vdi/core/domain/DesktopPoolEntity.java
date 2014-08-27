package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 虚拟桌面池.
 */
@XmlRootElement(name = "desktopPool")
@Entity
@Table(name = "desktoppool")
public class DesktopPoolEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 桌面来源: 自动池, 虚拟桌面从模板部署.<br />
	 * 当创建自动池时, 系统会自动创建出{@link DesktopPoolEntity#getMaxdesktops()}个桌面.
	 */
	public static final int DESKTOP_POOL_SOURCE_AUTO = 0;
	/**
	 * 桌面来源: 手动池, 虚拟桌面系手动添加的虚拟机.
	 */
	public static final int DESKTOP_POOL_SOURCE_MANUAL = 1;

	/**
	 * 虚拟桌面分配方式: 浮动, 用户注销后不保留分配关系.<br />
	 * 当桌面池的分配方式为浮动时, 系统会自动将桌面的启动数量维持在{@link DesktopPoolEntity#getSparedesktops()}个上.
	 */
	public static final int DESKTOP_POOL_ASSIGNMENT_FLOATING = 0;
	/**
	 * 虚拟桌面分配方式: 固定, 用户将保留首次分配的虚拟桌面.
	 */
	public static final int DESKTOP_POOL_ASSIGNMENT_DEDICATED = 1;

	public static final int PROTOCOL_RDP = 0x001;
	public static final int PROTOCOL_SPICE = 0x002;
	public static final int PROTOCOL_RDP_OVER_HTTP = 0x010;
	public static final int PROTOCOL_SPICE_OVER_HTTP = 0x020;
	public static final int PROTOCOL_RDP_OVER_HTTPS = 0x100;
	public static final int PROTOCOL_SPICE_OVER_HTTPS = 0x200;
	
	private Integer iddesktoppool;
	private String poolname;
	private int vmsource;
	private int assignment;
	private int cloudmanagerid;
	private String templateid;
	private String templatename;
	private String vmnamepatterrn;
	private String computernamepattern;
	private String domainname;
	private String domainbinddn;
	private String domainbindpass;
	private int maxdesktops;
	private int sparedesktops;
	private String notes;
	private String cloudname;
	private int sparingdesktops;
	private int abnormaldesktops;
	private int connecteddesktops;
	private int availableprotocols;
	private int unassignmentdelay;
	private Integer domainid;
	private Integer strategyid;
	private int status;
	private int link=1;
	private int availabledesktops;
	
	
	@Transient
	public int getAvailabledesktops() {
		return availabledesktops;
	}

	public void setAvailabledesktops(int availabledesktops) {
		this.availabledesktops = availabledesktops;
	}

	/**
	 * 创建一个DesktopPool实例并将自己的属性拷贝过去.
	 * 
	 * @return 创建出的DesktopPool.
	 */
	public DesktopPool copy() {
		final DesktopPool pool = new DesktopPool();
		pool.setComputernamepattern(this.getComputernamepattern());
		pool.setMaxdesktops(this.getMaxdesktops());
		pool.setSparedesktops(this.getSparedesktops());
		pool.setVmnamepatterrn(this.getVmnamepatterrn());
		pool.setTemplateid(this.getTemplateid());
		pool.setLink(this.getLink()==1?true:false);
		return pool;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIddesktoppool() {
		return iddesktoppool;
	}
	public void setIddesktoppool(Integer iddesktoppool) {
		this.iddesktoppool = iddesktoppool;
	}



	public int getLink() {
		return link;
	}

	public void setLink(int link) {
		this.link = link;
	}

	/**
	 * @return 桌面池名称.
	 */
	public String getPoolname() {
		return poolname;
	}
	public void setPoolname(String poolname) {
		this.poolname = poolname;
	}
	/**
	 * @return 桌面来源. 参考{@link DesktopPoolEntity#DESKTOP_POOL_SOURCE_AUTO}, {@link DesktopPoolEntity#DESKTOP_POOL_SOURCE_MANUAL}.
	 */
	public int getVmsource() {
		return vmsource;
	}
	public void setVmsource(int vmsource) {
		this.vmsource = vmsource;
	}
	/**
	 * @return 虚拟桌面分配方式. 参考{@link DesktopPoolEntity#DESKTOP_POOL_ASSIGNMENT_FLOATING}, {@link DesktopPoolEntity#DESKTOP_POOL_ASSIGNMENT_DEDICATED}.
	 */
	public int getAssignment() {
		return assignment;
	}
	public void setAssignment(int assignment) {
		this.assignment = assignment;
	}
	/**
	 * @return 虚拟化管理平台ID.
	 */
	public int getCloudmanagerid() {
		return cloudmanagerid;
	}
	public void setCloudmanagerid(int cloudmanagerid) {
		this.cloudmanagerid = cloudmanagerid;
	}
	/**
	 * @return 模板ID (手动池为空).
	 */
	public String getTemplateid() {
		return templateid;
	}
	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}
	/**
	 * @return 模板名称(冗余)(手动池为空).
	 */
	public String getTemplatename() {
		return templatename;
	}
	public void setTemplatename(String templatename) {
		this.templatename = templatename;
	}
	/**
	 * @return 虚拟机名称命名模式.
	 */
	public String getVmnamepatterrn() {
		return vmnamepatterrn;
	}
	public void setVmnamepatterrn(String vmnamepatterrn) {
		this.vmnamepatterrn = vmnamepatterrn;
	}
	/**
	 * @return 计算机名称命名模式.
	 */
	public String getComputernamepattern() {
		return computernamepattern;
	}
	public void setComputernamepattern(String computernamepattern) {
		this.computernamepattern = computernamepattern;
	}
	/**
	 * @return 域名称.
	 */
	public String getDomainname() {
		return domainname;
	}
	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}
	/**
	 * @return 域服务器管理员用户名.
	 */
	public String getDomainbinddn() {
		return domainbinddn;
	}
	public void setDomainbinddn(String domainbinddn) {
		this.domainbinddn = domainbinddn;
	}
	/**
	 * @return 域服务器管理员口令.
	 */
	public String getDomainbindpass() {
		return domainbindpass;
	}
	public void setDomainbindpass(String domainbindpass) {
		this.domainbindpass = domainbindpass;
	}
	/**
	 * @return 最大桌面数.
	 */
	public int getMaxdesktops() {
		return maxdesktops;
	}
	public void setMaxdesktops(int maxdesktops) {
		this.maxdesktops = maxdesktops;
	}
	/**
	 * @return 热备桌面数.
	 */
	public int getSparedesktops() {
		return sparedesktops;
	}
	public void setSparedesktops(int sparedesktops) {
		this.sparedesktops = sparedesktops;
	}
	/**
	 * @return 备注.
	 */
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	/**
	 * @return 虚拟化管理平台名称.
	 */
	@Transient
	public String getCloudname() {
		return cloudname;
	}
	public void setCloudname(String cloudname) {
		this.cloudname = cloudname;
	}
	/**
	 * @return 当前热备桌面数.
	 */
	@Transient
	public int getSparingdesktops() {
		return sparingdesktops;
	}
	public void setSparingdesktops(int sparingdesktops) {
		this.sparingdesktops = sparingdesktops;
	}
	/**
	 * @return 问题桌面数.
	 */
	@Transient
	public int getAbnormaldesktops() {
		return abnormaldesktops;
	}
	public void setAbnormaldesktops(int abnormaldesktops) {
		this.abnormaldesktops = abnormaldesktops;
	}
	/**
	 * @return 当前正在被使用的桌面数.
	 */
	@Transient
	public int getConnecteddesktops() {
		return connecteddesktops;
	}
	public void setConnecteddesktops(int connecteddesktops) {
		this.connecteddesktops = connecteddesktops;
	}
	public int getAvailableprotocols() {
		return availableprotocols;
	}
	public void setAvailableprotocols(int availableprotocols) {
		this.availableprotocols = availableprotocols;
	}
	public int getUnassignmentdelay() {
		return unassignmentdelay;
	}
	public void setUnassignmentdelay(int unassignmentdelay) {
		this.unassignmentdelay = unassignmentdelay;
	}

	/**
	 * @return 创建池时需要加入的域.
	 */
	@Transient
	public Integer getDomainid() {
		return domainid;
	}
	public void setDomainid(Integer domainid) {
		this.domainid = domainid;
	}
	@Transient
	public Integer getStrategyid() {
		return strategyid;
	}
	public void setStrategyid(Integer strategyid) {
		this.strategyid = strategyid;
	}
	@Transient
  public int getStatus()
  {
    return status;
  }

  public void setStatus(int status)
  {
    this.status = status;
  }
	
}
