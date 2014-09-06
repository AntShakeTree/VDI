package com.vdi.service.user.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.vdi.common.ExcecutorUtil;
import com.vdi.dao.user.domain.UserMapBridge;
import com.vdi.service.user.UsreStateObserver;
import com.vdi.service.user.UserStateSubject;

@Service
public class UserStateSubjectImpl implements UserStateSubject {
	private static final List<UsreStateObserver> ldapStateObservers = Collections
			.synchronizedList(new ArrayList<UsreStateObserver>());

	@Override
	public void registerUserStateChangeObserver(UsreStateObserver observer,
			UserMapBridge ldapConfig) {
		synchronized (ldapStateObservers) {
//			while(ldapStateObservers.size()!=0){
//				try {
//					ldapStateObservers.wait();
//				} catch (InterruptedException e) {
//				}
//			}
			observer.setUserMapBridge(ldapConfig);
			ldapStateObservers.add(observer);
			ldapStateObservers.notifyAll();
		}
	}

	@Override
	public void removeUserStateChangeObserver(UsreStateObserver observer) {
		ldapStateObservers.remove(observer);
	}

	@PostConstruct
	public void startNotice() {
		this.start(this);
	}

	private void start(final UserStateSubjectImpl ldapStateSubjectImpl) {
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
							UsreStateObserver ldapStateObserver = ldapStateObservers
									.remove(0);
							
//							ldapStateObservers.notifyAll();
							try {
								ldapStateObserver.whenUserStateChangeUpdateByLdapconfig(ldapStateSubjectImpl);
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
