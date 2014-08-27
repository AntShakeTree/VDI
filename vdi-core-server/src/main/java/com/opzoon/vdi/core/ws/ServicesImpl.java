package com.opzoon.vdi.core.ws;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.appstatus.domain.UserParam;
import com.opzoon.appstatus.domain.req.IPconfigReq;
import com.opzoon.appstatus.domain.req.NodeAddressList;
import com.opzoon.appstatus.domain.req.NodeReq;
import com.opzoon.appstatus.domain.res.AppStatusResponse;
import com.opzoon.appstatus.facade.AppStatusService;
import com.opzoon.ohvc.common.RailAppError;
import com.opzoon.ohvc.common.RailResponse;
import com.opzoon.ohvc.domain.Head;
import com.opzoon.ohvc.domain.RailApplicationIcon;
import com.opzoon.ohvc.response.RailApplicationIconRes;
import com.opzoon.vdi.core.app.domain.RailApplicationServer;
import com.opzoon.vdi.core.app.domain.RailApplicationView;
import com.opzoon.vdi.core.app.request.DeleteApplicationServerReq;
import com.opzoon.vdi.core.app.request.ListApplicationServersReq;
import com.opzoon.vdi.core.app.request.ListApplicationsReq;
import com.opzoon.vdi.core.app.request.ListRailAssignmentsReq;
import com.opzoon.vdi.core.app.request.PublishOrUnPubilshRailApplicationReq;
import com.opzoon.vdi.core.app.request.RailApplicationResourceReq;
import com.opzoon.vdi.core.app.request.RailApplicationToGroupReq;
import com.opzoon.vdi.core.app.request.RailApplicationToOrganizationReq;
import com.opzoon.vdi.core.app.request.RailApplicationToUserReq;
import com.opzoon.vdi.core.app.request.RailApplicationViewReq;
import com.opzoon.vdi.core.app.request.RailConnectionReq;
import com.opzoon.vdi.core.app.request.TraceReq;
import com.opzoon.vdi.core.app.response.ApplicationServerResponse;
import com.opzoon.vdi.core.app.response.ListApplicationServerRes;
import com.opzoon.vdi.core.app.response.ListApplicationsRes;
import com.opzoon.vdi.core.app.response.ListRailApplicationsViewRes;
import com.opzoon.vdi.core.app.response.ListTraceRes;
import com.opzoon.vdi.core.app.response.RailApplicationResourceRes;
import com.opzoon.vdi.core.app.response.RailConnection;
import com.opzoon.vdi.core.app.response.RailConnnectionRes;
import com.opzoon.vdi.core.domain.CloudManagerEntity;
import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.Group;
import com.opzoon.vdi.core.domain.License;
import com.opzoon.vdi.core.domain.LicenseServer;
import com.opzoon.vdi.core.domain.Logtrace;
import com.opzoon.vdi.core.domain.LogtraceAfter;
import com.opzoon.vdi.core.domain.Organization;
import com.opzoon.vdi.core.domain.RestrictionStrategy;
import com.opzoon.vdi.core.domain.User;
import com.opzoon.vdi.core.domain.UserVolume;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.facade.RailUserDataManager;
import com.opzoon.vdi.core.util.Validator;
import com.opzoon.vdi.core.ws.DesktopUsage.ConnectionTicketParam;
import com.opzoon.vdi.core.ws.DesktopUsage.ForceWrapper;
import com.opzoon.vdi.core.ws.DesktopUsage.GetNotificationResponse;
import com.opzoon.vdi.core.ws.DesktopUsage.GetRestrictionStrategyResponse;
import com.opzoon.vdi.core.ws.DesktopUsage.ListResourcesResponse;
import com.opzoon.vdi.core.ws.DesktopUsage.ResourceParam;
import com.opzoon.vdi.core.ws.DesktopUsage.ResourceTypeAndId;
import com.opzoon.vdi.core.ws.DesktopUsage.ResourceTypeParam;
import com.opzoon.vdi.core.ws.DomainUsage.ListDomainsResponse;
import com.opzoon.vdi.core.ws.admin.DesktopManagement;
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
import com.opzoon.vdi.core.ws.admin.DomainManagement;
import com.opzoon.vdi.core.ws.admin.DomainManagement.LDAPConfig;
import com.opzoon.vdi.core.ws.admin.DomainManagement.ListLDAPConfigsParam;
import com.opzoon.vdi.core.ws.admin.DomainManagement.ListLDAPConfigsResponse;
import com.opzoon.vdi.core.ws.admin.GroupManagement;
import com.opzoon.vdi.core.ws.admin.GroupManagement.GroupIdParam;
import com.opzoon.vdi.core.ws.admin.GroupManagement.GroupIdResponse;
import com.opzoon.vdi.core.ws.admin.GroupManagement.GroupListResponse;
import com.opzoon.vdi.core.ws.admin.GroupManagement.ListGroupsParam;
import com.opzoon.vdi.core.ws.admin.GroupManagement.ListGroupsResponse;
import com.opzoon.vdi.core.ws.admin.GroupManagement.OrganizationAndGroup;
import com.opzoon.vdi.core.ws.admin.GroupManagement.OrganizationIdAndGroupListResponse;
import com.opzoon.vdi.core.ws.admin.GroupManagement.UserAndGroup;
import com.opzoon.vdi.core.ws.admin.GroupManagement.UserIdAndGroupListResponse;
import com.opzoon.vdi.core.ws.admin.LicenseMangement;
import com.opzoon.vdi.core.ws.admin.LicenseMangement.FingerPrintResponse;
import com.opzoon.vdi.core.ws.admin.LicenseMangement.LicenseIdParam;
import com.opzoon.vdi.core.ws.admin.LicenseMangement.LicenseResponse;
import com.opzoon.vdi.core.ws.admin.LicenseMangement.ListLicenseResponse;
import com.opzoon.vdi.core.ws.admin.LicenseServerMangement;
import com.opzoon.vdi.core.ws.admin.LicenseServerMangement.GuidResponse;
import com.opzoon.vdi.core.ws.admin.LicenseServerMangement.LicenseServerIdParam;
import com.opzoon.vdi.core.ws.admin.LicenseServerMangement.LicenseServerResponse;
import com.opzoon.vdi.core.ws.admin.LicenseServerMangement.ListLicenseServerResponse;
import com.opzoon.vdi.core.ws.admin.OrganizationManagement;
import com.opzoon.vdi.core.ws.admin.OrganizationManagement.ListOrganizationsParam;
import com.opzoon.vdi.core.ws.admin.OrganizationManagement.ListOrganizationsResponse;
import com.opzoon.vdi.core.ws.admin.OrganizationManagement.OrganizationIdParam;
import com.opzoon.vdi.core.ws.admin.OrganizationManagement.OrganizationIdResponse;
import com.opzoon.vdi.core.ws.admin.OrganizationManagement.UserAndOrganization;
import com.opzoon.vdi.core.ws.admin.OrganizationManagement.UserIdAndOrganizationListResponse;
import com.opzoon.vdi.core.ws.admin.RailAdminManager;
import com.opzoon.vdi.core.ws.admin.SystemManagement;
import com.opzoon.vdi.core.ws.admin.SystemManagement.ConfigureSystemParam;
import com.opzoon.vdi.core.ws.admin.SystemManagement.ListNetworkAdaptersResponse;
import com.opzoon.vdi.core.ws.admin.SystemManagement.NetworkAdapter;
import com.opzoon.vdi.core.ws.admin.UserManagement;
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
 * WebServices总接口的实现类.<br />
 * 此类只负责传递参数与结果, 其支持的所有操作均由注入其中的具体的业务实现负责实现.
 */
