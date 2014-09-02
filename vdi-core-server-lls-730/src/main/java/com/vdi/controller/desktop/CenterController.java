package com.vdi.controller.desktop;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdi.common.ConfigUtil;
import com.vdi.common.Constants;
import com.vdi.dao.desktop.domain.CenterEntity;
import com.vdi.vo.res.Header;
import com.vdi.vo.res.ListCenter;
import com.vdi.vo.res.ListCenter.CenterList;

import static com.vdi.controller.BaseController.CONTEXT_TYPE;

@Controller
public class CenterController {
	@RequestMapping(value="/listCenters",method=RequestMethod.POST,consumes={CONTEXT_TYPE},produces={CONTEXT_TYPE})
//	@PreAuthorize()
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public @ResponseBody ListCenter listCenter(@RequestBody CenterEntity entity){
		ListCenter center=new ListCenter();
		Header head=new Header();
		head.setError(0);
		CenterList body=new CenterList();
		List<CenterEntity> list=new ArrayList<CenterEntity>();
		CenterEntity e=new CenterEntity();
		e.setIdcenter(1);
		e.setCentername(Constants.LLS_SORCKET_ADDRESS);
		e.setAddress(Constants.LLS_SORCKET_ADDRESS);
		list.add(e);
		body.setList(list);
		center.setBody(body);
		center.setHead(head);
		return center;
	}
}
