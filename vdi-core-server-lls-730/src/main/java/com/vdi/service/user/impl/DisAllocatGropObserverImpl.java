package com.vdi.service.user.impl;

import org.springframework.stereotype.Service;

import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.service.user.DisAllocatGrouObserver;
import com.vdi.service.user.UserStateSubject;
@Service
public class DisAllocatGropObserverImpl implements DisAllocatGrouObserver {
	private UserMapBridge config;
	@Override
	public void whenUserStateChangeUpdateByLdapconfig(
			UserStateSubject stateSubject) throws Exception {
		switch (config.getStatus()) {
		case UserMapBridge.DIS_ALLOCAT_OU:{
			
		}
			break;
		case UserMapBridge.DIS_ALLOCAT_USER:{
			
		}
		break;
		default:
			break;
		}
	}

	@Override
	public void setUserMapBridge(UserMapBridge config) {
		this.config=config;
	}

}
