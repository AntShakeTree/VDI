package com.opzoon.vdi.core.util;

import java.io.Serializable;

public class EntityId implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Class<?> type;
	private final int id;
	
	public EntityId(Class<?> type, int id) {
		this.type = type;
		this.id = id;
	}

	public Class<?> getType() {
		return type;
	}

	public int getId() {
		return id;
	}
	
}