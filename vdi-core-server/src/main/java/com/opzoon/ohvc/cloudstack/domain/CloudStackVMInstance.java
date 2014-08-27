package com.opzoon.ohvc.cloudstack.domain;

import com.opzoon.ohvc.common.anotation.Required;
import com.opzoon.vdi.core.domain.BaseDomain;

/**
 * cloudstackt 实例
 * @author maxiaochao
 * @version V04 2012-9-7
 */
public class CloudStackVMInstance extends BaseDomain<CloudStackVMInstance> {
    // the ID of the virtual machine
    @Required
    private String command;
    private String id;
    private String displayname;// user generated name. The name of the virtua
			       // machine is returned if no displayname exists.
    private String serviceofferingid;// the ID of the service offering for the
				     // virtual machine
    private String group;// the group name of the virtual machine
    private String groupid;// the group ID of the virtual machine
    private String zoneid;// availability zone for the virtual machine
    private String serviceOfferingId;
    private String templateId; 	//for create
    private String templateid;	//for get instance
    private String zoneId;
    private String name;
    private String hypervisor;
    private String userdata;

//    /**
//     * @return the jobstatus 2012-9-7
//     */
//    public String getJobstatus() {
//	return jobstatus;
//    }

    /**
     * @return Returns the zoneId.
     */
    public String getZoneId() {
	return zoneId;
    }

    /**
     * @param zoneId
     *            The zoneId to set.
     */
    public void setZoneId(String zoneId) {
	this.zoneId = zoneId;
    }

	/**
	 * @return the templateId
	 */
	public String getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	/**
     * @return Returns the serviceOfferingId.
     */
    public String getServiceOfferingId() {
	return serviceOfferingId;
    }

    /**
     * @param serviceOfferingId
     *            The serviceOfferingId to set.
     */
    public void setServiceOfferingId(String serviceOfferingId) {
	this.serviceOfferingId = serviceOfferingId;
    }

//    /**
//     * @return the jobid 2012-9-7
//     */
//    public String getJobid() {
//	return jobid;
//    }
//
//    /**
//     * @param jobstatus
//     *            the jobstatus to set 2012-9-7
//     */
//    public void setJobstatus(String jobstatus) {
//	this.jobstatus = jobstatus;
//    }
//
//    /**
//     * @param jobid
//     *            the jobid to set 2012-9-7
//     */
//    public void setJobid(String jobid) {
//	this.jobid = jobid;
//    }

    /**
     * @return the id 2012-9-7
     */
    public String getId() {
	return id;
    }

//    /**
//     * @return the account 2012-9-7
//     */
//    public String getAccount() {
//	return account;
//    }
//
//    /**
//     * @return the cpunumber 2012-9-7
//     */
//    public Integer getCpunumber() {
//	return cpunumber;
//    }
//
//    /**
//     * @return the cpuspeed 2012-9-7
//     */
//    public String getCpuspeed() {
//	return cpuspeed;
//    }
//
//    /**
//     * @return the cpuused 2012-9-7
//     */
//    public String getCpuused() {
//	return cpuused;
//    }
//
//    /**
//     * @return the created 2012-9-7
//     */
//    public String getCreated() {
//	return created;
//    }

    /**
     * @return the displayname 2012-9-7
     */
    public String getDisplayname() {
	return displayname;
    }

//    /**
//     * @return the domain 2012-9-7
//     */
//    public String getDomain() {
//	return domain;
//    }
//
//    /**
//     * @return the domainid 2012-9-7
//     */
//    public String getDomainid() {
//	return domainid;
//    }

    /**
     * @return the serviceofferingid 2012-9-7
     */
    public String getServiceofferingid() {
	return serviceofferingid;
    }

//    /**
//     * @return the forvirtualnetwork 2012-9-7
//     */
//    public String getForvirtualnetwork() {
//	return forvirtualnetwork;
//    }

    /**
     * @return the group 2012-9-7
     */
    public String getGroup() {
	return group;
    }

