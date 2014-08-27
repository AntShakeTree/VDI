package com.opzoon.appstatus.manager.handle;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opzoon.appstatus.common.ParseJSON;
import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.appstatus.executor.dao.TraceDao;
import com.opzoon.vdi.core.domain.Session;
import com.opzoon.vdi.core.domain.Trace;
import com.opzoon.vdi.core.domain.User;
import com.opzoon.vdi.core.facade.transience.SessionFacade;
import com.opzoon.vdi.core.ws.vo.admin.user.UserIdResponse;
import com.opzoon.vdi.core.ws.vo.admin.user.UserIdsParam;
import com.opzoon.vdi.core.ws.vo.entrance.LoginResponse;

@Aspect
@Component("adviceVerifyUser")
public class AdviceVerifyUser {
	static Logger log = Logger.getLogger(AdviceVerifyUser.class);

	@Pointcut("execution(* com.opzoon.vdi.core.ws.ServicesImpl.*(..)) && @annotation(com.opzoon.vdi.core.domain.Logtrace)")
	public void advice() {
	}

	@Pointcut("execution(* com.opzoon.vdi.core.ws.ServicesImpl.*(..)) && @annotation(com.opzoon.vdi.core.domain.LogtraceAfter)")
	public void after() {
	}

	private @Autowired
	SessionFacade sessionFacade;
	private @Autowired
	TraceDao traceDao;

	@AfterReturning(value = "after()", argNames = "joinPoint,returnvalue", returning = "returnvalue")
	public void after(JoinPoint joinPoint, Object returnvalue) {
		log.debug("adviceVerifyUser :: after");
		try {
			String action = joinPoint.getSignature().getName();
			Date createtime = new Date();
			if (action.equalsIgnoreCase("loginSession")) {
				LoginResponse loginResponse = (LoginResponse) returnvalue;
				Trace trace = new Trace();
				trace.setCreatetime(createtime);
				trace.setAction(action);
				trace.setOperatorid(loginResponse.getBody().getUserid());
				trace.setTargetid(loginResponse.getBody().getUserid());
				trace.setTargetname(loginResponse.getBody().getUsername());
				trace.setOperatorname(loginResponse.getBody().getUsername());
				traceDao.saveTrace(trace);
			} else if (action.contains("create")) {
				Session session = sessionFacade.getCurrentSession();
				int optid = session.getUserid();
				User opt = traceDao.findUser(optid);
				Trace trace = new Trace();
				trace.setCreatetime(createtime);
				trace.setAction(action);
				trace.setOperatorid(opt.getIduser());
				trace.setOperatorname(opt.getUsername());
				UserIdResponse res = (UserIdResponse) returnvalue;
				trace.setTargetid(res.getBody().getIduser());
				User tu = this.traceDao.findUser(res.getBody().getIduser());
				trace.setTargetname(tu.getUsername());
				traceDao.saveTrace(trace);
			} else {
				return;
			}

		} catch (Exception e) {
			log.error("adviceVerifyUser", e);
		}
	}

	@Before(value = "advice()", argNames = "joinPoint")
	public void before(JoinPoint joinPoint) {
		log.debug("adviceVerifyUser :: before");
		try {
			String action = joinPoint.getSignature().getName();
			log.debug("adviceVerifyUser :: ====>" + action);
			Session session =sessionFacade.getCurrentSession();
			int optid = session.getUserid();
			User opt = traceDao.findUser(optid);
			Date createtime = new Date();
		
			if(action.equalsIgnoreCase("logoutSession")){
				Trace trace = new Trace();
				trace.setAction(action);
				trace.setCreatetime(createtime);
				trace.setOperatorid(optid);
				trace.setOperatorname(opt.getUsername());
				trace.setTargetid(optid);
				trace.setTargetname(opt.getUsername());
				traceDao.saveTrace(trace);
				return;
			}
			Object[] args = joinPoint.getArgs();
			String json = "{}";
			if (args != null) {
				Object parameter = args[0];
				json = ParseJSON.getGSON().toJson(parameter);
			}
				if (action.equalsIgnoreCase("deleteUser")) {
				UserIdsParam ids = ParseJSON.fromJson(json, UserIdsParam.class);
				for (int id : ids.getIduser()) {
					User u = this.traceDao.findUser(id);
					Trace trace = new Trace();
					trace.setAction(action);
					trace.setCreatetime(createtime);
					trace.setOperatorid(optid);
					trace.setOperatorname(opt.getUsername());
					trace.setTargetid(id);
					trace.setTargetname(u.getUsername());
					traceDao.saveTrace(trace);
				}
			} else {
				Trace trace = new Trace();
				UerJsonDeseriseableDomain d = ParseJSON.fromJson(json,
							UerJsonDeseriseableDomain.class);
				trace.setAction(action);
				trace.setCreatetime(createtime);
				trace.setOperatorid(optid);
				trace.setOperatorname(opt.getUsername());
				int userid = optid;
				if (d.getIduser() != null) {
					userid = d.getIduser();
				}
				if (d.getUserid() != null) {
					userid = d.getUserid();
				}
				if (StringUtils.isNotEmpty(d.getUsername())) {
					if (d.getUserid() == null && d.getIduser() == null) {

						userid = this.traceDao.findUser(d.getUsername())
								.getIduser();

					}
				}
				trace.setTargetid(userid);
				User taget = this.traceDao.findUser(userid);
				trace.setTargetname(taget.getUsername());
				traceDao.saveTrace(trace);
			}
		} catch (Exception e) {
			log.error("adviceVerifyUser", e);
		}
	}

	public static class UerResponseJsonDeseriseableDomain {
		private UerJsonDeseriseableDomain body;

		public UerJsonDeseriseableDomain getBody() {
			return body;
		}

		public void setBody(UerJsonDeseriseableDomain body) {
			this.body = body;
		}

	}

	public static class UerJsonDeseriseableDomain {
		private Integer userid;
		private Integer iduser;
		private String username;

		public Integer getUserid() {
			return userid;
		}

		public void setUserid(Integer userid) {
			this.userid = userid;
		}

		public Integer getIduser() {
			return iduser;
		}

		public void setIduser(Integer iduser) {
			this.iduser = iduser;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

	}

	public static void main(String[] args) {
		User user = new User();
		// user.setIduser(1);
		String json = ParseJSON.toJson(user);
		UerJsonDeseriseableDomain d = ParseJSON.fromJson(json,
				UerJsonDeseriseableDomain.class);
		System.out.println(d.getIduser());

	}

}
