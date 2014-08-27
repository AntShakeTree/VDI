/**   
 * Title: ClusterConfigure.java 
 * Package com.opzoon.appstatus.domain 
 * Description: 配置解析对象  
 * @author    
 * @date 2013-7-19 下午4:41:25 
 * @version V2.0   
 */
package com.opzoon.appstatus.domain;

/**
 * Title: ClusterConfigure.java Package com.opzoon.appstatus.domain Description:
 * 配置解析对象
 * 
 * @author
 * @date 2013-7-19 下午4:41:25
 * @version V0.2.1023（迭代3）
 */
public class ClusterConfigure
{
	// 节点地址 
	private String nodeAddress;
	private int serviceType;
	private long myid;

	/**
	 * @return nodeAddress
	 */
	public String getNodeAddress()
	{
		return nodeAddress;
	}

	/**
	 * @param nodeAddress
	 *            the nodeAddress to set
	 */
	public void setNodeAddress(String nodeAddress)
	{
		this.nodeAddress = nodeAddress;
	}

	/**
	 * @return serviceType
	 */
	public int getServiceType()
	{
		return serviceType;
	}

	/**
	 * @param serviceType
	 *            the serviceType to set
	 */
	public void setServiceType(int serviceType)
	{
		this.serviceType = serviceType;
	}

	/**
	 * @return myid
	 */
	public long getMyid()
	{
		return myid;
	}

	/**
	 * @param myid
	 *            the myid to set
	 */
	public void setMyid(long myid)
	{
		this.myid = myid;
	}

	/*
	 * (非 Javadoc) <p>Title: toString</p> <p>Description: </p>
	 * 
	 * @return
	 * 
	 * @see java.lang.Object#toString()
	 */

	@Override
	public String toString()
	{
		return "ClusterConfigure [nodeAddress=" + nodeAddress + ", serviceType=" + serviceType + ", myid=" + myid + "]";
	}

}
