package com.opzoon.vdi.core.ws.admin;

import static com.opzoon.vdi.core.domain.Organization.ORGANIZATION_TYPE_APACHE;
import static com.opzoon.vdi.core.domain.Organization.ORGANIZATION_TYPE_LOCAL;
import static com.opzoon.vdi.core.domain.Organization.ORGANIZATION_TYPE_MSAD;
import static com.opzoon.vdi.core.facade.CommonException.BAD_REQUEST;
import static com.opzoon.vdi.core.facade.CommonException.MULTI_STATUS;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;
import static com.opzoon.vdi.core.util.StringUtils.allInBound;
import static com.opzoon.vdi.core.util.StringUtils.nullToBlankString;
import static com.opzoon.vdi.core.ws.WebServiceHelper.fixListParam;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.domain.Organization;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;
import com.opzoon.vdi.core.facade.OrganizationFacade;
import com.opzoon.vdi.core.ws.Services;
import com.opzoon.vdi.core.ws.Services.CommonList;
import com.opzoon.vdi.core.ws.Services.MultiStatusResponse;
import com.opzoon.vdi.core.ws.Services.NullResponse;
import com.opzoon.vdi.core.ws.Services.Response;
import com.opzoon.vdi.core.ws.WebServiceHelper.Validater;

/**
 * 组织管理业务实现.
 */
public class OrganizationManagement {
	
	private OrganizationFacade organizationFacade;

	public OrganizationIdResponse createOrganization(Organization organization) {
		OrganizationIdResponse response = new OrganizationIdResponse();
		int error = this.validationAndFixOrganization(organization, false);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		try {
			error = organizationFacade.createOrganization(organization);
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		}
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		OrganizationIdWrapper organizationIdWrapper = new OrganizationIdWrapper();
		response.setBody(organizationIdWrapper);
		organizationIdWrapper.setIdorganization(organization.getIdorganization());
		return response;
	}

