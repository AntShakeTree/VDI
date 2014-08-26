/**
 * Project Name:vdi-core-server-lls
 * File Name:a.java
 * Package Name:com.vdi.common
 * Date:2014年7月31日下午2:07:34
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.common;

import java.util.List;

public class PolicyValue {
	private List<Object> keys ;

	public List<Object> getKeys() {
		return keys;
	}

	

	public PolicyValue setKeys(List<Object> keys) {
		this.keys = keys;
		return this;
	}



	public void clear() {
		this.keys = null;
	}

}