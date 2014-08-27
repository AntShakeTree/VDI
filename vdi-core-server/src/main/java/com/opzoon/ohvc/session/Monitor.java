package com.opzoon.ohvc.session;

import java.lang.reflect.Method;
import java.util.List;

public class Monitor {
	private String method;
	private String classname;
	private List<Object> parameters;

	public void setMethod(String method) {
		this.method = method;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}

	public String getMethod() {
		return method;
	}

	public String getClassname() {
		return classname;
	}

	public List<Object> getParameters() {
		return parameters;
	}

	public void monitor() {
		try {
			Object object = Class.forName(this.getClassname()).newInstance();
			List<Object> os = this.getParameters();
			Class<?>[] clazzs = new Class<?>[os.size()];
			for (int i = 0; i < clazzs.length; i++) {
				clazzs[i] = os.get(i).getClass();
			}
			Method m = Class.forName(this.getClassname()).getMethod(
					this.getMethod(), clazzs);
			m.invoke(object, os.toArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
