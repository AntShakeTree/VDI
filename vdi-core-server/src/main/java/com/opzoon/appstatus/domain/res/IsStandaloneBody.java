package com.opzoon.appstatus.domain.res;

public class IsStandaloneBody<T> extends Body<T> {

	private boolean isStandalone;

	public boolean isStandalone() {
		return isStandalone;
	}

	public void setStandalone(boolean isStandalone) {
		this.isStandalone = isStandalone;
	}
	
}
