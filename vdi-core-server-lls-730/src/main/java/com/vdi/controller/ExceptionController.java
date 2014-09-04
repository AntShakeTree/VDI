/**
 * Project Name:vdi-core-server-lls
 * File Name:TestController.java
 * Package Name:com.vdi.controller
 * Date:2014年8月7日上午8:54:23
 * Copyright (c) 2014 All Rights Reserved.
 *
*/

package com.vdi.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdi.common.ExceptionHandle;
import com.vdi.support.desktop.lls.domain.Head;

@ControllerAdvice
public class ExceptionController {
	  @ExceptionHandler(Exception.class)
	  public @ResponseBody Head handleIOException(Exception ex) {
	    return new Head().setError(ExceptionHandle.changeCustomerExceptionCode(ex));
	  }
}
