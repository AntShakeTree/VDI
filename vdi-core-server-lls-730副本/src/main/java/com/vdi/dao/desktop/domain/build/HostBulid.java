package com.vdi.dao.desktop.domain.build;

import static org.springframework.util.StringUtils.isEmpty;

import org.springframework.util.Assert;

import com.vdi.dao.desktop.domain.HostEntity;
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

	public HostBulid entity_hostname() {
		if(!isEmpty(host.getHostName())){
			entity.setHostname(host.getHostName());
		}
		return this;
	}
	public HostBulid entity_status(){
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
	public HostBulid entity_address() {
		if(!isEmpty(host.getAddr())){
			entity.setAddress(host.getAddr());
		}
		return this;
	}

	public HostBulid entity_hostIdentity() {
		if(!isEmpty(host.getHostIdentity())){
			entity.setHostidentity(host.getHostIdentity());
		}
		return this;
	}


	public HostBulid lls_hostName() {
		if (!isEmpty(entity.getHostname()))
			host.setHostName(entity.getHostname());
		return this;
	}

	public HostBulid lls_ipAddress() {
		if (!isEmpty(entity.getAddress()))
			host.setAddr(entity.getAddress());
		return this;
	}

	public HostBulid lls_computPoolIdentity() {
		if (entity.getComputePoolEntity() != null
				&& isEmpty(entity.getComputePoolEntity()
						.getComputepoolidentity()))
			host.setComputePoolIdentity(entity.getComputePoolEntity()
					.getComputepoolidentity());
		return this;
	}

	public Host BulidLLSDomain() {
		return this.host;
	}
	public HostEntity bulidEntity() {
		return this.entity;
	}
}
