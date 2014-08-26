//package com.vdi.desktop.lls.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.vdi.desktop.lls.domain.resource.ComputePool;
//import com.vdi.desktop.lls.services.ComputePoolService;
//
//@Controller
//public class ComputePoolController {
//	@Autowired
//	private ComputePoolService computePoolService;
//	@RequestMapping(value={"/getComputePool/{id}"},method={RequestMethod.GET})
//	public @ResponseBody ComputePool getComputePool(@PathVariable String computerPoolId){
//		return computePoolService.getComputePool(computerPoolId);
//	} 
//}
