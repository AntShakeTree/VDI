/**
 * 
 */
package com.opzoon.ohvc.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.opzoon.ohvc.common.anotation.Required;

/**
 * 自动校验器
 * @author maxiaochao
 * @version V04 2012-9-6
 */
public class Validator {
    /**
     * opzoon 自定义注解方式校验器
     * <p>e.g: //使用该校验器必须继承 BaseDomain实体类 
     * 			 User user = new User();
     * 			 user.setUsername("opzoon");
     * 			 user.setPassword("password");
     *   		 user.validate();	 	
     * </p>
     * @param t
     * @return
     */
    public static Object validate(Object t) {
	Field[] fields = t.getClass().getDeclaredFields();
	Object value = null;
	for (Field f : fields) {

	    if (f.getAnnotation(Required.class) != null) {
		String fileNanme = f.getName();
		String firstLetter = fileNanme.substring(0, 1).toUpperCase(); // 获得字段第一个字母大写
		try {
		Method method = t.getClass().getMethod(
			"get" + firstLetter + fileNanme.substring(1));

		
		    value = method.invoke(t, new Object[] {});
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();

		}
		if (value == null) {
		    throw new RuntimeException(
			    "The field value must  not null!");
		}

	    }
	}
	return t;
    }
    private Validator(){} 
}
