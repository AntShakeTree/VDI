/**
 * 
 * @Title AppStatusService.java
 * @Description AppStatus模块提供的服务接口实现
 * Copyright: Copyright (c) 2013, Opzoon and/or its affiliates. All rights reserved.
 * OPZOON PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * @author NY
 * @date 2013-11-27 上午10:12:07
 * 
 */
package com.opzoon.appstatus.manager.handle;

/**
 * 
 * AppStatus认证接口
 * @author <a href="mailto:mynameisny@qq.com">Tony Joseph</a>
 * @version 1.0
 *
 */
public interface AuthenticateHandle
{
	/**
	 * AppStatus管理界面认证方法
	 * @param userName 用户名
	 * @param password 密码
	 * @return
	 */
	public abstract boolean checkLogin(String userName, String password);
}