public class ServicesImpl implements Services, RailMangerOperateService, RailUserOperateService {
	
	private static final Logger log = LoggerFactory.getLogger(ServicesImpl.class);

	private Entrance entrance;
	private DomainManagement domainManagement;
	private UserManagement userManagement;
	private DomainUsage domainUsage;
	private OrganizationManagement organizationManagement;
	private GroupManagement groupManagement;
	private DesktopManagement desktopManagement;
	private SystemManagement systemManagement;
	private DesktopUsage desktopUsage;

	private Validator validator;
	private RailUserDataManager railUserDataManager;
	private RailAdminManager railAdminManager;
	
	/**********add by zhengyi start************/
	@Autowired
	private AppStatusService appStatusService;
	/**********add by zhengyi end************/

	// 以下为迭代1内容.
	
	@Override
	public ListAvailableAuthenticationMethodsResponse listAvailableAuthenticationMethods()
	{
		log.trace("");
		return entrance.listAvailableAuthenticationMethods();
	}

	@Override
	@Ignore
	@LogtraceAfter
	public LoginResponse loginSession(LoginInfo loginInfo) {
		log.trace("");
		return entrance.loginSession(loginInfo);
	}

	@Override
	@Logtrace
	public NullResponse logoutSession() {
		log.trace("");
		return entrance.logoutSession();
	}

	@Override
	@LogtraceAfter
	public UserIdResponse createUser(User user) {
		log.trace("");
		return userManagement.createUser(user);
	}

	@Override
	@Logtrace
	public UserIdsResponse deleteUser(UserIdsParam userIdsParam) {
		log.trace("");
		return userManagement.deleteUser(userIdsParam);
	}

	  @Override
	  public RSAKeyIdsResponse importStandardRSAKeys(StandardRSAKeys standardRSAKeys) {
	    log.trace("");
	    return userManagement.importStandardRSAKeys(standardRSAKeys);
	  }

	  @Override
	  public RSAKeyIdsResponse assignStandardRSAKeys(StandardRSAKeyIds standardRSAKeyIds) {
	    log.trace("");
	    return userManagement.assignStandardRSAKeys(standardRSAKeyIds);
	  }

	  @Override
	  public RSAKeyIdsResponse unassignStandardRSAKeys(StandardRSAKeyIds standardRSAKeyIds) {
	    log.trace("");
	    return userManagement.unassignStandardRSAKeys(standardRSAKeyIds);
	  }

	  @Override
	  public RSAKeyIdsResponse enableStandardRSAKeys(StandardRSAKeyIds standardRSAKeyIds) {
	    log.trace("");
	    return userManagement.enableStandardRSAKeys(standardRSAKeyIds);
	  }

	  @Override
	  public RSAKeyIdsResponse disableStandardRSAKeys(StandardRSAKeyIds standardRSAKeyIds) {
	    log.trace("");
	    return userManagement.disableStandardRSAKeys(standardRSAKeyIds);
	  }

