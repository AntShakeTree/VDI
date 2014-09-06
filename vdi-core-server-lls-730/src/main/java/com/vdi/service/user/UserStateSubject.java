package com.vdi.service.user;

import com.vdi.dao.user.domain.UserMapBridge;

public interface UserStateSubject {
	public void registerUserStateChangeObserver(UsreStateObserver observer,UserMapBridge config);
	public void removeUserStateChangeObserver(UsreStateObserver observer);
}
