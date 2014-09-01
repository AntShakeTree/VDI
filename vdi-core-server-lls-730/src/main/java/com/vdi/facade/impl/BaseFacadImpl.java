package com.vdi.facade.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vdi.common.Session;
import com.vdi.dao.Request;
import com.vdi.dao.desktop.ComputePoolDao;
import com.vdi.dao.desktop.HostDao;
import com.vdi.dao.desktop.StorageDao;
import com.vdi.dao.desktop.domain.ComputePoolEntity;
import com.vdi.dao.desktop.domain.HostEntity;
import com.vdi.dao.desktop.domain.StorageEntity;
import com.vdi.dao.desktop.domain.build.ComputePoolBuild;
import com.vdi.dao.desktop.domain.build.HostBulid;
import com.vdi.dao.desktop.domain.build.StorageBuild;
import com.vdi.dao.user.UserDao;
import com.vdi.dao.user.domain.Domain;
import com.vdi.dao.user.domain.Role;
import com.vdi.dao.user.domain.User;
import com.vdi.facade.BaseFacad;
import com.vdi.support.desktop.lls.domain.resource.ComputePool;
import com.vdi.support.desktop.lls.domain.resource.Host;
import com.vdi.support.desktop.lls.domain.storage.Storage;
import com.vdi.support.desktop.lls.domain.task.Task;
import com.vdi.support.desktop.lls.services.ComputePoolService;
import com.vdi.support.desktop.lls.services.HostService;
import com.vdi.support.desktop.lls.services.StorageService;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.Job;
import com.vdi.vo.res.JobResponse;
import com.vdi.vo.res.ListComputePool;
import com.vdi.vo.res.ListComputePool.ComputePoolList;
@Service
public class BaseFacadImpl implements BaseFacad{
	private @Autowired UserDao userDao;
	private @Autowired ComputePoolService computePoolService;
	private @Autowired ComputePoolDao computePoolDao;
	private @Autowired HostDao hostDao;
	private @Autowired HostService hostService;
	private @Autowired StorageDao storageDao;
	private @Autowired StorageService storageService;
	@Override
	public JobResponse queryJob(Job job) {
		Task task = (Task) Session.getCache(job.getJobid());
		JobResponse res = new JobResponse();
		res.setHead(new Header());
		job.setError(task.getErrorCode());
		job.setProgress(task.getProgress());
		if (task.getErrorCode() != 0) {
			task.setStatus(Job.FAIL);
			Session.removeCache(job.getJobid());
		} else {
			if(task.isTaskFinished(task)){
				job.setStatus( Job.SUCCESS);
				Session.removeCache(job.getJobid());
			}else{
				job.setStatus(Job.RUNNING);
			}
			
		}
		res.setBody(job);
		return res;
	}
	
//	@PostConstruct
//	public void initDate(){
//		List<User> users =userDao.listRequest(new Request<User>() {});
//		if(users==null||users.size()==0){
//			User user =new User();
//			user.setUsername("admin");
//			user.setPassword("111111");
//			user.setEnabled(true);
//			Set<Role> roles=new HashSet<Role>();
//			Role role=new Role();
//			role.setAuthority("ROLE_ADMIN");
//			roles.add(role);
//			user.setRoles(roles);
//		}
//		
//		//computer pool
//		// TODO 单机版 集群一定要删除这段代码
//		List<ComputePoolEntity> cs =computePoolDao.listRequest(new Request<ComputePoolEntity>() {
//		});		
//		List<ComputePool> cps=computePoolService.listComputePool(new ComputePool());
//		for (ComputePool computePool : cps) {
//			String identity=computePool.getComputePoolIdentity();
//			boolean isEx=false;
//			for (ComputePoolEntity computePoolEntity : cs) {
//				if(!StringUtils.isEmpty(identity)){
//					if(computePoolEntity.getComputepoolidentity().equals(identity)){
//						isEx=true;
//						computePoolEntity=new ComputePoolBuild(computePoolEntity, computePool).entity_computePoolIdentity().entity_cpuamount().entity_cpurest().entity_dispatchtype().entity_memoryamount().entity_memoryrest().entity_status().bulidEntity();
//						computePoolDao.update(computePoolEntity);
//					}
//				}	
//			}	
//			if(!isEx){
//				ComputePoolEntity e =new ComputePoolBuild(new ComputePoolEntity(), computePool).entity_computePoolIdentity().entity_computepoolname().entity_cpuamount().entity_cpurest().entity_dispatchtype().entity_memoryamount().entity_memoryrest().entity_status().bulidEntity();
//				e.setIdcomputepool(null);
//				computePoolDao.save(e);
//			}
//		}
//       List<HostEntity> hostEntities=hostDao.listRequest(new Request<HostEntity>() {});
//		
//		List<Host> hs= hostService.listHost(new Host());
//
//				for (Host host : hs) {
//					boolean isexists=false;
//					for (HostEntity hostEntity : hostEntities) {
//						String identity=hostEntity.getHostidentity();
//						if (host.getHostIdentity().equals(identity)) {
//							hostEntity= new HostBulid(hostEntity, host).entity_status().bulidEntity();
//							hostDao.update(hostEntity);
//							isexists=true;
//						}
//					}
//					if (!isexists) {
//						HostEntity hostEntity=new HostBulid(new HostEntity(), host).entity_hostname().entity_hostIdentity().entity_address().entity_status().bulidEntity();
//						if(!StringUtils.isEmpty(host.getComputePoolIdentity())){
//							ComputePoolEntity ePoolEntity = computePoolDao.findOneByKey("computepoolidentity", host.getComputePoolIdentity());
//							hostEntity.setComputePoolEntity(ePoolEntity);
//						}
//						hostDao.save(hostEntity);
//					}
//				}
//			
//		List<StorageEntity> storages =storageDao.listRequest(new Request<StorageEntity>() {});
//		List<Storage> ss=storageService.listStorage(new Storage());
//		for (Storage storage : ss) {
//			boolean isexists=false;
//			for (StorageEntity entity : storages) {
//				String identity=entity.getStorageidentity();
//				if (storage.getHostIdentity().equals(identity)) {
//					entity=new StorageBuild(entity, storage).entity_free().entity_totalize().entity_address().entity_path().entity_status().buildEntity();
//					storageDao.update(entity);
//					isexists=true;
//				}
//			}
//			if (!isexists) {
//				StorageEntity entity=new StorageBuild(new StorageEntity(), storage).entity_address().entity_free().entity_path().entity_status().entity_storageidentity().entity_storagename().entity_totalize().buildEntity();
//				if(!StringUtils.isEmpty(storage.getHostIdentity())){
//					HostEntity hostentity = hostDao.findOneByKey("hostidentity",storage.getHostIdentity());
//					entity.setHost(hostentity);
//				}
//				hostDao.save(entity);
//			}
//		}
//	}
}
