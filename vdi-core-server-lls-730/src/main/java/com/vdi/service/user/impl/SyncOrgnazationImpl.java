package com.vdi.service.user.impl;

import java.util.List;

import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.suport.LdapSupport;
import com.vdi.dao.user.OrganizationDao;
import com.vdi.dao.user.UserDao;
import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.dao.user.domain.Organization;
import com.vdi.service.desktop.DestroyConnectionObserver;
import com.vdi.service.user.LdapStateSubject;
import com.vdi.service.user.SyncOrgnazationObserver;
import com.vdi.service.user.SyncUserObserver;

@Service
public class SyncOrgnazationImpl implements SyncOrgnazationObserver {
	@Autowired
	private OrganizationDao organizationDao;
	@Autowired
	private SyncUserObserver syncUser;
	@Autowired
	private UserDao userDao;
	private UserMapBridge config;
	private  @Autowired DestroyConnectionObserver destroyConnectionObserver;
	@Override
	public void whenLdapStateChangeUpdateByLdapconfig(LdapStateSubject subject) {
		if (config.getStatus() != UserMapBridge.SYNC) {
			return;
		}
		organizationDao.excuteHql("update Organization set status=? where domainguid=?", UserMapBridge.SYNCING,config.getGuid());
//		userDao.excuteHql("update User set status=? where domainguid=?", LdapConfig.SYNCING,config.getGuid());
		try {
			List<Organization> ls = LdapSupport.findAllOrganazations(config);
			for (Organization organization : ls) {
				Organization dao =organizationDao.findOneByKey("guid", organization.getGuid());
				if(dao==null){
					organizationDao.save(organization);
				}else{
					dao.setStatus(UserMapBridge.NORMAL);
					organizationDao.update(dao);
				}
			}
			Organization organization =new Organization();
			organization.setStatus(UserMapBridge.SYNCING);
			organization.setDomainguid(config.getGuid());
			List<Organization> os =organizationDao.listRequest(organization);
			if(os.size()>0){
				organizationDao.excuteHql("update Organization set status=? where status =? and domainguid=?",UserMapBridge.DELETING, UserMapBridge.SYNCING,config.getGuid());
				config.setOrganizations(os);
				subject.registerStateChangeObserver(destroyConnectionObserver, config);
			}
			//
			config.setStatus(UserMapBridge.SYNC_USER);
			config.setOrganizations(ls);
			subject.registerStateChangeObserver(syncUser, config);
		} catch (NamingException e) {
		}
	}
	@Override
	public void setLdapConfig(UserMapBridge config) {
		this.config=config;
		
	}

}
