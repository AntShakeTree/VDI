///**   
// * Title: ACLService.java 
// * @Package com.crawler.service.sec 
// * : ACLService.java 
// * @author david   
// * @date 2013-2-7 下午11:04:42 
// * @version 
// */
//package com.vdi.service.user;
//
//import java.util.Set;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.vdi.dao.user.ResourceDao;
//import com.vdi.dao.user.RoleDao;
//import com.vdi.dao.user.domain.Resource;
//import com.vdi.dao.user.domain.Role;
//
///**
// * ClassName: ACLService
// * :
// * @author david
// * @date 2013-2-7 下午11:04:42
// */
//@Service("aclService")
//public class ACLService {
//	@Autowired
//	private ResourceDao resourceDao;
//	@Autowired
//	private RoleDao roleDao;
//
//	@Transactional(readOnly = false)
//	public void authority(String url, String role) {
//		// 可以优化 insert into acl  
//		Resource resource = resourceDao.findOneByKey("url", url);
//		Role r = this.roleDao.findOneByKey("authority", role);
//		Role p=null;
//		if(r!=null&&r.getParent()!=0){
//			 p=  this.roleDao.get(Role.class,r.getParent());
//		}
//		if(resource!=null){
//			Set<Role> ls = resource.getRoles();
//			ls.add(r);
//			if(p!=null){
//				ls.add(p);
//			}
//		}
//		Set<Resource> rs = r.getResources();
//		rs.add(resource);
//		r.setResources(rs);
//		if(p!=null){
//			Set<Resource> pr=p.getResources();
//			p.setResources(pr);
//			this.roleDao.save(p);
//		}
//		this.resourceDao.save(resource);
//		this.roleDao.save(r);
//	}
//	@Transactional(readOnly=false)
//	public void saveRole(Role role){
//		this.resourceDao.save(role);
//	}
//	/** 
//	* Title: ACLService.java del 
//	* : 
//	* @param url
//	* @param authority
//	* @return void
//	* @throws 
//	*/
//	@Transactional(readOnly=false)
//	public void del(String url, String authority) {
//		Resource resource = resourceDao.findOneByKey("url", url);
//		Role r = this.roleDao.findOneByKey("authority", authority);
//		Set<Role> ls = resource.getRoles();
//		ls.remove(r);
//		Set<Resource> rs = r.getResources();
//		rs.remove(resource);
//		r.setResources(rs);
//		this.resourceDao.save(resource);
//		this.roleDao.save(rs);
//	}
//}
