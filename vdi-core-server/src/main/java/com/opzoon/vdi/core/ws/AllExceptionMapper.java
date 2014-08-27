package com.opzoon.vdi.core.ws;

import java.io.EOFException;
import java.lang.reflect.UndeclaredThrowableException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.codehaus.jackson.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.vdi.core.domain.Session;
import com.opzoon.vdi.core.facade.CommonException;

public class AllExceptionMapper implements ExceptionMapper<Exception> {
	
	private static final Logger log = LoggerFactory.getLogger(AllExceptionMapper.class);

	@Override
	public Response toResponse(Exception e) {
		log.warn("Exception raised: {} {}", e.getClass(), e.getMessage());
		if(e instanceof UndeclaredThrowableException){
			UndeclaredThrowableException u= (UndeclaredThrowableException)e;
			Throwable t = u.getUndeclaredThrowable();
			if(t instanceof AppstatusRestException){
				log.warn("Exception raised: {} {}", t.getClass(), t.getMessage());
				AppstatusRestException ex=	(AppstatusRestException) t;
				return Response.ok(String.format("{\"head\":{\"error\":%d}}",ex.getErrorCode())).build();
			}
		}
		if(e instanceof AppstatusRestException){
			AppstatusRestException ex=	(AppstatusRestException) e;
			return Response.ok(String.format("{\"head\":{\"error\":%d}}",ex.getErrorCode())).build();
		}else{
		
		if (e instanceof EOFException || e instanceof JsonProcessingException || e instanceof WebApplicationException) {
			
			return Response.ok(String.format("{\"head\":{\"error\":%d}}", Services.err.error(CommonException.BAD_REQUEST))).build();
		} else if (e instanceof AccessDeniedException) {
      Integer invalidatingreasoncode = TicketAuthenticationProvider.INVALID_SESSION_REASON_CODE.get();
      if (invalidatingreasoncode == null)
      {
        invalidatingreasoncode = Session.INVALIDATING_REASON_CODE_DEFAULT;
      }
      return Response.ok(String.format("{\"head\":{\"error\":%d}}", Services.err.error(CommonException.INVALID_SESSION_BASE + invalidatingreasoncode))).build();
		} else {
			log.warn("Unknown exception raised", e);
			return Response.ok(String.format("{\"head\":{\"error\":%d}}", Services.err.error(CommonException.UNKNOWN))).build();
		}}
	}

}
