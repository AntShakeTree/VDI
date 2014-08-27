package com.opzoon.appstatus.domain;

import java.util.List;

/**
 * PersistentMessage
 * @author maxiaochao
 *
 */
public class PersistentMessage extends AppstatusMessage{
	private String id;
	private String message;
	private String classname;
	private String method;
	private List<Object> parameters;
	
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getId() {
		return id;
	}
	public PersistentMessage setId(String id) {
		this.id = id;
		return this;
	}
	public String getMessage() {
		return message;
	}
	public PersistentMessage setMessage(String message) {
		this.message = message;
		return this;
	}
	public List<Object> getParameters() {
		return parameters;
	}
	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}
	@Override
	public String toString() {
		return "PersistentMessage [id=" + id + ", message=" + message
				+ ", classname=" + classname + ", method=" + method
				+ ", parameters=" + parameters + "]";
	}
	
	
}
