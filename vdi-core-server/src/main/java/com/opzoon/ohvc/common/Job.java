/*
 * Copyright reserved 2009 by roxinf Co. Ltd.     
 * com.opzoon.common
 * Author：maxiaochao
 * Date：2012-9-19
 */
package com.opzoon.ohvc.common;

import com.opzoon.ohvc.common.anotation.Required;

/**
 * Job Author：maxiaochao 2012-9-19 下午1:47:01
 */
public class Job<T>  {
	private JobStatus status=JobStatus.RUNNING;
	private int error;
	private T result;
	@Required
	private String id;
	private String name;
	private String ip;
	private volatile boolean isAgent=false;
	public Job()
	{
		
	}
	
    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Job other = (Job) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Job(Class classType){
        this.classType = classType;
    }
	
	private Class classType = null;
    
    public Class classType()
    {
    	return classType;
    }

	/**
	 * @return Returns the status.
	 */
	public JobStatus getStatus() {
		return status;
	}

	/**
	 * @return Returns the error.
	 */
	public int getError() {
		return error;
	}

	/**
	 * @return Returns the result.
	 */
	public T getResult() {
		return result;
	}

	/**
	 * @param status
	 *            The status to set.
	 */
	public Job<T> setStatus(JobStatus status) {
		this.status = status;
		return this;
	}

	/**
	 * @param error
	 *            The error to set.
	 */
	public Job<T> setError(int error) {
		this.error = error;
		return this;
	}

	/**
	 * @param result
	 *            The result to set.
	 */
	public Job<T> setResult(T result) {
		this.result = result;
		return this;
	}

	/**
	 * @return the isAgent
	 */
	public boolean isAgent() {
		return isAgent;
	}

	/**
	 * @param isAgent the isAgent to set
	 */
	public void setAgent(boolean isAgent) {
		this.isAgent = isAgent;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public Job<T> setId(String id) {
		this.id = id;
		return this;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	
}
