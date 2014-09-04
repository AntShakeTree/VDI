package com.vdi.facade;

import com.vdi.dao.user.domain.LdapConfig;
import com.vdi.service.user.LdapStateSubject;
import com.vdi.vo.req.LdapConfigIdReq;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.LdapConfigResponse;
import com.vdi.vo.res.ListLdapConfigRespones;

public interface LdapConfigFacad extends LdapStateSubject{
    LdapConfigResponse	addLDAPConfig(LdapConfig config);
	Header removeLDAPConfig(LdapConfigIdReq id);
	Header updateLdap(LdapConfig config);
	Header verifyLdap(LdapConfigIdReq req);
	Header configLDAPSynchronizingInterval(LdapConfigIdReq req);
	ListLdapConfigRespones listLDAPConfigs(LdapConfig config);
}
