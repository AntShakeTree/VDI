/**
 * 
 */
package com.opzoon.ohvc.driver.opzooncloud.service;

import com.opzoon.vdi.core.cloud.CloudManager;


/**
 * @author maxiaochao
 * @version V04
 */
public interface OpzoonCloudVMInstanceService extends CloudManager{
   





    /**
     * 创建一个实例
     * 
     * @param string
     *            :must be json type. eg{"a":"b"}
     * @return JSON 格式的数据
     */
    String createVMInstanceByJSONString(String jsonString ) throws Exception;

  
}
