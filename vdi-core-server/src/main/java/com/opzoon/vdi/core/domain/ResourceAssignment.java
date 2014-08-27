package com.opzoon.vdi.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 资源分配给用户的形式.
 */
@XmlRootElement(name = "resourceAssignment")
@Entity
public class ResourceAssignment implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 资源类别: 虚拟桌面池.
	 */
	public static final int RESOURCE_TYPE_POOL = 0x001;
	/**
	 * 资源类别: 虚拟应用.
	 */
	public static final int RESOURCE_TYPE_APPLICATION = 0x101;

	/**
	 * 访问者类别: 用户.
	 */
	public static final int RESOURCE_VISITOR_TYPE_USER = 0x11;
	/**
	 * 访问者类别: 组织.
	 */
	public static final int RESOURCE_VISITOR_TYPE_ORGANIZATION = 0x12;
	/**
	 * 访问者类别: 组.
	 */
	public static final int RESOURCE_VISITOR_TYPE_GROUP = 0x20;

	/**
	 * 许可类型: 禁止.
	 */
	public static final int RESOURCE_PERMISSION_FORBIDDEN = 0;
	/**
	 * 许可类型: 允许.
	 */
	public static final int RESOURCE_PERMISSION_ALLOWED = 1;
	
	private Integer idresourceassignment;
	private int resourcetype;
	private int resourceid;
	private int visitortype;
	private int visitorid;
	private int permission;

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
	
}
