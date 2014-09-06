package com.vdi.support.desktop.lls.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include=Inclusion.NON_DEFAULT)
public class Where{
	private Integer limit,offset;
	private String orderBy;
	public String getOrderBy() {
		return orderBy;
	}

	public Where setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	
	public Integer getLimit() {
		return limit;
	}

	public Where setLimit(Integer limit) {
		this.limit = limit;
		return this;
	}

	public Integer getOffset() {
		return offset;
	}

	public Where setOffset(Integer offset) {
		this.offset = offset;
		return this;
	}
	
}