	public NullResponse deleteOrganization(
			OrganizationIdParam organizationIdParam) {
		NullResponse response = new NullResponse();
		if (organizationIdParam.getIdorganization() < 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		int error = organizationFacade.deleteOrganization(organizationIdParam.getIdorganization());
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		return response;
	}

	public NullResponse updateOrganization(Organization organization) {
		NullResponse response = new NullResponse();
		int error = this.validationAndFixOrganization(organization, true);
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		try {
			error = organizationFacade.updateOrganization(organization);
		} catch (CommonException e) {
			response.getHead().setError(e.getError());
			return response;
		}
		if (error != NO_ERRORS) {
			response.getHead().setError(error);
			return response;
		}
		return response;
	}

	/**
	 * 列举全部组织.
	 * 
	 * @param listOrganizationsParam 列举组织参数.
	 * @return 列举组织响应.
	 */
	public ListOrganizationsResponse listOrganizations(
			ListOrganizationsParam listOrganizationsParam) {
		ListOrganizationsResponse response = new ListOrganizationsResponse();
		if (listOrganizationsParam.getIdorganization() < -1
				|| listOrganizationsParam.getDomainid() < -1
				|| listOrganizationsParam.getParent() < -2) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		// TODO Select.
		if (numberNotEquals(listOrganizationsParam.getOrganizationtype(), -1)
				&& numberNotEquals(listOrganizationsParam.getOrganizationtype(), ORGANIZATION_TYPE_LOCAL)
				&& numberNotEquals(listOrganizationsParam.getOrganizationtype(), ORGANIZATION_TYPE_MSAD)
				&& numberNotEquals(listOrganizationsParam.getOrganizationtype(), ORGANIZATION_TYPE_APACHE)) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		fixListParam(listOrganizationsParam, "idorganization");
		response.setBody(new OrganizationList());
		response.getBody().copyFrom(listOrganizationsParam);
		int[] amountContainer = new int[1];
		response.getBody().setList(organizationFacade.findOrganizations(
				listOrganizationsParam.getIdorganization(),
				listOrganizationsParam.getOrganizationtype(),
				listOrganizationsParam.getDomainid(),
				listOrganizationsParam.getParent(),
				listOrganizationsParam,
				amountContainer));
		response.getBody().setAmount(amountContainer[0]);
		return response;
	}

	public UserIdAndOrganizationListResponse addUserToOrganization(
			UserAndOrganization userAndOrganization) {
		return this.userToOrganization(userAndOrganization, false);
	}

	public UserIdAndOrganizationListResponse deleteUserFromOrganization(
			UserAndOrganization userAndOrganization) {
		return this.userToOrganization(userAndOrganization, true);
	}

	public void setOrganizationFacade(OrganizationFacade organizationFacade) {
		this.organizationFacade = organizationFacade;
	}

	private int validationAndFixOrganization(Organization organization, final boolean forUpdate) {
		return new Validater<Organization>() {
			@Override
			public int validationAndFix(Organization organization) {
				if (forUpdate) {
					if (organization.getIdorganization() == null || organization.getIdorganization() < 0) {
						return BAD_REQUEST;
					}
					if (organization.getOrganizationname() != null) {
						organization.setOrganizationname(organization.getOrganizationname().trim().toLowerCase());
						if (organization.getOrganizationname().length() < 1) {
							return BAD_REQUEST;
						}
					}
					if (organization.getNotes() != null) {
						organization.setNotes(organization.getNotes().trim());
					}
				} else {
					organization.setIdorganization(null);
					if (organization.getDomainid() != 0) {
						return BAD_REQUEST;
					}
					if (organization.getParent() < 0) {
						return BAD_REQUEST;
					}
					organization.setOrganizationname(nullToBlankString(organization.getOrganizationname()).trim().toLowerCase());
					if (organization.getOrganizationname().length() < 1) {
						return BAD_REQUEST;
					}
					organization.setNotes(nullToBlankString(organization.getNotes()));
				}
				if (!allInBound(
						100,
						organization.getOrganizationname(), organization.getNotes())) {
					return BAD_REQUEST;
				}
				return NO_ERRORS;
			}
		}.validationAndFix(organization);
	}

	private UserIdAndOrganizationListResponse userToOrganization(UserAndOrganization userAndOrganization,
			boolean deleting) {
		UserIdAndOrganizationListResponse response = new UserIdAndOrganizationListResponse();
		if (userAndOrganization.getUserid() == null
				|| userAndOrganization.getUserid().length < 1
				|| userAndOrganization.getOrganizationid() == null
				|| userAndOrganization.getOrganizationid().length != 1) {
			response.getHead().setError(BAD_REQUEST);
			return response;
		}
		for (int userid : userAndOrganization.getUserid()) {
			if (userid < 0) {
				response.getHead().setError(MULTI_STATUS);
				for (int organizationid : userAndOrganization.getOrganizationid()) {
					response.addStatus(new UserIdAndOrganizationAndError(userid, organizationid, Services.err.error(BAD_REQUEST)));
				}
				continue;
			}
			int organizationid = userAndOrganization.getOrganizationid()[0];
			if (organizationid < 0) {
				response.addStatus(new UserIdAndOrganizationAndError(userid, organizationid, Services.err.error(BAD_REQUEST)));
				continue;
			}
			int error = organizationFacade.addUserToOrganization(userid, deleting ? Organization.DEFAULT_ORGANIZATION_ID : organizationid);
			if (error != NO_ERRORS) {
				response.addStatus(new UserIdAndOrganizationAndError(userid, organizationid, Services.err.error(error)));
			}
		}
		return response;
	}

	@XmlRootElement(name = "idorganization")
	public static class OrganizationIdParam implements Serializable {

		private static final long serialVersionUID = 1L;

		private int idorganization;
		
		public int getIdorganization() {
			return idorganization;
		}
		public void setIdorganization(int idorganization) {
			this.idorganization = idorganization;
		}
		
	}

	@XmlRootElement(name = "listParam")
	public static class ListOrganizationsParam extends PagingInfo implements Serializable {

		private static final long serialVersionUID = 1L;

		private int idorganization;
		private int organizationtype;
		private int domainid;
		private int parent;
		
		public int getIdorganization() {
			return idorganization;
		}
		public void setIdorganization(int idorganization) {
			this.idorganization = idorganization;
		}
		public int getOrganizationtype() {
			return organizationtype;
		}
		public void setOrganizationtype(int organizationtype) {
			this.organizationtype = organizationtype;
		}
		public int getDomainid() {
			return domainid;
		}
		public void setDomainid(int domainid) {
			this.domainid = domainid;
		}
		public int getParent() {
			return parent;
		}
		public void setParent(int parent) {
			this.parent = parent;
		}
		
	}

	@XmlRootElement(name = "param")
	public static class UserAndOrganization implements Serializable {

		private static final long serialVersionUID = 1L;

		private int[] userid;
		private int[] organizationid;
		
		public int[] getUserid() {
			return userid;
		}
		public void setUserid(int[] userid) {
			this.userid = userid;
		}
		public int[] getOrganizationid() {
			return organizationid;
		}
		public void setOrganizationid(int[] organizationid) {
			this.organizationid = organizationid;
		}
		
	}

	@XmlRootElement(name = "idorganization")
	public static class OrganizationIdWrapper implements Serializable {

		private static final long serialVersionUID = 1L;

		private int idorganization;
		
		public int getIdorganization() {
			return idorganization;
		}
		public void setIdorganization(int idorganization) {
			this.idorganization = idorganization;
		}
		
	}

	@XmlRootElement(name = "response")
	public static class UserIdAndOrganizationListResponse extends MultiStatusResponse<UserIdAndOrganizationAndError> implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private List<UserIdAndOrganizationAndError> body;

		@Override
		public List<UserIdAndOrganizationAndError> getBody() {
			return body;
		}
		@Override
		public void setBody(List<UserIdAndOrganizationAndError> body) {
			this.body = body;
		}
		
	}

