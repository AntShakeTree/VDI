package com.opzoon.vdi.core.ws;

import static com.opzoon.vdi.core.facade.CommonException.MULTI_STATUS;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.junit.Ignore;

import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.appstatus.domain.UserParam;
import com.opzoon.appstatus.domain.req.IPconfigReq;
import com.opzoon.appstatus.domain.req.NodeAddressList;
import com.opzoon.appstatus.domain.req.NodeReq;
import com.opzoon.appstatus.domain.res.AppStatusResponse;
import com.opzoon.vdi.core.app.request.TraceReq;
import com.opzoon.vdi.core.app.response.ListTraceRes;
import com.opzoon.vdi.core.domain.CloudManagerEntity;
import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.Group;
import com.opzoon.vdi.core.domain.License;
import com.opzoon.vdi.core.domain.LicenseServer;
import com.opzoon.vdi.core.domain.Organization;
import com.opzoon.vdi.core.domain.RestrictionStrategy;
import com.opzoon.vdi.core.domain.User;
import com.opzoon.vdi.core.domain.UserVolume;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;
import com.opzoon.vdi.core.util.Errors;
import com.opzoon.vdi.core.ws.DesktopUsage.ConnectionTicketParam;
import com.opzoon.vdi.core.ws.DesktopUsage.ForceWrapper;
import com.opzoon.vdi.core.ws.DesktopUsage.GetNotificationResponse;
import com.opzoon.vdi.core.ws.DesktopUsage.GetRestrictionStrategyResponse;
import com.opzoon.vdi.core.ws.DesktopUsage.ListResourcesResponse;
import com.opzoon.vdi.core.ws.DesktopUsage.ResourceParam;
import com.opzoon.vdi.core.ws.DesktopUsage.ResourceTypeAndId;
import com.opzoon.vdi.core.ws.DesktopUsage.ResourceTypeParam;
import com.opzoon.vdi.core.ws.DomainUsage.ListDomainsResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.AddDesktopByIPAddressResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.AsyncJobIdParam;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.CloudManagerEntityIdParam;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.CloudManagerEntityIdResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ConnectionIdParam;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.DesktopIdParam;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.DesktopIds;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.DesktopListResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.DesktopPoolAndIPAddress;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.DesktopPoolAndSize;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.DesktopPoolIdParam;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.DesktopPoolIdResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.DesktopPoolListResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.EstablishConnectionAsyncJobResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.GroupAndDesktopPool;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.GroupAndPoolListResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListAssignmentsParam;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListAssignmentsResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListAsyncJobsParam;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListAsyncJobsResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListCloudDriversResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListCloudManagersParam;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListCloudManagersResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListConnectionsParam;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListConnectionsResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListDesktopPoolsParam;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListDesktopPoolsResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListDesktopsParam;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListDesktopsResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListOwnedVolumesResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListRestrictionStrategiesParam;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListRestrictionStrategiesResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListRestrictionStrategyAssignmentsParam;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListRestrictionStrategyAssignmentsResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListTemplatesParam;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListTemplatesResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.ListUserVolumesResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.OrganizationAndDesktopPool;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.OrganizationAndPoolListResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.RestrictionStrategyAssignmentParam;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.RestrictionStrategyListParam;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.RestrictionStrategyListResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.RestrictionStrategyResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.StrategyAssignmentResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.UserAndDesktopPool;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.UserAndPoolListResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement.UserIdAndVolumeIdParam;
import com.opzoon.vdi.core.ws.admin.DomainManagement.LDAPConfig;
import com.opzoon.vdi.core.ws.admin.DomainManagement.ListLDAPConfigsParam;
import com.opzoon.vdi.core.ws.admin.DomainManagement.ListLDAPConfigsResponse;
import com.opzoon.vdi.core.ws.admin.GroupManagement.GroupIdParam;
import com.opzoon.vdi.core.ws.admin.GroupManagement.GroupIdResponse;
import com.opzoon.vdi.core.ws.admin.GroupManagement.GroupListResponse;
import com.opzoon.vdi.core.ws.admin.GroupManagement.ListGroupsParam;
import com.opzoon.vdi.core.ws.admin.GroupManagement.ListGroupsResponse;
import com.opzoon.vdi.core.ws.admin.GroupManagement.OrganizationAndGroup;
import com.opzoon.vdi.core.ws.admin.GroupManagement.OrganizationIdAndGroupListResponse;
import com.opzoon.vdi.core.ws.admin.GroupManagement.UserAndGroup;
import com.opzoon.vdi.core.ws.admin.GroupManagement.UserIdAndGroupListResponse;
import com.opzoon.vdi.core.ws.admin.LicenseMangement.FingerPrintResponse;
import com.opzoon.vdi.core.ws.admin.LicenseMangement.LicenseIdParam;
import com.opzoon.vdi.core.ws.admin.LicenseMangement.LicenseResponse;
import com.opzoon.vdi.core.ws.admin.LicenseMangement.ListLicenseResponse;
import com.opzoon.vdi.core.ws.admin.LicenseServerMangement.GuidResponse;
import com.opzoon.vdi.core.ws.admin.LicenseServerMangement.LicenseServerIdParam;
import com.opzoon.vdi.core.ws.admin.LicenseServerMangement.LicenseServerResponse;
import com.opzoon.vdi.core.ws.admin.LicenseServerMangement.ListLicenseServerResponse;
import com.opzoon.vdi.core.ws.admin.OrganizationManagement.ListOrganizationsParam;
import com.opzoon.vdi.core.ws.admin.OrganizationManagement.ListOrganizationsResponse;
import com.opzoon.vdi.core.ws.admin.OrganizationManagement.OrganizationIdParam;
import com.opzoon.vdi.core.ws.admin.OrganizationManagement.OrganizationIdResponse;
import com.opzoon.vdi.core.ws.admin.OrganizationManagement.UserAndOrganization;
import com.opzoon.vdi.core.ws.admin.OrganizationManagement.UserIdAndOrganizationListResponse;
import com.opzoon.vdi.core.ws.admin.SystemManagement.ConfigureSystemParam;
import com.opzoon.vdi.core.ws.admin.SystemManagement.ListNetworkAdaptersResponse;
import com.opzoon.vdi.core.ws.admin.SystemManagement.NetworkAdapter;
import com.opzoon.vdi.core.ws.vo.admin.user.ListSessionsParam;
import com.opzoon.vdi.core.ws.vo.admin.user.ListSessionsResponse;
import com.opzoon.vdi.core.ws.vo.admin.user.ListStandardRSAKeysParam;
import com.opzoon.vdi.core.ws.vo.admin.user.ListStandardRSAKeysResponse;
import com.opzoon.vdi.core.ws.vo.admin.user.ListUsersParam;
import com.opzoon.vdi.core.ws.vo.admin.user.ListUsersResponse;
import com.opzoon.vdi.core.ws.vo.admin.user.PrivilegeParam;
import com.opzoon.vdi.core.ws.vo.admin.user.RSAKeyIdsResponse;
import com.opzoon.vdi.core.ws.vo.admin.user.SessionIdWrapper;
import com.opzoon.vdi.core.ws.vo.admin.user.StandardRSAKeyIds;
import com.opzoon.vdi.core.ws.vo.admin.user.StandardRSAKeys;
import com.opzoon.vdi.core.ws.vo.admin.user.UserIdParam;
import com.opzoon.vdi.core.ws.vo.admin.user.UserIdResponse;
import com.opzoon.vdi.core.ws.vo.admin.user.UserIdsParam;
import com.opzoon.vdi.core.ws.vo.admin.user.UserIdsResponse;
import com.opzoon.vdi.core.ws.vo.entrance.ListAvailableAuthenticationMethodsResponse;
import com.opzoon.vdi.core.ws.vo.entrance.LoginInfo;
import com.opzoon.vdi.core.ws.vo.entrance.LoginResponse;
import com.opzoon.vdi.core.ws.vo.entrance.NewPasswordWrapper;

