package com.vdi.dao.desktop.domain;

import static org.springframework.util.StringUtils.isEmpty;

import org.springframework.util.Assert;

import com.vdi.support.desktop.lls.domain.resource.Host;

public class HostBulid {
	private HostEntity entity;
	private Host host;

	public HostBulid(HostEntity entity, Host host) {
		Assert.notNull(entity);
		Assert.notNull(host);
		this.entity = entity;
		this.host = host;
	}

	public HostBulid hostEntity_hostname() {
		if(!isEmpty(host.getHostName())){
			entity.setHostname(host.getHostName());
		}
		return this;
	}
	public HostBulid hostEntity_status(){
		if(!isEmpty(host.getStatus())){
			switch (host.getStatus()) {
			case Host.DELETING:
				entity.setStatus(HostEntity.DELETING);
				break;
			case Host.DISABLE_CREATEING:
				entity.setStatus(HostEntity.CREATING);
				break;
			case Host.DISCON:
				entity.setStatus(HostEntity.DISCON);
				break;
			case Host.ENABLE_DELETE_FREE:
				entity.setStatus(HostEntity.FREE);
				break;
			case Host.FREEDIS:
				entity.setStatus(HostEntity.FREEDIS);
				break;
			case Host.WORK:
				entity.setStatus(HostEntity.WORK);
				break;
			case Host.WORKDIS:
				entity.setStatus(HostEntity.WORKDIS);
				break;
			case Host.RECOVING:
				entity.setStatus(HostEntity.RECOVING);
				break;
			default:
				break;
			}
		}
		return this;
	}

	// private String totalmem;∂
	public HostBulid hostEntity_ipaddress() {
		return this;
	}

	public HostBulid hostEntity_hostIdentity() {
		return this;
	}


	public HostBulid hostName() {
		if (!isEmpty(entity.getHostname()))
			host.setHostName(entity.getHostname());
		return this;
	}

	public HostBulid ipAddress() {
		if (!isEmpty(entity.getIpaddress()))
			host.setAddr(entity.getIpaddress());
		return this;
	}

	public HostBulid computPoolIdentity() {
		if (entity.getComputePoolEntity() != null
				&& isEmpty(entity.getComputePoolEntity()
						.getComputePoolIdentity()))
			host.setComputePoolIdentity(entity.getComputePoolEntity()
					.getComputePoolIdentity());
		return this;
	}

	public Host bulidHost() {
		return this.host;
	}
	public HostEntity bulidHostEntity() {
		return this.entity;
	}
}
