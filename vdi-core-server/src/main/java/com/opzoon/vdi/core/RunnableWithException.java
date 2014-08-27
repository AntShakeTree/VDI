package com.opzoon.vdi.core;

import com.opzoon.vdi.core.facade.CommonException;

public interface RunnableWithException {
	
	void run() throws CommonException;

}
