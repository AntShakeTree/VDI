package com.opzoon.vdi.core.ws.admin;

import static com.opzoon.vdi.core.facade.CommonException.BAD_REQUEST;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.util.StringUtils.allInBound;
import static com.opzoon.vdi.core.util.StringUtils.nullToBlankString;
import static com.opzoon.vdi.core.ws.WebServiceHelper.fixListParam;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.facade.DomainFacade;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;
import com.opzoon.vdi.core.ws.Services.CommonList;
import com.opzoon.vdi.core.ws.Services.NullResponse;
import com.opzoon.vdi.core.ws.Services.Response;
import com.opzoon.vdi.core.ws.WebServiceHelper.Validater;

/**
 * 用户管理业务实现.
 */
public class DomainManagement {

	private DomainFacade domainFacade;
	
	public ListLDAPConfigsResponse listLDAPConfigs(ListLDAPConfigsParam listLDAPConfigsParam) {
		ListLDAPConfigsResponse response = new ListLDAPConfigsResponse();
		if (listLDAPConfigsParam.getDomainid() < -1
				|| listLDAPConfigsParam.getIdldapconfig() < -1
				|| listLDAPConfigsParam.getSyncinterval() < -1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		fixListParam(listLDAPConfigsParam, "idldapconfig");
		response.setBody(new LDAPConfigList());
		response.getBody().copyFrom(listLDAPConfigsParam);
		int[] amountContainer = new int[1];
		List<com.opzoon.vdi.core.domain.LDAPConfig> domainLdapConfigs = domainFacade.findLDAPConfigs(
				listLDAPConfigsParam.getDomainid(),
				listLDAPConfigsParam.getDomainsearchbase(),
				listLDAPConfigsParam.getIdldapconfig(),
				listLDAPConfigsParam.getSyncinterval(),
				listLDAPConfigsParam,
				amountContainer);
		response.getBody().setAmount(amountContainer[0]);
		List<LDAPConfig> ldapConfigs = new LinkedList<LDAPConfig>();
		for (com.opzoon.vdi.core.domain.LDAPConfig domainLdapConfig : domainLdapConfigs) {
			LDAPConfig ldapConfig = new LDAPConfig();
			ldapConfigs.add(ldapConfig);
			ldapConfig.setIdldapconfig(domainLdapConfig.getIdldapconfig());
			ldapConfig.setDomainservername(domainLdapConfig.getDomain().getDomainservername());
			ldapConfig.setDomainserverport(domainLdapConfig.getDomain().getDomainserverport());
			ldapConfig.setSearchbase(domainLdapConfig.getDomainsearchbase());
			ldapConfig.setSyncinterval(domainLdapConfig.getSyncinterval());
			ldapConfig.setDomainid(domainLdapConfig.getDomain().getIddomain());
			ldapConfig.setDomaintype(domainLdapConfig.getDomain().getDomaintype());
			ldapConfig.setDomainname(domainLdapConfig.getDomain().getDomainname());
			ldapConfig.setDomainnetworkname(domainLdapConfig.getDomain().getDomainnetworkname());
			ldapConfig.setDomainnotes(domainLdapConfig.getDomain().getNotes());
			ldapConfig.setStatus(domainFacade.findLDAPConfigStatus(domainLdapConfig.getIdldapconfig()));
		}
		response.getBody().setList(ldapConfigs);
		return response;
	}

	public NullResponse verifyLDAPConfig(LDAPConfig ldapConfig) {
		NullResponse response = new NullResponse();
		int error = this.validationAndFixLDAPConfig(ldapConfig, false);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		try {
			domainFacade.verifyLDAPConfig(
					ldapConfig.getDomainservername(),
					ldapConfig.getDomainserverport(),
					ldapConfig.getSearchbase(),
					ldapConfig.getBinddn(),
					ldapConfig.getBindpass());
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		}
		return response;
	}
	public NullResponse fleshLDAPConfig(LDAPConfig ldapConfig) {
		NullResponse response = new NullResponse();
		int error=0;
		if(ldapConfig.getDomainid()==0){
			error= BAD_REQUEST;
		}
		if(ldapConfig.getIdldapconfig()==null){
			error= BAD_REQUEST;
		}
		response.getHead().setError(error);
		domainFacade.synLDAP(ldapConfig.getDomainid(), ldapConfig.getIdldapconfig());
		return response;
	}
	public NullResponse addLDAPConfig(LDAPConfig ldapConfig) {
		NullResponse response = new NullResponse();
		int error = this.validationAndFixLDAPConfig(ldapConfig, false);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		try {
			domainFacade.verifyLDAPConfig(
					ldapConfig.getDomainservername(),
					ldapConfig.getDomainserverport(),
					ldapConfig.getSearchbase(),
					ldapConfig.getBinddn(),
					ldapConfig.getBindpass());
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		}
		try {
			domainFacade.persistDomain(
					ldapConfig.getDomainservername(),
					ldapConfig.getDomainserverport(),
					ldapConfig.getSearchbase(),
					ldapConfig.getBinddn(),
					ldapConfig.getBindpass());
		} catch (CommonException e) {}
		domainFacade.fetchDomainLock(ldapConfig.getSearchbase());
		try {
			if (!domainFacade.checkDomainLock(
					ldapConfig.getSearchbase())) {
				response.getHead().setError(CommonException.CONFLICT);
				return response;
			}
			domainFacade.addLDAPConfig(
					ldapConfig.getDomainservername(),
					ldapConfig.getDomainserverport(),
					ldapConfig.getSearchbase(),
					ldapConfig.getBinddn(),
					ldapConfig.getBindpass(),
					ldapConfig.getSyncinterval());
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		} finally {
			domainFacade.releaseDomainLock(
					ldapConfig.getSearchbase());
		}
		return response;
	}

	public NullResponse configLDAPSynchronizingInterval(LDAPConfig ldapConfig) {
		NullResponse response = new NullResponse();
		if (ldapConfig.getSearchbase() == null || ldapConfig.getSearchbase().trim().length() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		try {
			domainFacade.updateLDAPConfigSynchronizingInterval(
					ldapConfig.getSearchbase(),
					ldapConfig.getSyncinterval());
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		}
		return response;
	}

	public NullResponse deleteLDAPConfig(LDAPConfig ldapConfig) {
		NullResponse response = new NullResponse();
		int error = this.validationAndFixLDAPConfig(ldapConfig, true);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		domainFacade.fetchDomainLock(ldapConfig.getSearchbase());
		try {
			if (!domainFacade.checkDomainLock(ldapConfig.getSearchbase())) {
				response.getHead().setError(CommonException.CONFLICT);
				return response;
			}
			domainFacade.deleteLDAPConfig(
					ldapConfig.getDomainservername(),
					ldapConfig.getSearchbase());
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		} finally {
			domainFacade.releaseDomainLock(
					ldapConfig.getSearchbase());
		}
		return response;
	}

	public void setDomainFacade(DomainFacade domainFacade) {
		this.domainFacade = domainFacade;
	}

	private int validationAndFixLDAPConfig(LDAPConfig ldapConfig, final boolean forDeleting) {
		return new Validater<LDAPConfig>() {
			@Override
			public int validationAndFix(LDAPConfig ldapConfig) {
				ldapConfig.setBinddn(nullToBlankString(ldapConfig.getBinddn()).trim());
				ldapConfig.setBindpass(nullToBlankString(ldapConfig.getBindpass()).trim());
				ldapConfig.setDomainservername(nullToBlankString(ldapConfig.getDomainservername()).trim());
				ldapConfig.setSearchbase(nullToBlankString(ldapConfig.getSearchbase()).trim().toLowerCase());
				if (!ldapConfig.getDomainservername().matches("^[\\d\\w\\.\\-_\\$]+$")) {
					return BAD_REQUEST;
				}
				if (ldapConfig.getDomainserverport() < 1
						|| ldapConfig.getDomainserverport() > 65535) {
					return BAD_REQUEST;
				}
				if (ldapConfig.getSyncinterval() < 60) {
					ldapConfig.setSyncinterval(86400);// 60 * 60 * 24
				}
				if (!forDeleting) {
					if (ldapConfig.getBinddn().length() < 1
							|| ldapConfig.getBindpass().length() < 1) {
						return BAD_REQUEST;
					}
				}
				if (ldapConfig.getDomainservername().length() < 1
						|| ldapConfig.getSearchbase().length() < 1) {
					return BAD_REQUEST;
				}
				if (!allInBound(
						100,
						ldapConfig.getBinddn(),
						ldapConfig.getBindpass(),
						ldapConfig.getDomainservername())) {
					return BAD_REQUEST;
				}
        if (ldapConfig.getSearchbase().toLowerCase().indexOf("dc=") < 0) {
          return BAD_REQUEST;
        }
				// FIXME Using parents/children structure in database.
				if (!allInBound(
						255,
						ldapConfig.getSearchbase())) {
					return BAD_REQUEST;
				}
				return NO_ERRORS;
			}
		}.validationAndFix(ldapConfig);
	}

	@XmlRootElement(name = "ldapConfig")
	public static class LDAPConfig implements Serializable {

		private static final long serialVersionUID = 1L;

		private String domainservername;
		private int domainserverport;
		private String searchbase;
		private String binddn;
		private String bindpass;
		private int syncinterval;
		// Output
		private Integer idldapconfig;
		private int domainid;
		private int domaintype;
		private String domainname;
		private String domainnetworkname;
		private String domainnotes;
		private int status;
		
		public String getDomainservername() {
			return domainservername;
		}
		public void setDomainservername(String domainservername) {
			this.domainservername = domainservername;
		}
		public int getDomainserverport() {
			return domainserverport;
		}
		public void setDomainserverport(int domainserverport) {
			this.domainserverport = domainserverport;
		}
		public String getSearchbase() {
			return searchbase;
		}
		public void setSearchbase(String searchbase) {
			this.searchbase = searchbase;
		}
		public String getBinddn() {
			return binddn;
		}
		public void setBinddn(String binddn) {
			this.binddn = binddn;
		}
		public String getBindpass() {
			return bindpass;
		}
		public void setBindpass(String bindpass) {
			this.bindpass = bindpass;
		}
		public int getSyncinterval() {
			return syncinterval;
		}
		public void setSyncinterval(int syncinterval) {
			this.syncinterval = syncinterval;
		}
		// Output
		public Integer getIdldapconfig() {
			return idldapconfig;
		}
		public void setIdldapconfig(Integer idldapconfig) {
			this.idldapconfig = idldapconfig;
		}
		public int getDomainid() {
			return domainid;
		}
		public void setDomainid(int domainid) {
			this.domainid = domainid;
		}
		public int getDomaintype() {
			return domaintype;
		}
		public void setDomaintype(int domaintype) {
			this.domaintype = domaintype;
		}
		public String getDomainname() {
			return domainname;
		}
		public void setDomainname(String domainname) {
			this.domainname = domainname;
		}
		public String getDomainnetworkname() {
			return domainnetworkname;
		}
		public void setDomainnetworkname(String domainnetworkname) {
			this.domainnetworkname = domainnetworkname;
		}
		public String getDomainnotes() {
			return domainnotes;
		}
		public void setDomainnotes(String domainnotes) {
			this.domainnotes = domainnotes;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		
	}

	@XmlRootElement(name = "response")
	public static class ListLDAPConfigsResponse extends Response<LDAPConfigList> implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private LDAPConfigList body;
		
		public LDAPConfigList getBody() {
			return body;
		}
		public void setBody(LDAPConfigList body) {
			this.body = body;
		}
		
	}
	
	public static class LDAPConfigList extends CommonList<LDAPConfig> implements Serializable {

		private static final long serialVersionUID = 1L;

		private List<LDAPConfig> list;

		@Override
		public List<LDAPConfig> getList() {
			return list;
		}
		@Override
		public void setList(List<LDAPConfig> list) {
			this.list = list;
		}
		
	}

	@XmlRootElement(name = "listParam")
	public static class ListLDAPConfigsParam extends PagingInfo implements Serializable {

		private static final long serialVersionUID = 1L;

		private int idldapconfig;
		private int domainid;
		private String domainsearchbase;
		private int syncinterval;
		
		public int getIdldapconfig() {
			return idldapconfig;
		}
		public void setIdldapconfig(int idldapconfig) {
			this.idldapconfig = idldapconfig;
		}
		public int getDomainid() {
			return domainid;
		}
		public void setDomainid(int domainid) {
			this.domainid = domainid;
		}
		public String getDomainsearchbase() {
			return domainsearchbase;
		}
		public void setDomainsearchbase(String domainsearchbase) {
			this.domainsearchbase = domainsearchbase;
		}
		public int getSyncinterval() {
			return syncinterval;
		}
		public void setSyncinterval(int syncinterval) {
			this.syncinterval = syncinterval;
		}
		
	}

}
