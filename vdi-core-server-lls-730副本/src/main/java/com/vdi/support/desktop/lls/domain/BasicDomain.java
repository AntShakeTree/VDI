package com.vdi.support.desktop.lls.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

//@JsonIgnoreType()

@JsonSerialize(include=Inclusion.NON_DEFAULT)
public class BasicDomain {
	
	private Integer errorCode;
	private String result;
	private Object content;
	private Integer limit;
	private String	orderBy;
	private Integer offset;
	private String key;
	
	public String getKey() {
		return key;
	}



	public void setKey(String key) {
		this.key = key;
	}



	public Integer getLimit() {
		return limit;
	}



	public void setLimit(Integer limit) {
		this.limit = limit;
	}



	public String getOrderBy() {
		return orderBy;
	}



	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}



	public Integer getOffset() {
		return offset;
	}



	public void setOffset(Integer offset) {
		this.offset = offset;
	}



	public Object getContent() {
		return content;
	}



	public Object Object () {
		return content;
	}

	

	public void setContent(Object content) {
		this.content = content;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}



	@Override
	public String toString() {
		return "BasicDomain [errorCode=" + errorCode + ", result=" + result
				+ ", content=" + content + ", limit=" + limit + ", orderBy="
				+ orderBy + ", offset=" + offset + ", key=" + key + "]";
	}
	
}
