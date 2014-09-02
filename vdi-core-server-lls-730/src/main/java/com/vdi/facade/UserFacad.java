package com.vdi.facade;

import com.vdi.vo.res.UserResponse;


public interface UserFacad {
	public UserResponse createUser();
	public void updateUser();
	public void updatePassword();
	public void removeUser();
	public void listUsers();
	public void getUser();
}
