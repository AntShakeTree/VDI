package com.vdi.service.user.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.service.user.LdapStateObserver;
import com.vdi.service.user.LdapStateSubject;
@Service
public class LdapStateSubjectImpl implements LdapStateSubject{
	private static final List<LdapStateObserver> ldapStateObservers = Collections
			.synchronizedList(new ArrayList<LdapStateObserver>());
	private LdapConfig ldapConfig;
	@Override
	public void registerStateChangeObserver(LdapStateObserver observer,LdapConfig ldapConfig) {
		ldapStateObservers.add(observer);
		this.ldapConfig=ldapConfig;
		ldapStateObservers.notifyAll();
	}

	@Override
	public void removeStateChangeObserver(LdapStateObserver observer) {
		ldapStateObservers.remove(observer);
	}
	@PostConstruct
	public void startNotice() {
		synchronized (ldapStateObservers) {
			for (;;) {
				if(ldapStateObservers.size()==0){
					try {
						ldapStateObservers.wait();
					} catch (InterruptedException e) {
					}
				}
				for (LdapStateObserver ldapStateObserver : ldapStateObservers) {
					ldapStateObserver.whenLdapStateChangeUpdateByLdapconfig(this,this.ldapConfig);
				}
			}
		}
	}
}