	  @Override
	  public RSAKeyIdsResponse deleteStandardRSAKeys(StandardRSAKeyIds standardRSAKeyIds) {
	    log.trace("");
	    return userManagement.deleteStandardRSAKeys(standardRSAKeyIds);
	  }

	  @Override
	  public ListStandardRSAKeysResponse listStandardRSAKeys(ListStandardRSAKeysParam listStandardRSAKeysParam) {
	    log.trace("");
	    return userManagement.listStandardRSAKeys(listStandardRSAKeysParam);
	  }

	@Override
	@Logtrace
	public NullResponse updateUser(User user) {
		log.trace("");
		return userManagement.updateUser(user);
	}

	@Override
	public ListUsersResponse listUsers(ListUsersParam listUsersParam) {
		log.trace("");
		return userManagement.listUsers(listUsersParam);
	}

	@Override
	public NullResponse verifyLDAPConfig(LDAPConfig ldapConfig) {
		log.trace("");
		return domainManagement.verifyLDAPConfig(ldapConfig);
	}
	@Override
	public NullResponse refreshLDAPConfig(LDAPConfig ldapConfig) {
		log.trace("");
	
		return domainManagement.fleshLDAPConfig(ldapConfig);
	}
	
	@Override
	public ListLDAPConfigsResponse listLDAPConfigs(ListLDAPConfigsParam listLDAPConfigsParam) {
		log.trace("");
		return domainManagement.listLDAPConfigs(listLDAPConfigsParam);
	}

	@Override
	public NullResponse addLDAPConfig(LDAPConfig ldapConfig) {
		log.trace("");
		return domainManagement.addLDAPConfig(ldapConfig);
	}


	@Override
	public NullResponse deleteLDAPConfig(LDAPConfig ldapConfig) {
		log.trace("");
		return domainManagement.deleteLDAPConfig(ldapConfig);
	}

	@Override
	public NullResponse configLDAPSynchronizingInterval(LDAPConfig ldapConfig) {
		log.trace("");
		return domainManagement.configLDAPSynchronizingInterval(ldapConfig);
	}

	@Override
	public ListSessionsResponse listSessions(ListSessionsParam listSessionsParam) {
		log.trace("");
		return userManagement.listSessions(listSessionsParam);
	}

	@Override
	public NullResponse disconnectSession(SessionIdWrapper sessionIdWrapper) {
		log.trace("");
		return userManagement.disconnectSession(sessionIdWrapper);
	}

	@Override
	public ListDomainsResponse listDomains() {
		log.trace("");
		return domainUsage.listDomains();
	}

	@Override
	public OrganizationIdResponse createOrganization(Organization organization) {
		log.trace("");
		return organizationManagement.createOrganization(organization);
	}

	@Override
	public NullResponse deleteOrganization(
			OrganizationIdParam organizationIdParam) {
		log.trace("");
		return organizationManagement.deleteOrganization(organizationIdParam);
	}

	@Override
	public NullResponse updateOrganization(Organization organization) {
		log.trace("");
		return organizationManagement.updateOrganization(organization);
	}

	@Override
	public ListOrganizationsResponse listOrganizations(
			ListOrganizationsParam listOrganizationsParam) {
		log.trace("");
		return organizationManagement.listOrganizations(listOrganizationsParam);
	}

	@Override
	public UserIdAndOrganizationListResponse addUserToOrganization(
			UserAndOrganization userAndOrganization) {
		log.trace("");
		return organizationManagement.addUserToOrganization(userAndOrganization);
	}

	@Override
	public UserIdAndOrganizationListResponse deleteUserFromOrganization(
			UserAndOrganization userAndOrganization) {
		log.trace("");
		return organizationManagement.deleteUserFromOrganization(userAndOrganization);
	}

	@Override
	public GroupIdResponse createGroup(Group group) {
		log.trace("");
		return groupManagement.createGroup(group);
	}

	@Override
	public GroupListResponse deleteGroup(GroupIdParam groupIdParam) {
		log.trace("");
		return groupManagement.deleteGroup(groupIdParam);
	}

	@Override
	public NullResponse updateGroup(Group group) {
		log.trace("");
		return groupManagement.updateGroup(group);
	}

	@Override
	public ListGroupsResponse listGroups(ListGroupsParam listGroupsParam) {
		log.trace("");
		return groupManagement.listGroups(listGroupsParam);
	}

	@Override
	public UserIdAndGroupListResponse addUserToGroup(UserAndGroup userAndGroup) {
		log.trace("");
		return groupManagement.addUserToGroup(userAndGroup);
	}

	@Override
	public UserIdAndGroupListResponse deleteUserFromGroup(UserAndGroup userAndGroup) {
		log.trace("");
		return groupManagement.deleteUserFromGroup(userAndGroup);
	}

	@Override
	public OrganizationIdAndGroupListResponse addOrganizationToGroup(
			OrganizationAndGroup organizationAndGroup) {
		log.trace("");
		return groupManagement.addOrganizationToGroup(organizationAndGroup);
	}

	@Override
	public OrganizationIdAndGroupListResponse deleteOrganizationFromGroup(
			OrganizationAndGroup organizationAndGroup) {
		log.trace("");
		return groupManagement.deleteOrganizationFromGroup(organizationAndGroup);
	}

	@Override
	public ListTemplatesResponse listTemplates(
			ListTemplatesParam listTemplatesParam) {
		log.trace("");
		return desktopManagement.listTemplates(listTemplatesParam);
	}

