/**
 * Project Name:vdi-core-server-lls
 * File Name:DesktopService.java
 * Package Name:com.vdi.dao.service
 * Date:2014年7月30日下午7:51:48
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.facade;

import com.vdi.dao.desktop.domain.Desktop;

public interface DesktopService {
	public void createDesktops(int seed,Desktop desktop) throws Exception;
}
