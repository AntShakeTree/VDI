/**
 * 
 * @Title CrudeLinuxLogin.java
 * @Description AppStatus模块提供的认证服务接口定义
 * Copyright: Copyright (c) 2013, Opzoon and/or its affiliates. All rights reserved.
 * OPZOON PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * @author NY
 * @date 2013-11-27 上午11:02:07
 * 
 */
package com.opzoon.appstatus.manager.handle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.opzoon.appstatus.common.Sha512Crypt;

/**
 * 
 * 粗糙的Linux认证策略，使用用户名和密码在Linux的用户文件中进行查询。
 * @author <a href="mailto:mynameisny@qq.com">Tony Joseph</a>
 * @version 1.0
 *
 */
@Service(value = "authenticateHandle")
public class CrudeLinuxLoginHandle implements AuthenticateHandle
{
	public static final String DEFAULT_FILE_PATH = "/etc/shadow";
	private static Logger logger = Logger.getLogger(CrudeLinuxLoginHandle.class);
	private String filePath;
	
	public CrudeLinuxLoginHandle(){}
	
	public CrudeLinuxLoginHandle(String filePath)
	{
		this.filePath = filePath;
	}
	
	/* (non-Javadoc)
	 * @see com.opzoon.appstatus.manager.handle.AuthenticateHandle#checkLogin(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean checkLogin(String userName, String password)
	{
		boolean result = false;
		if (userName == null || "".equals(userName))
		{
			userName = "root";
		}
		if (filePath == null || "".equals(filePath))
		{
			filePath = DEFAULT_FILE_PATH;
		}
		// 1.根据用户名读取密码文件，取到密码列，形如：$6$ab3lldy5Ts/m1jnC$v6ObNSHkAIqgdmL4qg3w77MEeqU6i9wA51FEfMKFLYRzp7WJqjdD8qt.qaVpeDthx3gmhAOCjTyhSVVdwHalO.
		String originPassword = "";
		try
		{
			originPassword = getOriginPassword(userName, filePath).get("password");
			logger.info("originPassword = " + originPassword);
		}
		catch (IOException e)
		{
			logger.error("Some error occured during get password from Linux password file, see details: " + e);
			return false;
		}
		
		// 2.截取密码，获得salt：$6$ab3lldy5Ts/m1jnC$
		String salt = "";
		if (originPassword != null && originPassword.length() > 20)
		{
			salt = originPassword.substring(0, 20);
			logger.info("salt = " + salt);
		}
		else 
		{
			logger.error("Some error occured during get salt which generate by using the Linux password: " + originPassword);
			return false;
		}
		
		// 3.将用户输入的明文密码与salt作为参数，取得加密后的密码
		String encryptedPassword = "";
		if (salt != null && !"".equals(salt))
		{
			encryptedPassword = Sha512Crypt.Sha512_crypt(password, salt, 0);
			logger.info("encryptedPassword = " + encryptedPassword);
		}
		else 
		{
			logger.error("Some error occured during execute encrypt salt and user password, see detail: password = " + password + ", salt = " + salt);
			return false;
		}
		
		// 4.比较并返回结果
		if (!encryptedPassword.equals("") && originPassword.equals(encryptedPassword))
		{
			result = true;
		}
		return result;
	}
	
	/**
	 * 根据指定的用户名，从密码文件中读取用户名与密码的映射。
	 * @param userName 用户名
	 * @param filePath 密码文件路径
	 * @return 用户名与密码的映射，Key分别为userName和password
	 * @throws IOException
	 */
	private Map<String, String> getOriginPassword(String userName, String filePath) throws IOException
	{
		Map<String, String> resultMap = new HashMap<String, String>();
		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
		while ((line = br.readLine()) != null)
		{
			if (!"".equals(line) && line.contains(":"))
			{
				String[] fields = line.split(":");
				if (fields[0].equals(userName))
				{
					resultMap.put("userName", fields[0]);
					resultMap.put("password", fields[1]);
					break;
				}
			}
		}
		return resultMap;
	}
}