	@XmlRootElement(name = "response")
	public static class OrganizationIdResponse extends Response<OrganizationIdWrapper> implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private OrganizationIdWrapper body;

		@Override
		public OrganizationIdWrapper getBody() {
			return body;
		}
		@Override
		public void setBody(OrganizationIdWrapper body) {
			this.body = body;
		}
		
	}

	@XmlRootElement(name = "response")
	public static class ListOrganizationsResponse extends Response<OrganizationList> implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private OrganizationList body;
		
		public OrganizationList getBody() {
			return body;
		}
		public void setBody(OrganizationList body) {
			this.body = body;
		}
		
	}
	
	public static class OrganizationList extends CommonList<Organization> implements Serializable {

		private static final long serialVersionUID = 1L;

		private List<Organization> list;
		
		@Override
		public List<Organization> getList() {
			return list;
		}
		@Override
		public void setList(List<Organization> list) {
			this.list = list;
		}
		
	}
	
	public static class UserIdAndOrganizationAndError implements Serializable {

		private static final long serialVersionUID = 1L;

		private int userid;
		private int organizationid;
		private int error;
		
		public UserIdAndOrganizationAndError(int userid, int organizationid, int error) {
			this.userid = userid;
			this.organizationid = organizationid;
			this.error = error;
		}
		public int getUserid() {
			return userid;
		}
		public void setUserid(int userid) {
			this.userid = userid;
		}
		public int getOrganizationid() {
			return organizationid;
		}
		public void setOrganizationid(int organizationid) {
			this.organizationid = organizationid;
		}
		public int getError() {
			return error;
		}
		public void setError(int error) {
			this.error = error;
		}
		
	}

}
