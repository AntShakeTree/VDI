package com.vdi.facade;

import com.vdi.dao.user.domain.LdapConfigEntity;
import com.vdi.vo.req.LdapConfigIdReq;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.LdapConfigResponse;
import com.vdi.vo.res.ListLdapConfigRespones;

public interface LdapConfigEntityFacad {
    LdapConfigResponse	addLdapConfigEntity(LdapConfigEntity config);
	Header removeLdapConfigEntity(LdapConfigIdReq id);
	LdapConfigResponse updateLdap(LdapConfigEntity config);
	ListLdapConfigRespones listLdapConfigEntitys(LdapConfigEntity config);
}
