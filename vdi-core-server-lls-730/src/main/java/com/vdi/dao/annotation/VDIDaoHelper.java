package com.vdi.dao.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Retention(RUNTIME)
@Target({ElementType.METHOD})
public @interface VDIDaoHelper {
	String value() default "";
	IgnoreValue ignore() default IgnoreValue.DEFAULT;
	
	public enum IgnoreValue {
		 DEFAULT,ZERO,NULLCOLLECTION;
	}
}
