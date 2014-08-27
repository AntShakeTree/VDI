/**  
* @title: VDIcloudWS v04 DistributionStrategy.java 
* @package com.opzoon.client.opzooncloud.domain
* @author maxiaochao
* @date 2012-9-12
* @version V04 
*/
package com.opzoon.ohvc.driver.opzooncloud.domain;



/**
 * @ClassName: DistributionStrategy.java
 * @Description: DistributionStrategy.java 
 * @author: maxiaochao 
 * @date: 2012-9-12
 * @version: V04 
 */
public class DistributionStrategy {
	private Nodefilter node_filter;
	private String optimization="load_balance";
	private String vm_relationship="none";
	
	public Nodefilter getNode_filter() {
		return node_filter;
	}
	public DistributionStrategy setNode_filter(Nodefilter node_filter) {
		this.node_filter = node_filter;
		return this;
	}
	public String getVm_relationship() {
		return vm_relationship;
	}
	public void setVm_relationship(String vm_relationship) {
		this.vm_relationship = vm_relationship;
	}
	public String getOptimization() {
		return optimization;
	}
	public void setOptimization(String optimization) {
		this.optimization = optimization;
	}
	public static DistributionStrategy getInstance(){
		return new DistributionStrategy().setNode_filter(new DistributionStrategy().new Nodefilter());
	}
	class Nodefilter{
		
		private boolean all=true;

		public boolean isAll() {
			return all;
		}

		public void setAll(boolean all) {
			this.all = all;
		}
	}
}

