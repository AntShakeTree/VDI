package com.opzoon.appstatus.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NodeUpdate {

	 private Long id;
	 
	 private String[] columnNames;
	 
	 private Object[] columnValues;
	 private Map<String ,Object> where=new HashMap<String, Object>();
	 private Map<String ,Object> notWhere=new HashMap<String, Object>();

	 public Map<String, Object> getWhere() {
		return where;
	}

	public void setWhere(Map<String, Object> where) {
		this.where = where;
	}

	public Map<String, Object> getNotWhere() {
		return notWhere;
	}

	public void setNotWhere(Map<String, Object> notWhere) {
		this.notWhere = notWhere;
	}

	public NodeUpdate() {
		 
	 }
	 
	 public NodeUpdate(Long id, String[] columnNames, Object[] columnValues) {
		 this.id = id;
		 this.columnNames = columnNames;
		 this.columnValues = columnValues;
	 }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

	public Object[] getColumnValues() {
		return columnValues;
	}

	public void setColumnValues(Object[] columnValues) {
		this.columnValues = columnValues;
	}

	@Override
	public String toString() {
		return "NodeUpdate [id=" + id + ", columnNames="
				+ Arrays.toString(columnNames) + ", columnValues="
				+ Arrays.toString(columnValues) + "]";
	}
	 
}
