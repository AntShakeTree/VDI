package com.vdi.common;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

public class VDIBeanUtils extends BeanUtils {
	public static void copyPropertiesByNotNull(Object source, Object target,String[] ignorePropertyStrs) {
		List<String> ignoreProperties = new ArrayList<String>();
		if(ignorePropertyStrs!=null&&ignorePropertyStrs.length>0){
			for (String string : ignorePropertyStrs) {
				ignoreProperties.add(string);
			}
		}
		PropertyDescriptor[] ps = getPropertyDescriptors(source.getClass());
		for (PropertyDescriptor propertyDescriptor : ps) {
			Object getV = null;
			try {
				Method method = propertyDescriptor.getReadMethod();
				if (method != null)
					getV = method.invoke(source);
				if (getV == null) {
					ignoreProperties.add(propertyDescriptor.getName());
				}
				if (getV != null && getV instanceof Number) {
					if ((getV + "").equals("0") || (getV + "").equals("0.0")) {
						ignoreProperties.add(propertyDescriptor.getName());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		if (ignoreProperties.size() != 0) {
			String[] is = new String[ignoreProperties.size()];
			for (int i = 0; i < ignoreProperties.size(); i++) {
				is[i] = ignoreProperties.get(i);
			}
		
			BeanUtils.copyProperties(source, target, is);
		} else
			BeanUtils.copyProperties(source, target);
	}

}
