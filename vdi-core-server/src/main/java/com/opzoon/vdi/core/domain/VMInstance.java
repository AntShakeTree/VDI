/**
 * 
 */
package com.opzoon.vdi.core.domain;

import com.opzoon.ohvc.common.anotation.ArrayAnnotation;
import com.opzoon.ohvc.common.anotation.Sub;
import com.opzoon.ohvc.common.anotation.TargetField;

/**
 * @author maxiaochao
 * @version V04 2012-9-06
 */
public class VMInstance extends BaseDomain<VMInstance>{

	private String state;
	private String id;
	@TargetField(target="displayname")
	private String name;
	private String zoneid;
	private String zonename;
	private String templateid;
	private String templatename;
	private String serviceofferingid;
	private String serviceofferingname;
	private String hostid;
	private String host;
	@TargetField(target="vncport")
	private Integer port;

	
	@Sub(name="nic", subName="ipaddress")
	@ArrayAnnotation
	private String ipaddress;



	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the zoneid
	 */
	public String getZoneid() {
		return zoneid;
	}

	/**
	 * @param zoneid the zoneid to set
	 */
	public void setZoneid(String zoneid) {
		this.zoneid = zoneid;
	}

	/**
	 * @return the zonename
	 */
	public String getZonename() {
		return zonename;
	}

	/**
	 * @param zonename the zonename to set
	 */
	public void setZonename(String zonename) {
		this.zonename = zonename;
	}

	/**
	 * @return the templateid
	 */
	public String getTemplateid() {
		return templateid;
	}

	/**
	 * @param templateid the templateid to set
	 */
	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}

	/**
	 * @return the templatename
	 */
	public String getTemplatename() {
		return templatename;
	}

	/**
	 * @param templatename the templatename to set
	 */
	public void setTemplatename(String templatename) {
		this.templatename = templatename;
	}

	/**
	 * @return the serviceofferingid
	 */
	public String getServiceofferingid() {
		return serviceofferingid;
	}

	/**
	 * @param serviceofferingid the serviceofferingid to set
	 */
	public void setServiceofferingid(String serviceofferingid) {
		this.serviceofferingid = serviceofferingid;
	}

	/**
	 * @return the serviceofferingname
	 */
	public String getServiceofferingname() {
		return serviceofferingname;
	}

	/**
	 * @param serviceofferingname the serviceofferingname to set
	 */
	public void setServiceofferingname(String serviceofferingname) {
		this.serviceofferingname = serviceofferingname;
	}

	/**
	 * @return Returns the state.
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state The state to set.
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the ipaddress
	 */
	public String getIpaddress() {
		return ipaddress;
	}

	/**
	 * @param ipaddress the ipaddress to set
	 */
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getHostid() {
		return hostid;
	}

	public void setHostid(String hostid) {
		this.hostid = hostid;
	}
	
}
