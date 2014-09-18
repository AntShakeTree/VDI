package com.vdi.service.user.impl;

import java.util.List;

import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.suport.LdapSupport;
import com.vdi.dao.user.LdapConfigDao;
import com.vdi.dao.user.OrganizationDao;
import com.vdi.dao.user.UserDao;
import com.vdi.dao.user.domain.LdapConfigEntity;
import com.vdi.dao.user.domain.Organization;
import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.service.user.DisAllocatGrouObserver;
import com.vdi.service.user.RemoveUserObserver;
import com.vdi.service.user.SyncDomainService;
import com.vdi.service.user.SyncOrgnazationObserver;
import com.vdi.service.user.SyncUserObserver;
import com.vdi.service.user.UserStateSubject;

@Service
public class SyncDomainServicempl implements SyncDomainService {
	@Autowired
	private OrganizationDao organizationDao;
	@Autowired
	private SyncUserObserver syncUser;
	@Autowired
	private UserDao userDao;
	private UserMapBridge config;
	private @Autowired RemoveUserObserver removeUserObserver;
	private @Autowired DisAllocatGrouObserver disAllocatGroupObserver;
	private @Autowired LdapConfigDao ldapConfigDao;
	private @Autowired SyncOrgnazationObserver syncOrgnazationObserver;
	@Override
	public void whenUserStateChangeUpdateByLdapconfig(UserStateSubject subject) {
		if (config.getStatus() != UserMapBridge.SYNC_DN) {
			return;
		}
		organizationDao.excuteHql(
				"update Organization set status=? where domainguid=?",
				UserMapBridge.SYNC_OU, config.getGuid());
		// userDao.excuteHql("update User set status=? where domainguid=?",
		// LdapConfig.SYNCING,config.getGuid());
		LdapConfigEntity hqle = new LdapConfigEntity();
		hqle.setDomainguid(config.getGuid());
		for (LdapConfigEntity entity : ldapConfigDao.listRequest(hqle)) {
			config.setBase(entity.getBaseurl());
			config.setStatus(UserMapBridge.SYNC_OU);
			subject.registerUserStateChangeObserver(syncOrgnazationObserver, config);
		}
		Organization organization = new Organization();
		organization.setStatus(UserMapBridge.SYNC_OU);
		organization.setDomainguid(config.getGuid());
		List<Organization> os = organizationDao
				.listRequest(organization);
		if (os.size() > 0) {
			organizationDao
					.excuteHql(
							"update Organization set status=? where status =? and domainguid=?",
							UserMapBridge.DELETING,
							UserMapBridge.SYNC_OU, config.getGuid());
			config.setOrganizations(os);
			config.setStatus(UserMapBridge.REMOVE_USER);
			subject.registerUserStateChangeObserver(removeUserObserver,
					config);
			config.setStatus(UserMapBridge.DIS_ALLOCAT_OU);
			subject.registerUserStateChangeObserver(
					disAllocatGroupObserver, config);
		}
	
	}

	@Override
	public void setUserMapBridge(UserMapBridge config) {
		this.config = config;

	}

}