	@Override
	public DesktopPoolIdResponse createDesktopPool(DesktopPoolEntity desktopPool) {
		log.trace("");
		return desktopManagement.createDesktopPool(desktopPool);
	}

	@Override
	public DesktopPoolListResponse deleteDesktopPool(DesktopPoolIdParam desktopPoolIdParam) {
		log.trace("");
		return desktopManagement.deleteDesktopPool(desktopPoolIdParam);
	}

	@Override
	public ListDesktopPoolsResponse listDesktopPools(ListDesktopPoolsParam listDesktopPoolsParam) {
		log.trace("");
		return desktopManagement.listDesktopPools(listDesktopPoolsParam);
	}

	@Override
	public UserAndPoolListResponse assignDesktopPoolToUser(
			UserAndDesktopPool userAndDesktopPool) {
		log.trace("");
		return desktopManagement.assignDesktopPoolToUser(userAndDesktopPool);
	}

	@Override
	public UserAndPoolListResponse unassignDesktopPoolToUser(
			UserAndDesktopPool userAndDesktopPool) {
		log.trace("");
		return desktopManagement.unassignDesktopPoolToUser(userAndDesktopPool);
	}

	@Override
	public GroupAndPoolListResponse assignDesktopPoolToGroup(
			GroupAndDesktopPool groupAndDesktopPool) {
		log.trace("");
		return desktopManagement.assignDesktopPoolToGroup(groupAndDesktopPool);
	}

	@Override
	public GroupAndPoolListResponse unassignDesktopPoolToGroup(
			GroupAndDesktopPool groupAndDesktopPool) {
		log.trace("");
		return desktopManagement.unassignDesktopPoolToGroup(groupAndDesktopPool);
	}

	@Override
	public OrganizationAndPoolListResponse assignDesktopPoolToOrganization(
			OrganizationAndDesktopPool organizationAndDesktopPool) {
		log.trace("");
		return desktopManagement.assignDesktopPoolToOrganization(organizationAndDesktopPool);
	}

	@Override
	public OrganizationAndPoolListResponse unassignDesktopPoolToOrganization(
			OrganizationAndDesktopPool organizationAndDesktopPool) {
		log.trace("");
		return desktopManagement.unassignDesktopPoolToOrganization(organizationAndDesktopPool);
	}

	@Override
	public NullResponse destroyDesktop(DesktopIdParam desktopIdParam) {
		log.trace("");
		return desktopManagement.destroyDesktop(desktopIdParam);
	}

	@Override
	public NullResponse rebootDesktop(DesktopIdParam desktopIdParam) {
		log.trace("");
		return desktopManagement.rebootDesktop(desktopIdParam);
	}

	@Override
	public NullResponse startDesktop(DesktopIdParam desktopIdParam) {
		log.trace("");
		return desktopManagement.startDesktop(desktopIdParam);
	}

	@Override
	public NullResponse stopDesktop(DesktopIdParam desktopIdParam) {
		log.trace("");
		return desktopManagement.stopDesktop(desktopIdParam);
	}

	@Override
	public NullResponse rebootDesktopOS(DesktopIdParam desktopIdParam) {
		log.trace("");
		return desktopManagement.rebootDesktopOS(desktopIdParam);
	}

	@Override
	public NullResponse stopDesktopOS(DesktopIdParam desktopIdParam) {
		log.trace("");
		return desktopManagement.stopDesktopOS(desktopIdParam);
	}

	@Override
	public ListDesktopsResponse listDesktops(
			ListDesktopsParam listDesktopsParam) {
		log.trace("");
		return desktopManagement.listDesktops(listDesktopsParam);
	}

	@Override
	public CloudManagerEntityIdResponse addCloudManager(
			CloudManagerEntity cloudManagerEntity) {
		log.trace("");
		return desktopManagement.addCloudManager(cloudManagerEntity);
	}

	@Override
	public NullResponse deleteCloudManager(
			CloudManagerEntityIdParam cloudManagerEntityIdParam) {
		log.trace("");
		return desktopManagement.deleteCloudManager(cloudManagerEntityIdParam);
	}

	@Override
	public ListCloudDriversResponse listCloudDrivers() {
		log.trace("");
		return desktopManagement.listCloudDrivers();
	}

	@Override
	public ListCloudManagersResponse listCloudManagers(
			ListCloudManagersParam listCloudManagersParam) {
		log.trace("");
		return desktopManagement.listCloudManagers(listCloudManagersParam);
	}

	@Override
	public EstablishConnectionAsyncJobResponse queryAsyncJobResult(AsyncJobIdParam asyncJobIdParam) {
		log.trace("");
		return desktopManagement.queryAsyncJobResult(asyncJobIdParam);
	}

	@Override
	public ListAsyncJobsResponse listAsyncJobs(
			ListAsyncJobsParam listAsyncJobsParam) {
		log.trace("");
		return desktopManagement.listAsyncJobs(listAsyncJobsParam);
	}

	@Override
	public ListResourcesResponse listResources(
			ResourceTypeParam resourceTypeParam) {
		log.trace("");
		return desktopUsage.listResources(resourceTypeParam);
	}

	@Override
	public NullResponse establishConnection(
			ResourceTypeAndId resourceTypeAndId) {
		log.trace("");
		return desktopUsage.establishConnection(resourceTypeAndId);
	}

