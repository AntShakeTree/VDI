package com.vdi.facade;

import com.vdi.vo.res.Header;
import com.vdi.vo.res.LdapConfigResponse;
import com.vdi.vo.res.ListLdapConfigRespones;

public interface LdapConfigFacad {
    LdapConfigResponse	addLDAPConfig();
	Header removeLDAPConfig();
	Header updateLdap();
	Header verifyLdap();
	Header configLDAPSynchronizingInterval();
	ListLdapConfigRespones listLDAPConfigs();
}
