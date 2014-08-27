package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ResourceAssignment加上对资源唯一的一列.(视图)
 */
@XmlRootElement(name = "resourceAssignmentWithResourceView")
@Entity
public class ResourceAssignmentWithResourceView implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer idresourceassignment;
	private int resourcetype;
	private int resourceid;
	private String resourcename;
	private int visitortype;
	private int visitorid;
	private int permission;
	private Long resource;
	private String visitorname;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIdresourceassignment() {
		return idresourceassignment;
	}
	public void setIdresourceassignment(Integer idresourceassignment) {
		this.idresourceassignment = idresourceassignment;
	}
	/**
	 * @return 资源类别. 参考{@link ResourceAssignment#RESOURCE_TYPE_POOL}, {@link ResourceAssignment#RESOURCE_TYPE_APPLICATION}.
	 */
	public int getResourcetype() {
		return resourcetype;
	}
	public void setResourcetype(int resourcetype) {
		this.resourcetype = resourcetype;
	}
	/**
	 * @return 资源ID.
	 */
	public int getResourceid() {
		return resourceid;
	}
	public void setResourceid(int resourceid) {
		this.resourceid = resourceid;
	}
	public String getResourcename() {
		return resourcename;
	}
	public void setResourcename(String resourcename) {
		this.resourcename = resourcename;
	}
	/**
	 * @return 访问者类别. 参考{@link ResourceAssignment#RESOURCE_VISITOR_TYPE_USER}, {@link ResourceAssignment#RESOURCE_VISITOR_TYPE_ORGANIZATION}, {@link ResourceAssignment#RESOURCE_VISITOR_TYPE_GROUP}.
	 */
	public int getVisitortype() {
		return visitortype;
	}
	public void setVisitortype(int visitortype) {
		this.visitortype = visitortype;
	}
	/**
	 * @return 访问者ID.
	 */
	public int getVisitorid() {
		return visitorid;
	}
	public void setVisitorid(int visitorid) {
		this.visitorid = visitorid;
	}
	/**
	 * @return 许可类型. 参考{@link ResourceAssignment#RESOURCE_PERMISSION_FORBIDDEN}, {@link ResourceAssignment#RESOURCE_PERMISSION_ALLOWED}.
	 */
	public int getPermission() {
		return permission;
	}
	public void setPermission(int permission) {
		this.permission = permission;
	}
	/**
	 * @return resourceid * 0x1000 + resourcetype.
	 */
	public Long getResource() {
		return resource;
	}
	public void setResource(Long resource) {
		this.resource = resource;
	}
	public String getVisitorname() {
		return visitorname;
	}
	public void setVisitorname(String visitorname) {
		this.visitorname = visitorname;
	}
	
}