	@Override
	public NullResponse destroyConnection(
			ConnectionTicketParam connectionTicketParam) {
		log.trace("");
		return desktopUsage.destroyConnection(connectionTicketParam);
	}

	@Override
	public ListConnectionsResponse listConnections(
			ListConnectionsParam listConnectionsParam) {
		log.trace("");
		return desktopManagement.listConnections(listConnectionsParam);
	}

	@Override
	public NullResponse disconnectConnection(ConnectionIdParam connectionIdParam) {
		log.trace("");
		return desktopManagement.disconnectConnection(connectionIdParam);
	}

	@Override
	@Logtrace
	public NullResponse assignPrivilege(PrivilegeParam privilegeParam) {
		log.trace("");
		return userManagement.assignPrivilege(privilegeParam);
	}

	@Override
	@Logtrace
	public NullResponse unassignPrivilege(PrivilegeParam privilegeParam) {
		log.trace("");
		return userManagement.unassignPrivilege(privilegeParam);
	}

	@Override
	public GetNotificationResponse getNotification() {
		log.trace("");
		return desktopUsage.getNotification();
	}

	@Override
	public NullResponse updateDesktopPool(DesktopPoolEntity desktopPool) {
		log.trace("");
		return desktopManagement.updateDesktopPool(desktopPool);
	}

	@Override
	public ListAssignmentsResponse listAssignments(
			ListAssignmentsParam listAssignmentsParam) {
		log.trace("");
		return desktopManagement.listAssignments(listAssignmentsParam);
	}

	@Override
	public NullResponse shutdownSystem() {
		log.trace("");
		return systemManagement.shutdownSystem();
	}

	@Override
	public NullResponse rebootSystem() {
		log.trace("");
		return systemManagement.rebootSystem();
	}

	@Override
	public NullResponse stopService() {
		log.trace("");
		return systemManagement.stopService();
	}

	@Override
	public NullResponse restartService() {
		log.trace("");
		return systemManagement.restartService();
	}

	@Override
	public NullResponse configureSystem(
			ConfigureSystemParam configureSystemParam) {
		log.trace("");
		return systemManagement.configureSystem(configureSystemParam);
	}

	@Override
	public ListNetworkAdaptersResponse listNetworkAdapters() {
		log.trace("");
		return systemManagement.listNetworkAdapters();
	}

	@Override
	public NullResponse configureNetworkAdapter(NetworkAdapter networkAdapter) {
		log.trace("");
		return systemManagement.configureNetworkAdapter(networkAdapter);
	}

	// 以下为迭代2内容.

	@Override
	@Logtrace
	public NullResponse createUserVolume(UserVolume userVolume) {
		log.trace("");
		return desktopManagement.createUserVolume(userVolume);
	}

	@Override
	@Logtrace
	public NullResponse deleteUserVolume(UserIdAndVolumeIdParam userIdParam) {
		log.trace("");
		return desktopManagement.deleteUserVolume(userIdParam);
	}

	@Override
	@Logtrace
	public NullResponse eraseUserVolume(UserIdAndVolumeIdParam userIdParam) {
		log.trace("");
		return desktopManagement.eraseUserVolume(userIdParam);
	}

	@Override
	public ListUserVolumesResponse listUserVolumes(UserIdParam userIdParam) {
		log.trace("");
		return desktopManagement.listUserVolumes(userIdParam);
	}

	@Override
	public NullResponse attachOwnedVolume(ResourceParam resourceParam) {
		log.trace("");
		return desktopUsage.attachUserVolume(resourceParam);
	}

	@Override
	public NullResponse detachOwnedVolume(ForceWrapper forceWrapper) {
		log.trace("");
		return desktopUsage.detachUserVolume(forceWrapper);
	}

	@Override
	public NullResponse eraseOwnedVolume(UserIdAndVolumeIdParam userIdParam) {
		log.trace("");
		return desktopUsage.eraseOwnedVolume(userIdParam);
	}

	@Override
	public ListOwnedVolumesResponse listOwnedVolumes() {
		log.trace("");
		return desktopUsage.listOwnedVolumes();
	}

	@Override
	@Logtrace
	public NullResponse updatePassword(NewPasswordWrapper newPasswordWrapper) {
		log.trace("");
		return entrance.updatePassword(newPasswordWrapper);
	}

	@Override
	public ListRestrictionStrategiesResponse listRestrictionStrategies(ListRestrictionStrategiesParam listRestrictionStrategiesParam) {
		log.trace("");
		return desktopManagement.listRestrictionStrategies(listRestrictionStrategiesParam);
	}

	@Override
	public RestrictionStrategyResponse createRestrictionStrategy(RestrictionStrategy restrictionStrategy)
	{
		log.trace("");
		return desktopManagement.createRestrictionStrategy(restrictionStrategy);
	}

	@Override
	public NullResponse updateRestrictionStrategy(
			RestrictionStrategy restrictionStrategy) {
		log.trace("");
		return desktopManagement.updateRestrictionStrategy(restrictionStrategy);
	}

	@Override
	public ListRestrictionStrategyAssignmentsResponse listRestrictionStrategyAssignments(
			ListRestrictionStrategyAssignmentsParam listRestrictionStrategyAssignmentsParam) {
		log.trace("");
		return desktopManagement.listRestrictionStrategyAssignments(listRestrictionStrategyAssignmentsParam);
	}

