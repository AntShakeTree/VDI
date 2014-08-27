package com.opzoon.ohvc.driver.opzooncloud.request;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.opzoon.ohvc.common.JSONObjectUtils;
import com.opzoon.ohvc.driver.opzooncloud.domain.OpzoonCloudVMInstance;
import com.opzoon.ohvc.driver.opzooncloud.domain.OpzoonCloudVMInstanceAttributes;
import com.opzoon.ohvc.driver.opzooncloud.domain.OpzoonCloudVMInstanceList;

/**
 * 返回结果解析类
 * 
 * @author maxiaochao
 * @version V04
 */
public class OpzoonParseResponseResult {

	/**
	 * 工具类不需要实例
	 */
	private OpzoonParseResponseResult() {
	}
	/**
	 * 解析instance result
	 * 
	 * @param 查询出的结果字符串
	 * @throws Exception
	 */
	public static OpzoonCloudVMInstanceList parseOpzoonCloudVMInstanceList(
			String resultData) throws Exception {

		OpzoonCloudVMInstanceList vs = JSONObjectUtils.parseOpzoonInstance(resultData,
				OpzoonCloudVMInstanceList.class);
		JSONArray jsonArray = vs.getResult();
		List<OpzoonCloudVMInstance> ls = new ArrayList<OpzoonCloudVMInstance>();
		vs.setInstances(ls);
		for (int i = 0; i < jsonArray.length(); i++) {
			OpzoonCloudVMInstance instance = JSONObjectUtils.parseOpzoonInstance(
					jsonArray.get(i).toString(), OpzoonCloudVMInstance.class);
			//现有的模板规则 
			
				instance.setIpaddress(instance.getIp());
				ls.add(instance);
				OpzoonCloudVMInstanceAttributes attributes = JSONObjectUtils.parseOpzoonInstance(instance.getAttributes().toString(),OpzoonCloudVMInstanceAttributes.class);
				instance.setDisplay_protoco(attributes.getDisplay_protocol());
		}
		
		return vs;
	}
	/**
	 * 
	 * @param jsonResult
	 * @return
	 * @throws Exception
	 * @return String
	 * @author：maxiaochao 2012-9-20 下午1:42:05
	 */
	public static OpzoonCloudVMInstance parseOpzoonCloudQueryInstance(
			String jsonResult) throws Exception {
		OpzoonCloudVMInstance vmInstance = JSONObjectUtils.parseOpzoonInstance(jsonResult,
				OpzoonCloudVMInstance.class);
		// 转换
		vmInstance.setIpaddress(vmInstance.getIp());
		vmInstance.setId(vmInstance.getName());
		vmInstance.setName(vmInstance.getLabel());
		return vmInstance;
	}

	


}
