package com.opzoon.vdi.core.domain;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonProperty;

@Entity
public class RestrictionStrategy implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int UNDEFINED = -1;
	public static final int DISABLED = 0;
	public static final int DISK_READ_ONLY = 1;
	public static final int DISK_READ_WRITE = 2;
	public static final int CLIPBOARD_UPSTREAM = 1;
	public static final int CLIPBOARD_DOWNSTREAM = 2;
	public static final int CLIPBOARD_BISTREAM = 3;
	public static final int AUDIO_DOWNSTREAM = 1;
	public static final int AUDIO_BISTREAM = 2;
	public static final int USER_VOLUME_ALLOWED = 1;

	private Integer idrestrictionstrategy;
	private String strategyname;
	private Integer usbenabled;
	private List<USBListItem> usbclasswhitelist = new LinkedList<USBListItem>();
	private List<USBListItem> usbclassblacklist = new LinkedList<USBListItem>();
	private List<USBListItem> usbdevicewhitelist = new LinkedList<USBListItem>();
	private List<USBListItem> usbdeviceblacklist = new LinkedList<USBListItem>();
	private Integer disk;
	private Integer clipboard;
	private Integer audio;
	private Integer uservolume;
  private Integer strategyid;
  private String notes;
  @JsonProperty("default")
  private int default_;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getIdrestrictionstrategy() {
		return idrestrictionstrategy;
	}
	public void setIdrestrictionstrategy(Integer idrestrictionstrategy) {
		this.idrestrictionstrategy = idrestrictionstrategy;
	}
	public String getStrategyname() {
		return strategyname;
	}
	public void setStrategyname(String strategyname) {
		this.strategyname = strategyname;
	}
	public Integer getClipboard() {
		return clipboard;
	}
	public void setClipboard(Integer clipboard) {
		this.clipboard = clipboard;
	}
	public Integer getUsbenabled() {
		return usbenabled;
	}
	public void setUsbenabled(Integer usbenabled) {
		this.usbenabled = usbenabled;
	}
	@Transient
	public List<USBListItem> getUsbclasswhitelist() {
		return usbclasswhitelist;
	}
	public void setUsbclasswhitelist(List<USBListItem> usbclasswhitelist) {
		this.usbclasswhitelist = usbclasswhitelist;
	}
	@Transient
	public List<USBListItem> getUsbclassblacklist() {
		return usbclassblacklist;
	}
	public void setUsbclassblacklist(List<USBListItem> usbclassblacklist) {
		this.usbclassblacklist = usbclassblacklist;
	}
	@Transient
	public List<USBListItem> getUsbdevicewhitelist() {
		return usbdevicewhitelist;
	}
	public void setUsbdevicewhitelist(List<USBListItem> usbdevicewhitelist) {
		this.usbdevicewhitelist = usbdevicewhitelist;
	}
	@Transient
	public List<USBListItem> getUsbdeviceblacklist() {
		return usbdeviceblacklist;
	}
	public void setUsbdeviceblacklist(List<USBListItem> usbdeviceblacklist) {
		this.usbdeviceblacklist = usbdeviceblacklist;
	}
	public Integer getDisk() {
		return disk;
	}
	public void setDisk(Integer disk) {
		this.disk = disk;
	}
	public Integer getAudio() {
		return audio;
	}
	public void setAudio(Integer audio) {
		this.audio = audio;
	}
	public Integer getUservolume() {
		return uservolume;
	}
	public void setUservolume(Integer uservolume) {
		this.uservolume = uservolume;
	}
	public String getNotes()
  {
    return notes;
  }
  public void setNotes(String notes)
  {
    this.notes = notes;
  }
  @Transient
	public Integer getStrategyid() {
		return strategyid;
	}
	public void setStrategyid(Integer strategyid) {
		this.strategyid = strategyid;
	}
  @Transient
  public int getDefault()
  {
    return default_;
  }
  public void setDefault(int default_)
  {
    this.default_ = default_;
  }
	
}
