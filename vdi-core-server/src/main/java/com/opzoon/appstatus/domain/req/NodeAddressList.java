/**   
 * @Title: NodeAddressList.java 
 * Package com.opzoon.appstatus.common 
 * Description: 
 * @author David   
 * @date 2013-7-23 下午6:56:25 
 * @version V1.0   
 */
package com.opzoon.appstatus.domain.req;

import java.util.List;

import com.opzoon.appstatus.domain.Node;

/**
 * ClassName: NodeAddressList Description:
 * 
 * @author david
 * @date 2013-7-23 下午6:56:25
 */
public class NodeAddressList
{
	private List<Node> nodes;

	/**
	 * @return nodes
	 */
	public List<Node> getNodes()
	{
		return nodes;
	}

	/**
	 * @param nodes
	 *            the nodes to set
	 */
	public void setNodes(List<Node> nodes)
	{
		this.nodes = nodes;
	}

	@Override
	public String toString()
	{
		return "NodeAddressList [nodes=" + nodes + "]";
	}

}
