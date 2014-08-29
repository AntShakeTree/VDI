package com.vdi.facade.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.vdi.dao.desktop.ComputePoolDao;
import com.vdi.dao.desktop.HostDao;
import com.vdi.dao.desktop.domain.ComputePoolEntity;
import com.vdi.dao.desktop.domain.HostBulid;
import com.vdi.dao.desktop.domain.HostEntity;
import com.vdi.facade.HostFacad;
import com.vdi.support.desktop.lls.domain.resource.Host;
import com.vdi.support.desktop.lls.services.AsynchounousHostService;
import com.vdi.support.desktop.lls.services.HostService;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.Job;
import com.vdi.vo.res.JobResponse;
import com.vdi.vo.res.ListHostResponse;
import com.vdi.vo.res.ListHostResponse.ListHost;
@Service
public class HostFacadImpl implements HostFacad {
	private @Autowired HostService hostService;
	private @Autowired AsynchounousHostService asynchounousHostService;
	private @Autowired HostDao hostDao;
	private @Autowired ComputePoolDao computePoolDao;
	@Override
	public JobResponse createHost(HostEntity entity) {
		JobResponse response =new JobResponse();
		Header head=new Header();
		response.setHead(head.setError(0));
		String taskid =asynchounousHostService.createHost(new HostBulid(entity,new Host()).computPoolIdentity().hostName().ipAddress().bulidHost());
		Job body=new Job();
		body.setJobid(taskid);
		body.setError(0);
		response.setBody(body);
		return response;
	}
	
	@Override
	@Transactional(readOnly=false)
	public ListHostResponse listHost(HostEntity entity) {
		ListHostResponse response=new ListHostResponse();
		ListHost body=new ListHost();
		List<HostEntity> hostEntities=hostDao.listRequest(entity);
		
		List<Host> hs= hostService.listHost(new HostBulid(entity,new Host()).hostName().computPoolIdentity().ipAddress().bulidHost());
		
		//～～不是很好实现
		for (Host host : hs) {
			boolean isexists=false;
			for (HostEntity hostEntity : hostEntities) {
				String identity=hostEntity.getHostIdentity();
				if (host.getHostIdentity().equals(identity)) {
					hostEntity.setStatus(new HostBulid(hostEntity, host).hostEntity_status().bulidHostEntity().getStatus());
					hostDao.update(hostEntity);
					isexists=true;
				}
			}
			if (!isexists) {
				
				HostEntity hostEntity=new HostBulid(new HostEntity(), host).hostEntity_hostIdentity().hostEntity_ipaddress().hostEntity_status().bulidHostEntity();
				if(!StringUtils.isEmpty(host.getComputePoolIdentity())){
					ComputePoolEntity ePoolEntity = computePoolDao.findOneByKey("computePoolIdentity", host.getComputePoolIdentity());
					hostEntity.setComputePoolEntity(ePoolEntity);
				}
				hostDao.save(hostEntity);
			}
		}
		
		response.setBody(body);
		return response;
	}
	
}
