package com.vdi.service.user;

import com.vdi.dao.user.domain.LdapConfig;

public interface LdapStateObserver {
	void whenLdapStateChangeUpdateByLdapconfig(LdapStateSubject stateSubject);	
	void setLdapConfig(LdapConfig config);
}