    /**
     * @return the groupid 2012-9-7
     */
    public String getGroupid() {
	return groupid;
    }

//    /**
//     * @return the guestosid 2012-9-7
//     */
//    public String getGuestosid() {
//	return guestosid;
//    }
//
//    /**
//     * @return the haenable 2012-9-7
//     */
//    public String getHaenable() {
//	return haenable;
//    }

    /**
     * @return the zoneid 2012-9-7
     */
    public String getZoneid() {
	return zoneid;
    }

    /**
     * @param id
     *            the id to set 2012-9-7
     */
    public CloudStackVMInstance setId(String id) {
	this.id = id;
	return this;
    }

//    /**
//     * @param account
//     *            the account to set 2012-9-7
//     */
//    public void setAccount(String account) {
//	this.account = account;
//    }
//
//    /**
//     * @param cpunumber
//     *            the cpunumber to set 2012-9-7
//     */
//    public void setCpunumber(Integer cpunumber) {
//	this.cpunumber = cpunumber;
//    }

//    /**
//     * @param cpuspeed
//     *            the cpuspeed to set 2012-9-7
//     */
//    public void setCpuspeed(String cpuspeed) {
//	this.cpuspeed = cpuspeed;
//    }
//
//    /**
//     * @param cpuused
//     *            the cpuused to set 2012-9-7
//     */
//    public void setCpuused(String cpuused) {
//	this.cpuused = cpuused;
//    }

//    /**
//     * @param created
//     *            the created to set 2012-9-7
//     */
//    public void setCreated(String created) {
//	this.created = created;
//    }

    /**
     * @param displayname
     *            the displayname to set 2012-9-7
     */
    public void setDisplayname(String displayname) {
	this.displayname = displayname;
    }

    /**
     * @param domain
     *            the domain to set 2012-9-7
     */
//    public void setDomain(String domain) {
//	this.domain = domain;
//    }
//
//    /**
//     * @param domainid
//     *            the domainid to set 2012-9-7
//     */
//    public void setDomainid(String domainid) {
//	this.domainid = domainid;
//    }

    /**
     * @param serviceofferingid
     *            the serviceofferingid to set 2012-9-7
     */
    public void setServiceofferingid(String serviceofferingid) {
	this.serviceofferingid = serviceofferingid;
    }

//    /**
//     * @param forvirtualnetwork
//     *            the forvirtualnetwork to set 2012-9-7
//     */
//    public void setForvirtualnetwork(String forvirtualnetwork) {
//	this.forvirtualnetwork = forvirtualnetwork;
//    }

    /**
     * @param group
     *            the group to set 2012-9-7
     */
    public void setGroup(String group) {
	this.group = group;
    }

    /**
     * @param groupid
     *            the groupid to set 2012-9-7
     */
    public CloudStackVMInstance setGroupid(String groupid) {
	this.groupid = groupid;
	return this;
    }

//    /**
//     * @param guestosid
//     *            the guestosid to set 2012-9-7
//     */
//    public void setGuestosid(String guestosid) {
//	this.guestosid = guestosid;
//    }

//    /**
//     * @param haenable
//     *            the haenable to set 2012-9-7
//     */
//    public void setHaenable(String haenable) {
//	this.haenable = haenable;
//    }

    /**
     * @param zoneid
     *            the zoneid to set 2012-9-7
     */
    public void setZoneid(String zoneid) {
	this.zoneid = zoneid;
    }

    /**
     * @return Returns the command.
     */
    public String getCommand() {
	return command;
    }

    /**
     * @param command
     *            The command to set.
     */
    public CloudStackVMInstance setCommand(String command) {
	this.command = command;
	return this;
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
	 * @return the hypervisor
	 */
	public String getHypervisor() {
		return hypervisor;
	}

	/**
	 * @param hypervisor the hypervisor to set
	 */
	public void setHypervisor(String hypervisor) {
		this.hypervisor = hypervisor;
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
	 * @return the userdata
	 */
	public String getUserdata() {
		return userdata;
	}

	/**
	 * @param userdata the userdata to set
	 */
	public void setUserdata(String userdata) {
		this.userdata = userdata;
	}
}
