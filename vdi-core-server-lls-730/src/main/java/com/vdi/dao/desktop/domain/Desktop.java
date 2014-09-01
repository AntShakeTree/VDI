package com.vdi.dao.desktop.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.vdi.common.cache.CacheDomain;
import com.vdi.dao.Request;

@Entity
@Table(name = "desktop")
public class Desktop  implements CacheDomain , Request<Desktop>{
	/*
	 * CREATE TABLE `test`.`desktop` ( `idesktop` INT NOT NULL AUTO_INCREMENT,
	 * `vmname` VARCHAR(45) NULL, `poolname` VARCHAR(45) NULL, `poolid` INT
	 * NULL, PRIMARY KEY (`idesktop`)) ENGINE = InnoDB DEFAULT CHARACTER SET =
	 * utf8 COLLATE = utf8_bin;
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO) 
	private Integer idesktop;
	private String vmname;
	private String vmid;
	private Long poolid;
	private String poolname;

	// private String
	

	public String getVmname() {
		return vmname;
	}

	public Integer getIdesktop() {
		return idesktop;
	}

	public void setIdesktop(Integer idesktop) {
		this.idesktop = idesktop;
	}

	public void setVmname(String vmname) {
		this.vmname = vmname;
	}

	public String getVmid() {
		return vmid;
	}

	public void setVmid(String vmid) {
		this.vmid = vmid;
	}

	public Long getPoolid() {
		return poolid;
	}

	public void setPoolid(Long poolid) {
		this.poolid = poolid;
	}

	public String getPoolname() {
		return poolname;
	}

	public void setPoolname(String poolname) {
		this.poolname = poolname;
	}

	@Override
	@JsonIgnore
	public Object getId() {
		// TODO Auto-generated method stub
		return this.getIdesktop();
	}

}
