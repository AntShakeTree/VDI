package com.vdi.dao.user.domain.bulid;

import com.vdi.dao.user.domain.Role;

public class RoleBuild  {
	private Role role=new Role();
	
	public RoleBuild authority(String authority){
		role.setAuthority(authority);
		return this;
	}
	public RoleBuild parent(int praent){
		this.role.setParent(praent);
		return this;
	}
	public Role bulidEntity(){
		return this.role;
	}
}
