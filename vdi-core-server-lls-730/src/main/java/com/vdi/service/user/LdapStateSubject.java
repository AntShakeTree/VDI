package com.vdi.service.user;

import com.vdi.dao.user.domain.LdapConfig;

public interface LdapStateSubject {
	public void registerStateChangeObserver(LdapStateObserver observer,LdapConfig config);
	public void removeStateChangeObserver(LdapStateObserver observer);
}
