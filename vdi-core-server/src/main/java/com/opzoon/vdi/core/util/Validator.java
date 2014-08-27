/**
 * 
 */
package com.opzoon.vdi.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.apache.log4j.Logger;

import com.opzoon.ohvc.common.OpzoonUtils;
import com.opzoon.ohvc.common.RailAppError;
import com.opzoon.ohvc.common.Regular;
import com.opzoon.ohvc.common.anotation.DaoName;
import com.opzoon.ohvc.common.anotation.Required;
import com.opzoon.ohvc.domain.Head;
import com.opzoon.vdi.core.facade.ValidatorManager;

/**
 * 自动校验器
 * 
 * @author maxiaochao
 * @version V04 2012-9-6
 */
public class Validator {
	private  Logger log = Logger.getLogger(Validator.class);

	/**
	 * opzoon 自定义注解方式校验器
	 * 
	 * @param <T>
	 *            object
	 * @param <T>
	 *            RailResponse 返回值
	 * @param <T>
	 *            Object 校验
	 * @param t
	 * @return boolean
	 */
	public  boolean validate(Object t, Head head) {
		Class<?> clazz = t.getClass();
		Field[] fields = clazz.getDeclaredFields();
		Object value = null;
		head.setError(0);
		DaoName daoName = clazz.getAnnotation(DaoName.class);

		try {
			for (Field f : fields) {
				Required required = f.getAnnotation(Required.class);
				if (required != null) {
					String fieldName = f.getName();
					String firstLetter = fieldName.substring(0, 1).toUpperCase(); // 获得字段第一个字母大写
					Method method = t.getClass().getMethod("get" + firstLetter + fieldName.substring(1));
					value = method.invoke(t, new Object[] {});

					RailAppError error = required.error();
					if (error.equals(RailAppError.DEFAULT)) {
						error = RailAppError.fromByPropertyName(fieldName);
					}
					String name = t.getClass().getSimpleName();
					if (daoName != null) {
						name = daoName.name();
					}
					boolean success = isSuccessByRequired(required, name, fieldName, value);
					if (!success) {
						if (value != null) {
							value = value + "(请核实你输入的信息是否出现重复或者超出长度.)";
						}
						head.setError(error.getError()).setMessage(MessageFormat.format(error.getMessage(), "[" + value + "]"));
						return false;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			head.setError(0x80000001);
			head.setMessage("未知错误");
			return false;
		}
		return true;
	}

	/**
	 * @Title: handleRegular
	 * @Description: 处理规则引擎
	 * @param @param required
	 * @param @param simpleName
	 * @param @param fieldName
	 * @param @param value
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	private  boolean handleRegular(Required required, String simpleName, String fieldName, Object value) {
		Regular[] res = required.regular();
		if (res == null) {
			return true;
		}
		if (res.length < 1) {
			return true;
		}
		boolean isSu = false;
		boolean isContantInetAddress = false;
		for (Regular regular : res) {

			switch (regular) {
			case NULL:
				isSu = true;
				break;
			case INETADDRESS:
				isSu = OpzoonUtils.isInetAddress(value + "");
				isContantInetAddress = true;
				break;
			case UNREPEATABLE:
				if (isContantInetAddress) {
					value = OpzoonUtils.covertDomainToIp(value.toString());
					isSu = !isRepeatableIp(simpleName, fieldName, value);
				} else {
					isSu = !isRepeatable(simpleName, fieldName, value);
				}
				break;
			case EXIST:
				isSu = isRepeatable(simpleName, fieldName, value);
				break;
			default:
				break;
			}
		}

		return isSu;
	}

	/**
	 * @Title: isRepeatableIp
	 * @Description: 是否存在innetAddress
	 * @param @param simpleName
	 * @param @param fieldName
	 * @param @param value
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public  boolean isRepeatableIp(String simpleName, String fieldName, Object value) {

		return validatorManager.isRepeatableIP(simpleName, fieldName, value);
	}

	/**
	 * @Title: handleRegular
	 * @Description: 是否存在 重复
	 * @param @param simpleName
	 * @param @param fieldName
	 * @param @param value
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public  boolean isRepeatable(String simpleName, String fieldName, Object value) {
		return validatorManager.isRepeatable(simpleName, fieldName, value);
	}

	/**
	 * @Title: handleNULL
	 * @Description: 处理空值
	 * @param regular
	 * @param value
	 * @return boolean
	 * @throws
	 */
	private  boolean handleNULL(Object value) {
		if (value == null)
			return false;
		return org.apache.commons.lang.StringUtils.isNotEmpty(value + "");
	}

	/**
	 * @Title: handleRegExp
	 * @Description: 处理正则
	 * @param regular
	 * @param value
	 * @return boolean
	 * @throws
	 */
	private  boolean handleRegExp(String regExp, Object value) {
		if (org.apache.commons.lang.StringUtils.isEmpty(regExp))
			return true;
		return OpzoonUtils.matches(value.toString(), regExp);
	}

	/**
	 * @Title: handleMin
	 * @Description: 处理最小值
	 * @param regular
	 * @param value
	 * @return boolean
	 * @throws
	 */
	private  boolean handleMin(int min, Object value) {
		int va = 0;
		try {
			va = Integer.parseInt("" + value);
		} catch (Exception e) {
		}
		if (min == -1) {
			return true;
		}
		return va > min;
	}
	
	/**
	 * @Title: 处理所有验证 必填项规则是否是成功的
	 * @Description:处理所有验证
	 * @param required
	 * @param value
	 * @return boolean
	 * @throws
	 */
	private  boolean isSuccessByRequired(Required required, String name, String fieldName, Object value) {
		String regExp = required.RegExp();
		boolean success = false;
		int min = required.min();
		// 操作数据库的类名
		String daoName = required.daoName();
		String filedName = required.name();
		if (org.apache.commons.lang.StringUtils.isNotEmpty(daoName)) {
			name = daoName;
		}
		if (org.apache.commons.lang.StringUtils.isNotEmpty(filedName)) {
			fieldName = filedName;
		}
		success = handleNULL(value);
		if (!success) {
			return success;
		}
		boolean reg = handleRegular(required, name, fieldName, value);
		log.info("handleRegular ::" + reg);
		boolean exp = handleRegExp(regExp, value);
		log.info("handleRegExp ::" + exp);
		boolean minb = handleMin(min, value);
		log.info("handleMin ::" + minb);
		return reg && exp && minb;
	}

	private ValidatorManager validatorManager;

	/**
	 * @param validatorManager the validatorManager to set
	 */
	public void setValidatorManager(ValidatorManager validatorManager) {
		this.validatorManager = validatorManager;
	}
	
}