/**
 * WebServices鎬绘帴鍙?
 */
@Path("/")
public interface Services {
	
	Errors err = Errors.newInstance(2, 1);
	
	/**
	 * VDICore鑷畾涔夌殑榛樿Content-Type.
	 */
	String DEFAULT_CONTENT_TYPE = "application/opzoon-v4+json";

	// 浠ヤ笅涓鸿凯浠?鍐呭.

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listAvailableAuthenticationMethods")
	ListAvailableAuthenticationMethodsResponse listAvailableAuthenticationMethods();
	
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/loginSession")
	LoginResponse loginSession(LoginInfo loginInfo);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/logoutSession")
	NullResponse logoutSession();

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/createUser")
	UserIdResponse createUser(User user);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/deleteUser")
	UserIdsResponse deleteUser(UserIdsParam userIdsParam);

	  @POST
	  @Produces(DEFAULT_CONTENT_TYPE)
	  @Consumes(DEFAULT_CONTENT_TYPE)
	  @Path("/importStandardRSAKeys")
	  RSAKeyIdsResponse importStandardRSAKeys(StandardRSAKeys standardRSAKeys);

	  @POST
	  @Produces(DEFAULT_CONTENT_TYPE)
	  @Consumes(DEFAULT_CONTENT_TYPE)
	  @Path("/assignStandardRSAKeys")
	  RSAKeyIdsResponse assignStandardRSAKeys(StandardRSAKeyIds standardRSAKeyIds);

