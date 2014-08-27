/**  
 * @title: VDIcloudWS v04 OpzoonCloudVMInstanceList.java 
 * @package com.opzoon.client.opzooncloud.domain
 * @author maxiaochao
 * @date 2012-9-10
 * @version V04 
 */
package com.opzoon.ohvc.driver.opzooncloud.domain;

import java.util.List;

import org.json.JSONArray;

import com.opzoon.ohvc.common.anotation.Json;
import com.opzoon.ohvc.common.anotation.Required;
import com.opzoon.vdi.core.domain.BaseDomain;

/**
 * @ClassName: OpzoonCloudVMInstanceList.java
 * @Description: OpzoonCloudVMInstance 创建VM实例的对象生成
 * @author: maxiaochao
 * @date: 2012-9-10
 * @version: V04
 */
public class OpzoonCloudVMInstanceList extends
	BaseDomain<OpzoonCloudVMInstanceList> {
    @Required
    @Json
    private DistributionStrategy distribution_strategy;
    @Required
    @Json
    private List<OpzoonCloudVMInstance> instances;
    @Required
    @Json
    private  List<OpzoonCloudRelationship> relationships;
    private JSONArray result;
    /**
     * @return Returns the distribution_strategy.
     */
    public DistributionStrategy getDistribution_strategy() {
	return distribution_strategy;
    }

    /**
     * @return Returns the instances.
     */
    public List<OpzoonCloudVMInstance> getInstances() {
        return instances;
    }

    /**
     * @return Returns the relationships.
     */
    public List<OpzoonCloudRelationship> getRelationships() {
        return relationships;
    }

    /**
     * @param distribution_strategy The distribution_strategy to set.
     */
    public void setDistribution_strategy(DistributionStrategy distribution_strategy) {
        this.distribution_strategy = distribution_strategy;
    }

    /**
     * @param instances The instances to set.
     */
    public void setInstances(List<OpzoonCloudVMInstance> instances) {
        this.instances = instances;
    }

    /**
     * @param relationships The relationships to set.
     */
    public void setRelationships(List<OpzoonCloudRelationship> relationships) {
        this.relationships = relationships;
    }

	public JSONArray getResult() {
		return result;
	}

	public void setResult(JSONArray result) {
		this.result = result;
	}



}
