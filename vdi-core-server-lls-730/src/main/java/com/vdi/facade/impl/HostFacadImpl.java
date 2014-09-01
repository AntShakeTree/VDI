package com.vdi.facade.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.vdi.dao.desktop.ComputePoolDao;
import com.vdi.dao.desktop.HostDao;
import com.vdi.dao.desktop.domain.HostEntity;
import com.vdi.dao.desktop.domain.build.HostBulid;
import com.vdi.facade.HostFacad;
import com.vdi.support.desktop.lls.domain.resource.Host;
import com.vdi.support.desktop.lls.services.AsynchounousHostService;
import com.vdi.support.desktop.lls.services.HostService;
import com.vdi.vo.req.HostIdReq;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.HostResponse;
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
		String taskid =asynchounousHostService.createHost(new HostBulid(entity,new Host()).lls_computPoolIdentity().lls_hostName().lls_ipAddress().BulidLLSDomain());
		Job body=new Job();
		body.setJobid(taskid);
		body.setError(0);
		response.setBody(body);
		return response;
	}
	
	@Override
	public ListHostResponse listHost(HostEntity entity) {
		ListHostResponse response=new ListHostResponse();
		ListHost body=new ListHost();
		List<HostEntity> hostEntities=hostDao.listRequest(entity);
		body.setList(hostEntities);
		response.setBody(body);
		return response;
	}

	@Override
	@Transactional
	public HostResponse getHost(HostIdReq req) {
		Assert.notNull(req);
		HostEntity entity =hostDao.get(HostEntity.class, req.getHostid());
		Host host =hostService.getHost(entity.getHostidentity());
		entity=new HostBulid(entity, host).entity_hostIdentity().entity_hostname().entity_address().entity_status().bulidEntity();
		hostDao.update(entity);
		Header head =new Header();
		HostResponse res =new HostResponse();
		res.setHead(head);
		res.setBody(entity);
		return res;
	}
}