	@Override
	public StrategyAssignmentResponse assignRestrictionStrategy(
			RestrictionStrategyAssignmentParam restrictionStrategyAssignmentParam) {
		log.trace("");
		return desktopManagement.assignRestrictionStrategy(restrictionStrategyAssignmentParam);
	}

	@Override
	public StrategyAssignmentResponse unassignRestrictionStrategy(
			RestrictionStrategyAssignmentParam restrictionStrategyAssignmentParam) {
		log.trace("");
		return desktopManagement.unassignRestrictionStrategy(restrictionStrategyAssignmentParam);
	}
	
	@Override
	public RestrictionStrategyListResponse deleteRestrictionStrategy(
	    RestrictionStrategyListParam restrictionStrategyAssignmentParam) {
		log.trace("");
		return desktopManagement.deleteRestrictionStrategy(restrictionStrategyAssignmentParam);
	}

	@Override
	public NullResponse stopAssignedDesktop(ResourceTypeAndId resourceTypeAndId) {
		log.trace("");
		return desktopUsage.stopAssignedDesktop(resourceTypeAndId);
	}

	@Override
	public NullResponse startAssignedDesktop(ResourceTypeAndId resourceTypeAndId) {
		log.trace("");
		return desktopUsage.startAssignedDesktop(resourceTypeAndId);
	}

	@Override
	public NullResponse restartAssignedDesktop(ResourceTypeAndId resourceTypeAndId) {
		log.trace("");
		return desktopUsage.restartAssignedDesktop(resourceTypeAndId);
	}

	@Override
	public GetRestrictionStrategyResponse getRestrictionStrategy(
			ResourceTypeAndId resourceTypeAndId) {
		log.trace("");
		return desktopUsage.getRestrictionStrategy(resourceTypeAndId);
	}

	@Override
	public AddDesktopByIPAddressResponse verifyDesktopByIPAddress(
			DesktopPoolAndIPAddress desktopPoolAndIPAddress) {
		log.trace("");
		return desktopManagement.verifyDesktopByIPAddress(desktopPoolAndIPAddress);
	}

	@Override
	public AddDesktopByIPAddressResponse addDesktopByIPAddress(
			DesktopPoolAndIPAddress desktopPoolAndIPAddress) {
		log.trace("");
		return desktopManagement.addDesktopByIPAddress(desktopPoolAndIPAddress);
	}

	@Override
	public NullResponse resizeDesktopPool(DesktopPoolAndSize desktopPoolAndSize) {
		log.trace("");
		return desktopManagement.resizeDesktopPool(desktopPoolAndSize);
	}

	@Override
	public DesktopListResponse deleteDesktops(DesktopIds desktopIds) {
		log.trace("");
		return desktopManagement.deleteDesktops(desktopIds);
	}

	// ~~===========maxiaochao=====================虚拟应用服务器========================================~~
	@Override
	public RailResponse<RailApplicationServer> addApplicationServer(RailApplicationServer applicationServer) throws CommonException {
		return this.railAdminManager.addApplicationServer(applicationServer);
	}

	@Override
	public RailResponse<RailApplicationServer> deleteApplicationServer(DeleteApplicationServerReq delApplicationServerReq) throws CommonException {
		log.trace("");
		return this.railAdminManager.deleteApplicationServer(delApplicationServerReq);
	}

	@Override
	public ListApplicationServerRes listApplicationServers(ListApplicationServersReq req) throws CommonException {
		log.trace("");
		return this.railAdminManager.listApplicationServers(req);
	}

	@Override
	public ListApplicationsRes listRailApplications(ListApplicationsReq re) {
		log.trace("");
		return this.railAdminManager.listRailApplications(re);
	}

	/*
	 * (非 Javadoc) <p>Title: listPublishedRailApplications</p> <p>Description: </p>
	 * @param req
	 * @return
	 * @see com.opzoon.vdi.core.ws.RailUserOperateService#listPublishedRailApplications(com.opzoon.ohvc.domain.RailApplicationView)
	 */

	@Override
	public ListRailApplicationsViewRes listPublishedRailApplications(RailApplicationViewReq req) {
		log.trace("");

		return this.railAdminManager.listPublishedRailApplications(req);
	}

	@Override
	public RailResponse<Object> publishRailApplication(PublishOrUnPubilshRailApplicationReq req) {
		log.trace("");

		return this.railAdminManager.publishRailApplication(req);
	}

	@Override
	public RailResponse<Object> unpublishRailApplication(PublishOrUnPubilshRailApplicationReq req) {
		log.trace("");
		return this.railAdminManager.unpublishRailApplication(req);
	}

	@Override
	public RailResponse<Object> assignApplicationToUser(RailApplicationToUserReq req) {
		log.trace("");

		return this.railAdminManager.assignApplicationToUser(req);
	}

	@Override
	public RailResponse<Object> unassignApplicationToUser(RailApplicationToUserReq req) {
		log.trace("");
		return this.railAdminManager.unassignApplicationToUser(req);
	}

	@Override
	public RailResponse<Object> assignApplicationToOrganization(RailApplicationToOrganizationReq req) {
		log.trace("");
		return this.railAdminManager.assignApplicationToOrganization(req);
	}

	@Override
	public RailResponse<Object> unassignApplicationToOrganization(RailApplicationToOrganizationReq req) {
		log.trace("");
		return this.railAdminManager.unassignApplicationToOrganization(req);
	}

