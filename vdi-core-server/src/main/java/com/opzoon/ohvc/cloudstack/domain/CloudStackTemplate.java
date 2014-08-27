/**
 * 
 */
package com.opzoon.ohvc.cloudstack.domain;

import com.opzoon.vdi.core.domain.BaseDomain;

/**
 * @author maxiaochao
 * @version V04 2012-9-6
 */
public class CloudStackTemplate extends BaseDomain<CloudStackTemplate> {
    private String command;
    private String templatefilter="self"; //possible values are "featured", "self", "self-executable", "executable", and "community".* featured-templates that are featured and are public* self-templates that have been registered/created by the owner* selfexecutable-templates that have been registered/created by the owner that can be used to deploy a new VM* executable-all templates that can be used to deploy a new VM* community-templates that are public.
    private String displaytext;// the display text of the template. This is
			       // usually used for display purposes.
    private String name;
    private String ostypeid;// the ID of the OS Type that best represents the OS
			    // of this template.true
    private String bits;// 32 or 64 bit false;
    private String details;// Template details in key/value pairs.false
    private Boolean isfeatured;// true if this template is a featured template,
			       // false otherwisefalse;
    private Boolean ispublic;// true if this template is a public template,
			     // false otherwisefalse;
    private Boolean passwordenabled = false;// true if the template supports the
					    // password reset feature; default
					    // is false
    private String requireshvm;
    private String snapshotid;// he ID of the snapshot the template is being
			      // created from. Either this parameter, or
			      // volumeId has to be passed in
    private String tempatetag;// the tag for this template.
    private String url;// Optional, only for baremetal hypervisor. The directory
		       // name where template stored on CIFS server
    private String virtualmachineid;// Optional, VM ID. If this presents, it is
				    // going to create a baremetal template for
				    // VM this ID refers to. This is only for VM
				    // whose hypervisor type is BareMetal
    private String volumeid;// the ID of the disk volume the template is being
			    // created from. Either this parameter, or
			    // snapshotId has to be passed in
    private String id;

    /**
     * @return the displaytext
     */
    public String getDisplaytext() {
	return displaytext;
    }

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @return the ostypeid
     */
    public String getOstypeid() {
	return ostypeid;
    }

    /**
     * @return the bits
     */
    public String getBits() {
	return bits;
    }

    /**
     * @return the details
     */
    public String getDetails() {
	return details;
    }

    /**
     * @return the isfeatured
     */
    public Boolean getIsfeatured() {
	return isfeatured;
    }

    /**
     * @return the ispublic
     */
    public Boolean getIspublic() {
	return ispublic;
    }

    /**
     * @return the passwordenabled
     */
    public Boolean getPasswordenabled() {
	return passwordenabled;
    }

    /**
     * @return the requireshvm
     */
    public String getRequireshvm() {
	return requireshvm;
    }

    /**
     * @return the snapshotid
     */
    public String getSnapshotid() {
	return snapshotid;
    }

    /**
     * @return the tempatetag
     */
    public String getTempatetag() {
	return tempatetag;
    }

    /**
     * @return the url
     */
    public String getUrl() {
	return url;
    }

    /**
     * @return the virtualmachineid
     */
    public String getVirtualmachineid() {
	return virtualmachineid;
    }

    /**
     * @return the volumeid
     */
    public String getVolumeid() {
	return volumeid;
    }

    /**
     * @param displaytext
     *            the displaytext to set
     */
    public void setDisplaytext(String displaytext) {
	this.displaytext = displaytext;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * @param ostypeid
     *            the ostypeid to set
     */
    public void setOstypeid(String ostypeid) {
	this.ostypeid = ostypeid;
    }

    /**
     * @param bits
     *            the bits to set
     */
    public void setBits(String bits) {
	this.bits = bits;
    }

    /**
     * @param details
     *            the details to set
     */
    public void setDetails(String details) {
	this.details = details;
    }

    /**
     * @param isfeatured
     *            the isfeatured to set
     */
    public void setIsfeatured(Boolean isfeatured) {
	this.isfeatured = isfeatured;
    }

    /**
     * @param ispublic
     *            the ispublic to set
     */
    public void setIspublic(Boolean ispublic) {
	this.ispublic = ispublic;
    }

    /**
     * @param passwordenabled
     *            the passwordenabled to set
     */
    public void setPasswordenabled(Boolean passwordenabled) {
	this.passwordenabled = passwordenabled;
    }

    /**
     * @param requireshvm
     *            the requireshvm to set
     */
    public void setRequireshvm(String requireshvm) {
	this.requireshvm = requireshvm;
    }

    /**
     * @param snapshotid
     *            the snapshotid to set
     */
    public void setSnapshotid(String snapshotid) {
	this.snapshotid = snapshotid;
    }

    /**
     * @param tempatetag
     *            the tempatetag to set
     */
    public void setTempatetag(String tempatetag) {
	this.tempatetag = tempatetag;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
	this.url = url;
    }

    /**
     * @param virtualmachineid
     *            the virtualmachineid to set
     */
    public void setVirtualmachineid(String virtualmachineid) {
	this.virtualmachineid = virtualmachineid;
    }

    /**
     * @param volumeid
     *            the volumeid to set
     */
    public void setVolumeid(String volumeid) {
	this.volumeid = volumeid;
    }

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	public CloudStackTemplate setCommand(String command) {
		this.command = command;
		return this;
	}

	/**
	 * @return the templatefilter
	 */
	public String getTemplatefilter() {
		return templatefilter;
	}

	/**
	 * @param templatefilter the templatefilter to set
	 */
	public CloudStackTemplate setTemplatefilter(String templatefilter) {
		this.templatefilter = templatefilter;
		return this;
	}

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
    
}
