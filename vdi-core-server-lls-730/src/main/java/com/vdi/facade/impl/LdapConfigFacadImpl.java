//package com.vdi.facade.impl;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import javax.naming.NamingException;
//import javax.naming.ldap.LdapContext;
//
//import org.springframework.stereotype.Service;
//import org.springframework.util.Assert;
//
//import com.vdi.common.ErrorCode;
//import com.vdi.common.ExceptionHandle;
//import com.vdi.common.VDIBeanUtils;
//import com.vdi.dao.suport.LdapSupport;
//import com.vdi.dao.user.LdapConfigDao;
//import com.vdi.dao.user.domain.LdapConfig;
//import com.vdi.facade.LdapConfigFacad;
//import com.vdi.service.user.LdapStateObserver;
//import com.vdi.vo.req.LdapConfigIdReq;
//import com.vdi.vo.res.Header;
//import com.vdi.vo.res.LdapConfigResponse;
//import com.vdi.vo.res.ListLdapConfigRespones;
//import com.vdi.vo.res.ListLdapConfigRespones.ListLdapConfig;
//
//@Service
//public class LdapConfigFacadImpl implements LdapConfigFacad {
//	private LdapConfigDao ldapConfigDao;
//	private static final List<LdapStateObserver> ldapStateObservers = Collections
//			.synchronizedList(new ArrayList<LdapStateObserver>());
//
//	@Override
//	public LdapConfigResponse addLDAPConfig(LdapConfig config) {
//		LdapConfigResponse response = new LdapConfigResponse();
//		LdapContext con = null;
//		try {
//			con = LdapSupport.createDirContext(config);
//		} catch (Exception e) {
//			config.setAccesstype(LdapConfig.READONLY);
//			try {
//				con = LdapSupport.createDirContext(config);
//			} catch (Exception e1) {
//				response.getHead().setError(
//						ExceptionHandle.err.error(ErrorCode.LDAP_ABNORMAL));
//				return response;
//			}
//		} finally {
//			if (con != null) {
//				try {
//					con.close();
//				} catch (NamingException e) {
//				}
//			}
//		}
//		config.setStatus(LdapConfig.CREATEING);
//		ldapConfigDao.save(config);
//		response.setBody(config);
//		// publishState(LdapConfig.CREATEING);
//		return response;
//	}
//
//	@Override
//	public Header removeLDAPConfig(LdapConfigIdReq id) {
//		Assert.noNullElements(id.getLdapconfigids());
//		for (Integer iterable_element : id.getLdapconfigids()) {
//			LdapConfig config = ldapConfigDao.get(LdapConfig.class,
//					iterable_element);
//			if (config.getStatus() == LdapConfig.DELETING) {
//				return new Header().setError(0);
//			}
//			config.setStatus(LdapConfig.DELETING);
//			ldapConfigDao.update(config);
//			whenLdapStaeChangeNotify(config);
//		}
//		return new Header();
//	}
//
//	@Override
//	public Header updateLdap(LdapConfig config) {
//		Header header = new Header();
//		if (config.getAccesstype() == LdapConfig.READ_WRITE) {
//			try {
//
//				LdapSupport.createDirContext(config);
//				config.setStatus(LdapConfig.NORMAL);
//			} catch (Exception e) {
//				config.setAccesstype(LdapConfig.READONLY);
//				try {
//					LdapSupport.createDirContext(config);
//					header.setError(ExceptionHandle.err.warn(ErrorCode.LDAP_READER_ONLY));
//				} catch (Exception e1) {
//					header.setError(ExceptionHandle.err
//							.error(ErrorCode.LDAP_ABNORMAL));
//					return header;
//				}
//			}
//		}
//		LdapConfig dao = ldapConfigDao
//				.get(LdapConfig.class, config.getIdldap());
//		VDIBeanUtils.copyPropertiesByNotNull(config, dao, null);
//		ldapConfigDao.update(dao);
//		return header;
//	}
//
//	@Override
//	public Header verifyLdap(LdapConfigIdReq req) {
//		Header header = new Header();
//		LdapConfig config = ldapConfigDao.get(LdapConfig.class,
//				req.getLdapconfigid());
//		LdapContext con = null;
//		try {
//			con = LdapSupport.createDirContext(config);
//			config.setStatus(LdapConfig.NORMAL);
//			ldapConfigDao.update(config);
//		} catch (Exception e) {
//			config.setStatus(LdapConfig.ERROR);
//			ldapConfigDao.update(config);
//			header.setError(ExceptionHandle.err
//					.error(ErrorCode.LDAP_ABNORMAL));
//			return header;
//		} finally {
//			if (con != null) {
//				try {
//					con.close();
//				} catch (NamingException e) {
//				}
//			}
//		}
//		return header;
//	}
//
//	@Override
//	public Header configLDAPSynchronizingInterval(LdapConfigIdReq req) {
//		Header header = this.verifyLdap(req);
//		if (header.getError() != 0) {
//			return header;
//		}
//		// List<>
//
//		return null;
//	}
//
//	@Override
//	public ListLdapConfigRespones listLDAPConfigs(LdapConfig config) {
//		ListLdapConfigRespones respones = new ListLdapConfigRespones();
//		respones.getHead().setError(0);
//		ListLdapConfig body = new ListLdapConfig();
//		body.setList(ldapConfigDao.listRequest(config));
//		respones.setBody(body);
//		return respones;
//	}
//
//	
//
//}