	@Override
	public RailResponse<Object> assignApplicationToGroup(RailApplicationToGroupReq req) {
		log.trace("");
		return this.railAdminManager.assignApplicationToGroup(req);
	}

	@Override
	public RailResponse<Object> unassignApplicationToGroup(RailApplicationToGroupReq req) {
		log.trace("");
		return this.railAdminManager.unassignApplicationToGroup(req);
	}

	/*
	 * (非 Javadoc) <p>Title: establishRailConnection</p> <p>Description: </p>
	 * @param req
	 * @return
	 * @see com.opzoon.vdi.core.ws.RailMangerOperateService#establishRailConnection(com.opzoon.ohvc.request.RailConnectionReq)
	 */

	@Override
	public RailConnnectionRes establishRailConnection(RailConnectionReq req) throws CommonException {
		RailConnnectionRes res = new RailConnnectionRes();
		res.setHead(new Head());
		// 验证逻辑
		if (!validator.validate(req, res.getHead())) {
			return res;
		}
		// 列举所有的可用的虚拟应用服务器 包含同一个域名 这样做的目的是为了确保单点登录
		RailConnection connection = this.railUserDataManager.createConnectionByApplicationIdAndConnectionStrategy(req.getApplicationid());
		res.setBody(connection);
		return res;
	}

	@Override
	public NullResponse destroyRailConnection(ConnectionTicketParam connectionTicketParam) {
		this.railUserDataManager.destroyRailConnection(connectionTicketParam.getConnectionticket());
		return this.destroyConnection(connectionTicketParam);
	}

	/**
	 * @param validator
	 *            the validator to set
	 */
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	/*
	 * (非 Javadoc) <p>Title: listRailResource</p> <p>Description: </p>
	 * @param req
	 * @return
	 * @see com.opzoon.vdi.core.ws.RailUserOperateService#listRailResource(com.opzoon.ohvc.request.RailApplicationResourceReq)
	 */

	@Override
	public RailApplicationResourceRes listRailResource(RailApplicationResourceReq req) {
		RailApplicationResourceRes res = new RailApplicationResourceRes();
		Head head = new Head();
		res.setHead(head);
		boolean val = validator.validate(req, head);
		if (!val) {
			return res;
		}
		List<RailApplicationView> body = this.railUserDataManager.listRailResource(req.getUserid());
		res.setBody(body);

		return res;
	}

	/**
	 * @return railUserDataManager
	 */
	public RailUserDataManager getRailUserDataManager() {
		return railUserDataManager;
	}

	/**
	 * @param railUserDataManager
	 *            the railUserDataManager to set
	 */
	public void setRailUserDataManager(RailUserDataManager railUserDataManager) {
		this.railUserDataManager = railUserDataManager;
	}

	/**
	 * @return railAdminManager
	 */
	public RailAdminManager getRailAdminManager() {
		return railAdminManager;
	}

	/**
	 * @param railAdminManager
	 *            the railAdminManager to set
	 */
	public void setRailAdminManager(RailAdminManager railAdminManager) {
		this.railAdminManager = railAdminManager;
	}

	/***********add by zhengyi start************/
	public AppStatusService getAppStatusService() {
		return appStatusService;
	}

	public void setAppStatusService(AppStatusService appStatusService) {
		this.appStatusService = appStatusService;
	}
	/***********add by zhengyi end************/
	
	/*
	 * (非 Javadoc) <p>Title: getRailApplicationIcon</p> <p>Description: </p>
	 * @param req
	 * @return
	 * @see com.opzoon.vdi.core.ws.RailMangerOperateService#getRailApplicationIcon(com.opzoon.ohvc.response.RailApplicationIcon)
	 */
	@Override
	public RailApplicationIconRes getRailApplicationIcon(RailApplicationIcon req) {
		log.trace("");
		RailApplicationIconRes res = new RailApplicationIconRes();
		Head head = new Head();
		head.setError(0);
		res.setHead(head);
		RailApplicationIcon icon = new RailApplicationIcon();
		try {
			icon = this.railUserDataManager.getRailApplicationIcon(req.getServername(), req.getApplicationid());
		} catch (Exception e) {
			e.printStackTrace();
			head.setError(RailAppError.RAIL_ERR.getError());
			log.error(e.getCause() + ":" + e.getMessage());
			return res;
		}
		res.setBody(icon);
		return res;
	}


	@Override
	public ListAssignmentsResponse listRailAssignments(ListRailAssignmentsReq listAssignmentsParam) {
		log.trace("");
		if(listAssignmentsParam.getResourcetype()==0x101){
			return railAdminManager.listRailAssignments(listAssignmentsParam);
		}
		return railAdminManager.listRailAssignments(listAssignmentsParam);
	}

	public void setEntrance(Entrance entrance) {
		this.entrance = entrance;
	}

	public void setUserManagement(UserManagement userManagement) {
		this.userManagement = userManagement;
	}
	
	public void setDomainManagement(DomainManagement domainManagement) {
		this.domainManagement = domainManagement;
	}

	public void setDomainUsage(DomainUsage domainUsage) {
		this.domainUsage = domainUsage;
	}

	public void setOrganizationManagement(
			OrganizationManagement organizationManagement) {
		this.organizationManagement = organizationManagement;
	}

	public void setGroupManagement(GroupManagement groupManagement) {
		this.groupManagement = groupManagement;
	}

