package com.vdi.service.user.impl;

import java.util.List;

import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vdi.dao.suport.LdapSupport;
import com.vdi.dao.user.OrganizationDao;
import com.vdi.dao.user.UserDao;
import com.vdi.dao.user.domain.Organization;
import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.service.desktop.DestroyConnectionObserver;
import com.vdi.service.user.DisAllocatGrouObserver;
import com.vdi.service.user.RemoveLdapConfigObserver;
import com.vdi.service.user.RemoveOrganizationObserver;
import com.vdi.service.user.RemoveUserObserver;
import com.vdi.service.user.SyncOrgnazationObserver;
import com.vdi.service.user.SyncUserObserver;
import com.vdi.service.user.UserStateSubject;

@Service
public class SyncOrgnazationImpl implements SyncOrgnazationObserver {
	@Autowired
	private OrganizationDao organizationDao;
	@Autowired
	private SyncUserObserver syncUser;
	@Autowired
	private UserDao userDao;
	private UserMapBridge config;
	private @Autowired RemoveUserObserver removeUserObserver;
	private @Autowired DisAllocatGrouObserver disAllocatGroupObserver;
	
	@Override
	public void whenUserStateChangeUpdateByLdapconfig(UserStateSubject subject) {
		if (config.getStatus() != UserMapBridge.SYNC_OU) {
			return;
		}
		try {
			List<Organization> ls = LdapSupport.findAllOrganazations(config);
			for (Organization organization : ls) {
				Organization dao = organizationDao.findOneByKey("guid",
						organization.getGuid());
				if (dao == null) {
					organization.setStatus(UserMapBridge.NORMAL);
					organizationDao.save(organization);
				} else {
					dao.setStatus(UserMapBridge.NORMAL);
					organizationDao.update(dao);
				}
			}
			//
			config.setStatus(UserMapBridge.SYNC_USER);
			config.setOrganizations(ls);
			subject.registerUserStateChangeObserver(syncUser, config);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setUserMapBridge(UserMapBridge config) {
		this.config = config;

	}

}
