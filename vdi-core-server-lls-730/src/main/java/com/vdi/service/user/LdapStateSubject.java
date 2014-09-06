package com.vdi.service.user;

import com.vdi.dao.user.domain.UserMapBridge;

public interface LdapStateSubject {
	public void registerStateChangeObserver(LdapStateObserver observer,UserMapBridge config);
	public void removeStateChangeObserver(LdapStateObserver observer);
}
