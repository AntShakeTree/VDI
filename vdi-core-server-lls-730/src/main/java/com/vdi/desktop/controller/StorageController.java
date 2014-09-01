package com.vdi.desktop.controller;

import static com.vdi.controller.BaseController.CONTEXT_TYPE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdi.dao.desktop.domain.StorageEntity;
import com.vdi.facade.StorageFacad;
import com.vdi.vo.req.StorageIdReq;
import com.vdi.vo.res.ListStorage;
import com.vdi.vo.res.StorageResponse;

@Controller 
public class StorageController {
	private @Autowired StorageFacad storageFacad;
	@RequestMapping(value="/listStorages",method=RequestMethod.POST,produces={CONTEXT_TYPE},consumes={CONTEXT_TYPE})
	public @ResponseBody ListStorage listStorage(StorageEntity entity){
		return storageFacad.listStorage(entity);
	}
	@RequestMapping(value="/getStorage",method=RequestMethod.POST,produces={CONTEXT_TYPE},consumes={CONTEXT_TYPE})
	public StorageResponse getStorage(StorageIdReq req){
		return storageFacad.getStorage(req);
	}
}
