package com.opzoon.ohvc.common.anotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 此注解为将子对象的域映射到自己的域上。
 * 解析在JSONObjectUtils类里。
 * 此注解只能作用于简单类型的域上
 * @author tanyunhua
 * @date: 2012-9-24
 * @version: V04 
 */
@Retention(RUNTIME)
@Target({FIELD})
public @interface Sub {
	public String name();
	public String subName();
}
