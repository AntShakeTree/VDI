package com.opzoon.ohvc.domain;

import com.opzoon.ohvc.common.ConfigUtil;
import com.opzoon.vdi.core.domain.BaseDomain;

/**
 * 底层错误信息解析类
 * 
 * @author maxiaochao
 * @version: V04
 * @since V04 2012-11-8
 */
public class ErrorMeesage extends BaseDomain<ErrorMeesage> {
	// {errorcode:503,"message":
	// "Can not do \"clone_source\" while \"clone_source\" is under operation.",
	// "reference": "13-02-002"}
	private String message;
	private String reference;
	/**
	 * 克隆错误码
	 */
	public final static String CLONE_ERRORCODE = ConfigUtil
			.getBykey("clone.errorcode") == null ? "13-02-002" : ConfigUtil
			.getBykey("clone.errorcode");
	public final static String CLONE_ERRORCODE_ZK="13-02-003"; 
	public static final String[] RESOURCE_ERROR = new String[]{"06-00-007"
		,"06-00-008","06-00-009","06-00-010","06-00-011","06-00-0012"
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((reference == null) ? 0 : reference.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		ErrorMeesage other = (ErrorMeesage) obj;
		if (reference == null) {
			if (other.reference != null)
				return false;
		} else if (!reference.equals(other.reference))
			return false;
		return true;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference
	 *            the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

}