	  @POST
	  @Produces(DEFAULT_CONTENT_TYPE)
	  @Consumes(DEFAULT_CONTENT_TYPE)
	  @Path("/unassignStandardRSAKeys")
	  RSAKeyIdsResponse unassignStandardRSAKeys(StandardRSAKeyIds standardRSAKeyIds);

	  @POST
	  @Produces(DEFAULT_CONTENT_TYPE)
	  @Consumes(DEFAULT_CONTENT_TYPE)
	  @Path("/enableStandardRSAKeys")
	  RSAKeyIdsResponse enableStandardRSAKeys(StandardRSAKeyIds standardRSAKeyIds);

	  @POST
	  @Produces(DEFAULT_CONTENT_TYPE)
	  @Consumes(DEFAULT_CONTENT_TYPE)
	  @Path("/disableStandardRSAKeys")
	  RSAKeyIdsResponse disableStandardRSAKeys(StandardRSAKeyIds standardRSAKeyIds);

	  @POST
	  @Produces(DEFAULT_CONTENT_TYPE)
	  @Consumes(DEFAULT_CONTENT_TYPE)
	  @Path("/deleteStandardRSAKeys")
	  RSAKeyIdsResponse deleteStandardRSAKeys(StandardRSAKeyIds standardRSAKeyIds);

