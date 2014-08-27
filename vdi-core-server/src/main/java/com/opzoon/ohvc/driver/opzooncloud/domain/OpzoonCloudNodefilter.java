/**  
 * @title: VDIcloudWS v04 OpzoonCloudNodefilter.java 
 * @package com.opzoon.client.opzooncloud.domain
 * @author maxiaochao
 * @date 2012-9-12
 * @version V04 
 */
package com.opzoon.ohvc.driver.opzooncloud.domain;

import java.util.List;

import com.opzoon.vdi.core.domain.BaseDomain;

/**
 * @ClassName: OpzoonCloudNodefilter.java
 * @Description: OpzoonCloudNodefilter.java
 * @author: maxiaochao
 * @date: 2012-9-12
 * @version: V04
 */
public class OpzoonCloudNodefilter extends BaseDomain<OpzoonCloudNodefilter> {
    private boolean all;
    private List<String> discard_nodes;

    /**
     * @return Returns the all.
     */
    public boolean isAll() {
	return all;
    }

    /**
     * @return Returns the discard_nodes.
     */
    public List<String> getDiscard_nodes() {
	return discard_nodes;
    }

    /**
     * @param all
     *            The all to set.
     */
    public OpzoonCloudNodefilter setAll(boolean all) {
	this.all = all;
	return this;
    }

    /**
     * @param discard_nodes
     *            The discard_nodes to set.
     */
    public OpzoonCloudNodefilter setDiscard_nodes(List<String> discard_nodes) {
	this.discard_nodes = discard_nodes;
	return this;
    }

}
