package com.vdi.service.user.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.vdi.common.ExcecutorUtil;
import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.service.user.LdapStateObserver;
import com.vdi.service.user.LdapStateSubject;

@Service
public class LdapStateSubjectImpl implements LdapStateSubject {
	private static final List<LdapStateObserver> ldapStateObservers = Collections
			.synchronizedList(new ArrayList<LdapStateObserver>());

	@Override
	public void registerStateChangeObserver(LdapStateObserver observer,
			UserMapBridge ldapConfig) {
		synchronized (ldapStateObservers) {
			while(ldapStateObservers.size()!=0){
				try {
					ldapStateObservers.wait();
				} catch (InterruptedException e) {
				}
			}
			observer.setLdapConfig(ldapConfig);
			ldapStateObservers.add(observer);
			ldapStateObservers.notifyAll();
		}
	}

	@Override
	public void removeStateChangeObserver(LdapStateObserver observer) {
		ldapStateObservers.remove(observer);
	}

	@PostConstruct
	public void startNotice() {
		this.start(this);
	}

	private void start(final LdapStateSubjectImpl ldapStateSubjectImpl) {
		ExcecutorUtil.execute(new Runnable() {

			@Override
			public void run() {
				synchronized (ldapStateObservers) {
					for (;;) {
						if (ldapStateObservers.size() == 0) {
							try {
								ldapStateObservers.wait();
							} catch (InterruptedException e) {
							}
						}
						if (ldapStateObservers.size() != 0) {
							LdapStateObserver ldapStateObserver = ldapStateObservers
									.remove(0);
							
							ldapStateObservers.notifyAll();
							try {
								ldapStateObserver.whenLdapStateChangeUpdateByLdapconfig(ldapStateSubjectImpl);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		});

	}
}
