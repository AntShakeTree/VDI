package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.ohvc.common.anotation.Required;

@XmlRootElement(name = "desktopPool")
public class DesktopPool extends BaseDomain<DesktopPool>  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int DESKTOP_POOL_SOURCE_AUTO = 0;
	public static final int DESKTOP_POOL_SOURCE_MANUAL = 1;

	public static final int DESKTOP_POOL_ASSIGNMENT_FLOAT = 0;
	public static final int DESKTOP_POOL_ASSIGNMENT_FIXED = 1;
	
	private Integer iddesktoppool;
	private String poolname;
	private int vmsource;
	private int assignment;
	private int cloudmanagerid;
	@Required
	private String templateid;
	private String vmnamepatterrn;
	private String computernamepattern;
	private int maxdesktops;
	private int sparedesktops;
	private String notes;
	private boolean link;
	

	public boolean isLink() {
		return link;
	}
	public void setLink(boolean link) {
		this.link = link;
	}
	public Integer getIddesktoppool() {
		return iddesktoppool;
	}
	public void setIddesktoppool(Integer iddesktoppool) {
		this.iddesktoppool = iddesktoppool;
	}
	public String getPoolname() {
		return poolname;
	}
	public void setPoolname(String poolname) {
		this.poolname = poolname;
	}
	public int getVmsource() {
		return vmsource;
	}
	public void setVmsource(int vmsource) {
		this.vmsource = vmsource;
	}
	public int getAssignment() {
		return assignment;
	}
	public void setAssignment(int assignment) {
		this.assignment = assignment;
	}
	public int getCloudmanagerid() {
		return cloudmanagerid;
	}
	public void setCloudmanagerid(int cloudmanagerid) {
		this.cloudmanagerid = cloudmanagerid;
	}
	public String getTemplateid() {
		return templateid;
	}
	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}
	public String getVmnamepatterrn() {
		return vmnamepatterrn;
	}
	public void setVmnamepatterrn(String vmnamepatterrn) {
		this.vmnamepatterrn = vmnamepatterrn;
	}
	public String getComputernamepattern() {
		return computernamepattern;
	}
	public void setComputernamepattern(String computernamepattern) {
		this.computernamepattern = computernamepattern;
	}
	public int getMaxdesktops() {
		return maxdesktops;
	}
	public void setMaxdesktops(int maxdesktops) {
		this.maxdesktops = maxdesktops;
	}
	public int getSparedesktops() {
		return sparedesktops;
	}
	public void setSparedesktops(int sparedesktops) {
		this.sparedesktops = sparedesktops;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
}
