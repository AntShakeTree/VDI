//
//package com.vdi.service.user.impl;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.vdi.dao.user.AuthoritiesDao;
//import com.vdi.dao.user.domain.Authorities;
//import com.vdi.dao.user.domain.User;
//import com.vdi.service.user.AuthoritiesService;
//
///** 
// * ClassName: AuthoritiesService 
// * :  
// * @author david 
// * @date 2013-2-2 下午4:57:36 
// *  
// */
//@Service("authoritiesService")
//public class AuthoritiesServiceImpl implements AuthoritiesService {
//	@Autowired
//	private AuthoritiesDao authoritiesDao;
//
//
//
//	/** 
//	* Title: AuthoritiesService.java save 
//	* : 
//	* @param authority
//	* @return void
//	* @throws 
//	*/
//	@Transactional(readOnly=false)
//	public void save(Authorities authority) {
//		this.authoritiesDao.save(authority);
//	}
//
//	/** 
//	* Title: AuthoritiesService.java findAllByUser 
//	* : 
//	* @param user
//	* @return
//	* @return List<Authorities>
//	* @throws 
//	*/
//	@Transactional(readOnly=true)
//	public List<Authorities> findAllByUser(User user) {
//		Authorities authorities =new Authorities();
//		authorities.setUser(user);
//		return this.authoritiesDao.listRequest(authorities);
//	}
//
//	/** 
//	* Title: AuthoritiesService.java getAuthority 
//	* : 
//	* @param authority
//	* @return void
//	* @throws 
//	*/
//	@Transactional(readOnly=true)
//	public Authorities getAuthority(String authority) {
//		return this.authoritiesDao.findOneByKey("authority",authority);
//	}
//	
//}