	  @POST
	  @Produces(DEFAULT_CONTENT_TYPE)
	  @Consumes(DEFAULT_CONTENT_TYPE)
	  @Path("/listStandardRSAKeys")
	  ListStandardRSAKeysResponse listStandardRSAKeys(ListStandardRSAKeysParam listStandardRSAKeysParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/updateUser")
	NullResponse updateUser(User user);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listUsers")
	ListUsersResponse listUsers(ListUsersParam listUsersParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listLDAPConfigs")
	ListLDAPConfigsResponse listLDAPConfigs(ListLDAPConfigsParam listLDAPConfigsParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/verifyLDAPConfig")
	NullResponse verifyLDAPConfig(LDAPConfig ldapConfig);
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/refreshLDAPConfig")
	NullResponse refreshLDAPConfig(LDAPConfig ldapConfig);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/addLDAPConfig")
	NullResponse addLDAPConfig(LDAPConfig ldapConfig);
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/syncLDAPConfig")
	NullResponse syncLDAPConfig(LDAPConfig ldapConfig);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/deleteLDAPConfig")
	NullResponse deleteLDAPConfig(LDAPConfig ldapConfig);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/configLDAPSynchronizingInterval")
	NullResponse configLDAPSynchronizingInterval(LDAPConfig ldapConfig);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listSessions")
	ListSessionsResponse listSessions(ListSessionsParam listSessionsParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/disconnectSession")
	NullResponse disconnectSession(SessionIdWrapper sessionIdWrapper);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listDomains")
	ListDomainsResponse listDomains();

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/createOrganization")
	OrganizationIdResponse createOrganization(Organization organization);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/deleteOrganization")
	NullResponse deleteOrganization(OrganizationIdParam organizationIdParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/updateOrganization")
	NullResponse updateOrganization(Organization organization);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listOrganizations")
	ListOrganizationsResponse listOrganizations(ListOrganizationsParam listOrganizationsParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/addUserToOrganization")
	UserIdAndOrganizationListResponse addUserToOrganization(UserAndOrganization userAndOrganization);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/deleteUserFromOrganization")
	UserIdAndOrganizationListResponse deleteUserFromOrganization(UserAndOrganization userAndOrganization);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/createGroup")
	GroupIdResponse createGroup(Group group);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/deleteGroup")
	GroupListResponse deleteGroup(GroupIdParam groupIdParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/updateGroup")
	NullResponse updateGroup(Group group);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listGroups")
	ListGroupsResponse listGroups(ListGroupsParam listGroupsParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/addUserToGroup")
	UserIdAndGroupListResponse addUserToGroup(UserAndGroup userAndGroup);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/deleteUserFromGroup")
	UserIdAndGroupListResponse deleteUserFromGroup(UserAndGroup userAndGroup);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/addOrganizationToGroup")
	OrganizationIdAndGroupListResponse addOrganizationToGroup(OrganizationAndGroup organizationAndGroup);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/deleteOrganizationFromGroup")
	OrganizationIdAndGroupListResponse deleteOrganizationFromGroup(OrganizationAndGroup organizationAndGroup);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listTemplates")
	ListTemplatesResponse listTemplates(ListTemplatesParam listTemplatesParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/createDesktopPool")
	DesktopPoolIdResponse createDesktopPool(DesktopPoolEntity desktopPool);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/deleteDesktopPool")
	DesktopPoolListResponse deleteDesktopPool(DesktopPoolIdParam desktopPoolIdParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listDesktopPools")
	ListDesktopPoolsResponse listDesktopPools(ListDesktopPoolsParam listDesktopPoolsParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/assignDesktopPoolToUser")
	UserAndPoolListResponse assignDesktopPoolToUser(UserAndDesktopPool userAndDesktopPool);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/assignDesktopPoolToGroup")
	GroupAndPoolListResponse assignDesktopPoolToGroup(GroupAndDesktopPool groupAndDesktopPool);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/destroyDesktop")
	NullResponse destroyDesktop(DesktopIdParam desktopIdParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/rebootDesktop")
	NullResponse rebootDesktop(DesktopIdParam desktopIdParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/startDesktop")
	NullResponse startDesktop (DesktopIdParam desktopIdParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/stopDesktop")
	NullResponse stopDesktop(DesktopIdParam desktopIdParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/rebootDesktopOS")
	NullResponse rebootDesktopOS(DesktopIdParam desktopIdParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/stopDesktopOS")
	NullResponse stopDesktopOS(DesktopIdParam desktopIdParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listDesktops")
	ListDesktopsResponse listDesktops(ListDesktopsParam listDesktopsParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/addCloudManager")
	CloudManagerEntityIdResponse addCloudManager(CloudManagerEntity cloudManagerEntity);
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/updateCloudManager")
	CloudManagerEntityIdResponse updateCloudManager(CloudManagerEntity cloudManagerEntity);
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listTrace")
	ListTraceRes listTrace(TraceReq user);
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/deleteCloudManager")
	NullResponse deleteCloudManager(CloudManagerEntityIdParam cloudManagerEntityIdParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listCloudDrivers")
	ListCloudDriversResponse listCloudDrivers();

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listCloudManagers")
	ListCloudManagersResponse listCloudManagers(ListCloudManagersParam listCloudManagersParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/queryAsyncJobResult")
	EstablishConnectionAsyncJobResponse queryAsyncJobResult(AsyncJobIdParam asyncJobIdParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listAsyncJobs")
	ListAsyncJobsResponse listAsyncJobs(ListAsyncJobsParam listAsyncJobsParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listResources")
	ListResourcesResponse listResources(ResourceTypeParam resourceTypeParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/establishConnection")
	NullResponse establishConnection(ResourceTypeAndId resourceTypeAndId);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/destroyConnection")
	NullResponse destroyConnection(ConnectionTicketParam connectionTicketParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listConnections")
	ListConnectionsResponse listConnections(ListConnectionsParam listConnectionsParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/disconnectConnection")
	NullResponse disconnectConnection(ConnectionIdParam connectionIdParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/assignPrivilege")
	NullResponse assignPrivilege(PrivilegeParam privilegeParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/unassignPrivilege")
	NullResponse unassignPrivilege(PrivilegeParam privilegeParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/unassignDesktopPoolToUser")
	UserAndPoolListResponse unassignDesktopPoolToUser(UserAndDesktopPool userAndDesktopPool);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/unassignDesktopPoolToGroup")
	GroupAndPoolListResponse unassignDesktopPoolToGroup(GroupAndDesktopPool groupAndDesktopPool);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/assignDesktopPoolToOrganization")
	OrganizationAndPoolListResponse assignDesktopPoolToOrganization(OrganizationAndDesktopPool organizationAndDesktopPool);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/unassignDesktopPoolToOrganization")
	OrganizationAndPoolListResponse unassignDesktopPoolToOrganization(OrganizationAndDesktopPool organizationAndDesktopPool);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/getNotification")
	GetNotificationResponse getNotification();

	@POST()
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/updateDesktopPool")
	NullResponse updateDesktopPool(DesktopPoolEntity desktopPool);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listAssignments")
	ListAssignmentsResponse listAssignments(ListAssignmentsParam listAssignmentsParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/shutdownSystem")
	NullResponse shutdownSystem();

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/rebootSystem")
	NullResponse rebootSystem();

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/stopService")
	NullResponse stopService();

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/restartService")
	NullResponse restartService();

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/configureSystem")
	NullResponse configureSystem(ConfigureSystemParam configureSystemParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listNetworkAdapters")
	ListNetworkAdaptersResponse listNetworkAdapters();

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/configureNetworkAdapter")
	NullResponse configureNetworkAdapter(NetworkAdapter networkAdapter);
	
	// 浠ヤ笅涓鸿凯浠?鍐呭.

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/createUserVolume")
	NullResponse createUserVolume(UserVolume userVolume);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/deleteUserVolume")
	NullResponse deleteUserVolume(UserIdAndVolumeIdParam userIdParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/eraseUserVolume")
	NullResponse eraseUserVolume(UserIdAndVolumeIdParam userIdParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listUserVolumes")
	ListUserVolumesResponse listUserVolumes(UserIdParam userIdParam);
	
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/attachOwnedVolume")
	NullResponse attachOwnedVolume(ResourceParam resourceParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/detachOwnedVolume")
	NullResponse detachOwnedVolume(ForceWrapper forceWrapper);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listOwnedVolumes")
	ListOwnedVolumesResponse listOwnedVolumes();

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/eraseOwnedVolume")
	NullResponse eraseOwnedVolume(UserIdAndVolumeIdParam userIdParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/updatePassword")
	NullResponse updatePassword(NewPasswordWrapper newPasswordWrapper);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listRestrictionStrategies")
	ListRestrictionStrategiesResponse listRestrictionStrategies(ListRestrictionStrategiesParam listRestrictionStrategiesParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/createRestrictionStrategy")
	RestrictionStrategyResponse createRestrictionStrategy(RestrictionStrategy restrictionStrategy);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/updateRestrictionStrategy")
	NullResponse updateRestrictionStrategy(RestrictionStrategy restrictionStrategy);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listRestrictionStrategyAssignments")
	ListRestrictionStrategyAssignmentsResponse listRestrictionStrategyAssignments(ListRestrictionStrategyAssignmentsParam listRestrictionStrategyAssignmentsParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/assignRestrictionStrategy")
	StrategyAssignmentResponse assignRestrictionStrategy(RestrictionStrategyAssignmentParam restrictionStrategyAssignmentParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/unassignRestrictionStrategy")
	StrategyAssignmentResponse unassignRestrictionStrategy(RestrictionStrategyAssignmentParam restrictionStrategyAssignmentParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/deleteRestrictionStrategy")
	RestrictionStrategyListResponse deleteRestrictionStrategy(RestrictionStrategyListParam restrictionStrategyAssignmentParam);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/stopAssignedDesktop")
	NullResponse stopAssignedDesktop(ResourceTypeAndId resourceTypeAndId);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/startAssignedDesktop")
	NullResponse startAssignedDesktop(ResourceTypeAndId resourceTypeAndId);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/restartAssignedDesktop")
	NullResponse restartAssignedDesktop(ResourceTypeAndId resourceTypeAndId);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/getRestrictionStrategy")
	GetRestrictionStrategyResponse getRestrictionStrategy(ResourceTypeAndId resourceTypeAndId);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/verifyDesktopByIPAddress")
	AddDesktopByIPAddressResponse verifyDesktopByIPAddress(DesktopPoolAndIPAddress desktopPoolAndIPAddress);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/addDesktopByIPAddress")
	AddDesktopByIPAddressResponse addDesktopByIPAddress(DesktopPoolAndIPAddress desktopPoolAndIPAddress);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/resizeDesktopPool")
	NullResponse resizeDesktopPool(DesktopPoolAndSize desktopPoolAndSize);

	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/deleteDesktops")
	DesktopListResponse deleteDesktops(DesktopIds desktopIds);
	
	//add by tanyunhua start---
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/importLicense")
	public LicenseResponse importLicense(License license);
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listLicense")
	@Ignore
	public ListLicenseResponse listLicense(LicenseIdParam licenseParam);
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/deleteLicense")
	@Ignore
	public NullResponse deleteLicense(LicenseIdParam licenseParam);

	//add by zhanglu 2014-07-11 start
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listLicenseServer")
	public ListLicenseServerResponse ListLicenseServer(LicenseServerIdParam licenseServerIdParam);
	
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/createLicenseServer")
	public LicenseServerResponse createLicenseServer(LicenseServer licenseServer);
	
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/updateLicenseServer")
	public LicenseServerResponse updateLicenseServer(LicenseServer licenseServer);
	
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/downloadGuid")
	public GuidResponse downloadGuid();
	//add by zhanglu 2014-07-11 end
	
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/downloadFingerPrint")
	public FingerPrintResponse downloadFingerPrint();
	//add by tanyunhua end---
	
	/**********add by zhengyi start
	 * @throws AppstatusRestException ************/
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/listNodes")
	public AppStatusResponse<Node> listNodes(NodeReq req) throws AppstatusRestException;
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/updateNodes")
	public AppStatusResponse<Node> updateNodes(NodeAddressList list) throws AppstatusRestException;
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/deleteNodes")
	public AppStatusResponse<Node> deleteNodes(NodeAddressList list);
	
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/appstatuslogin")
	public AppStatusResponse<Object> appstatusCheckLogin(UserParam userParam) throws AppstatusRestException;
	/**********add by zhengyi end************/
	@POST
	@Produces(DEFAULT_CONTENT_TYPE)
	@Consumes(DEFAULT_CONTENT_TYPE)
	@Path("/appstatuschangenetworkinterface")
	public AppStatusResponse<Node> appstatusChangeNetworkInterface(IPconfigReq ipConfigReq) throws AppstatusRestException;
	
	

	@XmlTransient
	public static abstract class Response<T> {

		private ResponseHeader head;
		
		public Response() {
			this.head = new ResponseHeader();
		}
		
		public ResponseHeader getHead() {
			return head;
		}
		
		public abstract T getBody();
		public abstract void setBody(T body);
		
	}

	@XmlTransient
	public static abstract class MultiStatusResponse<E> extends Response<List<E>> {
		
		public void addStatus(E e) {
			if (numberNotEquals(this.getHead().getError(), MULTI_STATUS)) {
				this.getHead().setError(MULTI_STATUS);
			}
			if (this.getBody() == null) {
				this.setBody(new LinkedList<E>());
			}
			this.getBody().add(e);
		}
		
	}

	public static class ResponseHeader implements Serializable {

		private static final long serialVersionUID = 1L;

		private int error;
		private Integer jobid;

		public int getError() {
			return error;
		}
		public void setError(int error) {
			this.error = err.error(error);
		}
		public Integer getJobid() {
			return jobid;
		}
		public void setJobid(Integer jobid) {
			this.jobid = jobid;
		}
		
	}

	@XmlRootElement(name = "response")
	public static class NullResponse extends Response<String> implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private String body;
		
		@Override
		public String getBody() {
			return body;
		}
		@Override
		public void setBody(String body) {
			this.body = body;
		}
		
	}

	@XmlRootElement(name = "list")
	public static abstract class CommonList<E> extends PagingInfo implements Serializable {

		private static final long serialVersionUID = 1L;

		private int amount;

		public int getAmount() {
			return amount;
		}
		public void setAmount(int amount) {
			this.amount = amount;
		}
		
		public abstract List<E> getList();
		public abstract void setList(List<E> list);
		
	}

}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    