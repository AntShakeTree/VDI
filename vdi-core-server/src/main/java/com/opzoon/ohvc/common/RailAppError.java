/**   
 * @Title: RailAppError.java 
 * @Package com.opzoon.vdi.core.util 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author david   
 * @date 2013-1-22 上午11:29:29 
 * @version V1.0   
 */
package com.opzoon.ohvc.common;

/**
 * ClassName: RailAppError
 * @Description: 虚拟应用错误码
 * @author david
 * @date 2013-1-22 上午11:29:29
 */
public enum RailAppError {
	RAIL_APP_SERVER_NAME_ERR(0x80000120, "servername", "虚拟应用服务器地址信息传递不正确,您传递的参数为{0}"), RAIL_APP_SERVER_TYPE_ERR(0x80000121, "servertype",
			"虚拟应用服务器类型传递不正确,您传递的参数为{0}"), RAIL_APP_SERVER_ID_ERR(0x80000122, "idapplicationserver", "虚拟应用服务器ID传递不正确,您传递的参数为{0}"), RAIL_APP_ID_ERR(0x80000123,
			"", "虚拟应用ID不正确,您传递的参数为{0}"), RAIL_APP_PATH_ERR(0x80000124, "applicationpath", "虚拟应用路径不正确,您传递的参数为{0}"), RAIL_USER_ID_ERR(0x80000125, "",
			"用户信息不正确,您传递的参数为{0}"), RAIL_ORI_ID_ERR(0x80000126, "", "组织单元信息不正确,您传递的参数为{0}"), RAIL_GROUP_ID_ERR(0x80000127, "", "用户组信息不正确,您传递的参数为{0}"), RAIL_ERR(
			0x80000001, "null", "未知错误"), DEFAULT(0, "", ""),RAIL_HAVED_DEL_ERR(0x80000128,"","虚拟应用已经被卸载"),RAIL_AGETN_NOTINSTALL_ROLE(0x80010007,"","虚拟应用服务器没有安装角色");
	
	
	private int error;
	private String propertyName;
	private String message;

	/**
	 * @return propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @param propertyName
	 *            the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * @return error
	 */
	public int getError() {
		return error;
	}

	/**
	 * @param error
	 *            the error to set
	 */
	public void setError(int error) {
		this.error = error;
	}

	/**
	 * @return message
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
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param name
	 * @param ordinal
	 */

	private RailAppError(int error, String propertyName, String message) {
		// TODO Auto-generated constructor stub
		this.error = error;
		this.propertyName = propertyName;
		this.message = message;
	}

	/**
	 * 通过属性名得到错误enum
	 * 
	 * @return: void
	 * @param string
	 */
	public static RailAppError fromByPropertyName(String string) {

		for (RailAppError r : values()) {
			if (r.getPropertyName().equalsIgnoreCase(string)) {
				return r;
			}
		}
		return RAIL_ERR;
	}

}
