package com.vdi.dao.user.impl;


import org.springframework.stereotype.Repository;

import com.vdi.dao.suport.JPADaoSuport;
import com.vdi.dao.user.UserDao;
import com.vdi.dao.user.domain.User;
@Repository("userDao")
public class UserDaoImpl extends JPADaoSuport<User> implements UserDao{

}
