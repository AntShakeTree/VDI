package com.vdi.facade;

import com.vdi.vo.req.UserReq;
import com.vdi.vo.res.UserResponse;


public interface UserFacad {
	public UserResponse createUser(UserReq req);
	public void updateUser();
	public void updatePassword();
	public void removeUser();
	public void listUsers();
	public void getUser();
}
