package com.vdi.dao.suport;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

import com.vdi.common.ConfigUtil;
import com.vdi.common.ParseJSON;
import com.vdi.dao.GenericsUtils;
import com.vdi.dao.PageRequest;
import com.vdi.dao.Request;




public  class QueryUtil {

	private String hql;
	private List<Object> values;

	 QueryUtil(String hql, List<Object> values) {
		super();
		this.hql = hql;
		this.values = values;
	}

	public String getHql() {
		return hql;
	}

	public List<Object> getValues() {
		return values;
	}

	/**
	 * @param <T>
	 * @return
	 * @Title: getHqlByDomain
	 * @Description: 获得hql
	 * @param @param req 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static <T> QueryUtil getHqlByDomain(Request<T> req) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		BeanWrapper bean = new BeanWrapperImpl(req);
		PropertyDescriptor[] pros = bean.getPropertyDescriptors();
		StringBuffer hql = new StringBuffer();
		hql.append(" from ");
		hql.append(GenericsUtils.getMethodParameterGenericsInterfaceType(req).getSimpleName());
		hql.append(" where ");
		List<Object> strs = new ArrayList<Object>();
		for (PropertyDescriptor propertyDescriptor : pros) {
			Method methodGetX = propertyDescriptor.getReadMethod(); // Read对应get()方法
			Method methodSet = propertyDescriptor.getWriteMethod();
			String properyName = propertyDescriptor.getName();
			if (methodGetX != null && methodSet != null) {
				Object reValue = methodGetX.invoke(req);
				if (reValue == null||(reValue+"").equals("")) {
					continue;
				}
				if (isContainsPageProperty(properyName)) {
					continue;
				}
				strs.add(reValue);
				hql.append(" " + propertyDescriptor.getName()).append(" = ? ").append("and ");
			}

		}
		hql.append(" 1=1 ");
		if(req instanceof PageRequest)
		if (!StringUtils.isEmpty(((PageRequest<T>)req).getSortkey())) {
			hql.append(" order by ").append(((PageRequest<T>)req).getSortkey());
			if (((PageRequest<T>)req).getAscend() == 1) {
				hql.append(" asc");
			} else {
				hql.append(" desc");
			}
		}
		return new QueryUtil(hql.toString(), strs);
	}
	public static boolean isContainsPageProperty(String properyName) {
		String[] sts = ConfigUtil.getKey("page.property").split(",");
		for (String string : sts) {
			if (properyName.equalsIgnoreCase(string)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hql == null) ? 0 : hql.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QueryUtil other = (QueryUtil) obj;
		if (hql == null) {
			if (other.hql != null)
				return false;
		} else if (!hql.equals(other.hql))
			return false;
		String vs =checkVs();
		if (vs == null) {
			if (other.checkVs() != null)
				return false;
		} else if (!vs.equals(other.checkVs()))
			return false;
		return true;
	}
	private  String checkVs(){
		return ParseJSON.toJson(this.values);
	}

}
