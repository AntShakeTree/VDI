package com.opzoon.appstatus.common.exception;

import java.io.EOFException;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonProcessingException;

import com.opzoon.appstatus.common.exception.custom.AppstatusAuthenticateException;
import com.opzoon.appstatus.common.exception.custom.AppstatusClusterDownException;
import com.opzoon.appstatus.common.exception.custom.AppstatusDatabaseException;
import com.opzoon.appstatus.common.exception.custom.AppstatusForbidAddClusterException;
import com.opzoon.appstatus.common.exception.custom.AppstatusResourceNoFoundException;
import com.opzoon.appstatus.common.exception.custom.AppstatusUpdateNotFinishedException;
import com.opzoon.appstatus.common.exception.custom.AppstatusZookeeperException;

/**
 * Appstatus 异常处理类
 * 
 * @author david Date ：2013-11-08
 * @version V0.2.1023（迭代3）
 */
public class AppstatusExceptionHandle {
	private static Logger log = Logger.getLogger(AppstatusRestException.class);

	public static AppstatusRestException throwAppstatusException(Exception e) {
		log.error("<=Appstatus=> AppstatusRestException : ["
				+ e.getMessage() + "]" + "[" + e.getCause() + "]",e);
		return changeCustomerExceptionCode(e);
	}

	public static AppstatusRestException changeCustomerExceptionCode(Exception e) {
		AppstatusExceptionCode ec;
		if (e instanceof NullPointerException
				|| e instanceof java.lang.ClassCastException
				|| e instanceof java.lang.ClassNotFoundException
				|| e instanceof java.lang.NoSuchFieldException
				|| e instanceof java.lang.IndexOutOfBoundsException) {
			ec = AppstatusExceptionCode.AppstatusHypervisorAbnormalException;
		} else if (e instanceof EOFException
				|| e instanceof JsonProcessingException
				|| e instanceof WebApplicationException) {
			ec = AppstatusExceptionCode.AppstatusBadRequestException;
		} else if (e instanceof AppstatusResourceNoFoundException) {
			ec = AppstatusExceptionCode.AppstatusResourceNoFoundException;
		} else if (e instanceof AppstatusDatabaseException) {
			ec = AppstatusExceptionCode.AppstatusDatabaseException;
		} else if (e instanceof AppstatusUpdateNotFinishedException) {
			ec = AppstatusExceptionCode.AppstatusUpdateNotFinishedException;
		} else if (e instanceof AppstatusZookeeperException) {
			ec = AppstatusExceptionCode.AppstatusZookeeperExceptin;
		} else if (e instanceof AppstatusForbidAddClusterException) {
			ec = AppstatusExceptionCode.AppstatusForbidAddClusterException;
		} else if (e instanceof AppstatusAuthenticateException) {
			ec = AppstatusExceptionCode.AppstatusAuthenticateException;
		} else if (e instanceof AppstatusClusterDownException) {
			ec = AppstatusExceptionCode.AppstatusClusterDownException;
		} else {
			ec = AppstatusExceptionCode.AppstatusUnknowException;
		}
		AppstatusRestException restException = new AppstatusRestException();
		restException.setErrorCode(ec.getErrorCode());
		log.error(ec.name()+"  :: appstatusExceptionCode ==>"+ec.getErrorCode());
		return restException;
	}


}
