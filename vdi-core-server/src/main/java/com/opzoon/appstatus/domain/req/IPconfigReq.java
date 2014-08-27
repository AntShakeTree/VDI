/**
 * 
 */
package com.opzoon.appstatus.domain.req;

/**
 * @author ningyu
 *
 */
public class IPconfigReq
{
	private String oldIPAddress;
	
	private String newIPAddress;

	public String getOldIPAddress()
	{
		return oldIPAddress;
	}

	public void setOldIPAddress(String oldIPAddress)
	{
		this.oldIPAddress = oldIPAddress;
	}

	public String getNewIPAddress()
	{
		return newIPAddress;
	}

	public void setNewIPAddress(String newIPAddress)
	{
		this.newIPAddress = newIPAddress;
	}
}