	public void setDesktopManagement(DesktopManagement desktopManagement) {
		this.desktopManagement = desktopManagement;
	}

	public void setSystemManagement(SystemManagement systemManagement) {
		this.systemManagement = systemManagement;
	}

	public void setDesktopUsage(DesktopUsage desktopUsage) {
		this.desktopUsage = desktopUsage;
	}

	/**-----------------add by tanyunhua for license function start----------------------*/
	public LicenseResponse importLicense(License license) {
		log.trace("");
		return licenseMangement.importLicense(license);
	}

	public ListLicenseResponse listLicense(LicenseIdParam licenseParam) {
		log.trace("");
		return licenseMangement.listLicense(licenseParam);
	}

	public NullResponse deleteLicense(LicenseIdParam licenseParam) {
		log.trace("");
		return licenseMangement.deleteLicense(licenseParam);
	}
	
	public FingerPrintResponse downloadFingerPrint(){
		log.trace("");
		return licenseMangement.downloadFingerPrint();
	}
	
	private LicenseMangement licenseMangement;

	public void setLicenseMangement(LicenseMangement licenseMangement) {
		this.licenseMangement = licenseMangement;
	}
	/**----------------add by tanyunhua for license function end------------------------*/

	//add by zhanglu 2014-07-11 start
	private LicenseServerMangement licenseServerMangement;
	public void setLicenseServerMangement(LicenseServerMangement licenseServerMangement) {
		this.licenseServerMangement = licenseServerMangement;
	}
	public ListLicenseServerResponse ListLicenseServer(LicenseServerIdParam licenseServerIdParam){
		return licenseServerMangement.ListLicenseServer(licenseServerIdParam);
	};
	public LicenseServerResponse createLicenseServer(LicenseServer licenseServer){
		return licenseServerMangement.createLicenseServer(licenseServer);
	};
	public LicenseServerResponse updateLicenseServer(LicenseServer licenseServer){
		return licenseServerMangement.updateLicenseServer(licenseServer);
	}
	public GuidResponse downloadGuid(){
		return licenseServerMangement.downloadGuid();
	};
	//add by zhanglu 2014-07-11 end
	/* (非 Javadoc) 
	* <p>Title: updateApplicationServer</p> 
	* <p>Description: </p> 
	* @param applicationServer
	* @return
	* @throws CommonException 
	* @see com.opzoon.vdi.core.ws.RailMangerOperateService#updateApplicationServer(com.opzoon.vdi.core.app.domain.RailApplicationServer) 
	*/
	
	@Override
	@POST
	@Produces("application/opzoon-v4+json")
	@Consumes("application/opzoon-v4+json")
	@Path("/updateApplicationServer")
	public RailResponse<RailApplicationServer> updateApplicationServer(RailApplicationServer applicationServer) throws CommonException {
		ApplicationServerResponse  res =new ApplicationServerResponse();
		res.setHead(this.railAdminManager.updateApplicationServer(applicationServer));
//		res.setBody(new RailApplicationServer());
		return res;
	}

	/**********add by zhengyi start************/
	@Override
	@POST
	@Produces("application/opzoon-v4+json")
	@Consumes("application/opzoon-v4+json")
	@Path("/listNodes")
	@Ignore
	public AppStatusResponse<Node> listNodes(NodeReq req) throws AppstatusRestException
	{
		return appStatusService.listNodes(req);
	}

	@Override
	@POST
	@Produces("application/opzoon-v4+json")
	@Consumes("application/opzoon-v4+json")
	@Path("/updateNodes")
	@Ignore
	public AppStatusResponse<Node> updateNodes(NodeAddressList list) throws AppstatusRestException
	{
		return appStatusService.updateNodes(list);
	}

	@Override
	@POST
	@Produces("application/opzoon-v4+json")
	@Consumes("application/opzoon-v4+json")
	@Path("/deleteNodes")
	@Ignore
	public AppStatusResponse<Node> deleteNodes(NodeAddressList list)
	{
		return appStatusService.deleteNodes(list);
	}
	
	@Override
	@POST
	@Produces("application/opzoon-v4+json")
	@Consumes("application/opzoon-v4+json")
	@Path("/appstatuslogin")
	@Ignore
	public AppStatusResponse<Object> appstatusCheckLogin(UserParam userParam) throws AppstatusRestException
	{
		return appStatusService.checkAppStatusLogin(userParam.getUsername(), userParam.getPassword());
	}
	/**********add by zhengyi end************/
	@Override
	@POST
	@Produces("application/opzoon-v4+json")
	@Consumes("application/opzoon-v4+json")
	@Path("/appstatuschangenetworkinterface")
	@Ignore
	public AppStatusResponse<Node> appstatusChangeNetworkInterface(IPconfigReq ipConfigReq) throws AppstatusRestException
	{
		return appStatusService.changeNetworkInterface(ipConfigReq.getOldIPAddress(), ipConfigReq.getNewIPAddress());
	}

	@Override
	public NullResponse syncLDAPConfig(LDAPConfig ldapConfig) {

		return null;
	}

	@Override
	public CloudManagerEntityIdResponse updateCloudManager(
			CloudManagerEntity cloudManagerEntity) {
		log.trace("");
		return desktopManagement.updateCloudManager(cloudManagerEntity);
		
	}
	@Override
	public ListTraceRes listTrace(TraceReq user) {
		log.trace("");
		return userManagement.listTrace(user);
	}
}
