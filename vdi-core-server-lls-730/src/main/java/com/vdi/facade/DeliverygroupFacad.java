package com.vdi.facade;

import com.vdi.dao.user.domain.DeliveryGroup;
import com.vdi.vo.req.DeligroupIdReq;
import com.vdi.vo.req.DeliveryAndOriganzationIdReq;
import com.vdi.vo.req.DeliveryAndUserIdReq;
import com.vdi.vo.res.DeliveryGroupResponse;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.ListGroupsResponse;

public interface DeliverygroupFacad {
	DeliveryGroupResponse createGroup(DeliveryGroup group);
	Header updateGroup(DeliveryGroup group);//
	Header deleteGroup(DeligroupIdReq req);
	ListGroupsResponse listGroups(DeliveryGroup group);
	DeliveryGroupResponse getGroup(DeligroupIdReq req);
	
	//
	Header addUserToGroup(DeliveryAndUserIdReq req);
	Header removeUserFromGroup(DeliveryAndUserIdReq req);
	Header addOriganazationToGroup(DeliveryAndOriganzationIdReq req);	
	Header removeOriganzationFromGroup(DeliveryAndOriganzationIdReq req);
}
