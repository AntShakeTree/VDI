package com.vdi.facade;

import com.vdi.dao.user.domain.Organization;
import com.vdi.vo.req.OrganizationIdsReq;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.ListOrganizationResponse;
import com.vdi.vo.res.OrganizationResponse;


public interface OriganazationFacad {
	OrganizationResponse 	createOrganization(Organization organization) throws Exception;
	Header deleteOrganization(OrganizationIdsReq req) throws Exception;
	OrganizationResponse updateOrganization(OrganizationIdsReq req) throws Exception;
	ListOrganizationResponse listOrganizations() ;
	OrganizationResponse 	addUserToOrganization();
	OrganizationResponse deleteUserFromOrganization();
}
