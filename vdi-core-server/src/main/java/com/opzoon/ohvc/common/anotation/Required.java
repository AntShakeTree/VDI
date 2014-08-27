/**
 * 
 */
package com.opzoon.ohvc.common.anotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.opzoon.ohvc.common.RailAppError;
import com.opzoon.ohvc.common.Regular;

/**
 * 不能为空的数据
 * 
 * @author maxiaochao
 * @version V04 2012-9-6
 */
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface Required {
	// 用系统中已知的规则
	Regular[] regular() default Regular.NULL;

	String daoName() default "";

	// 自定义正在表达式
	String RegExp() default "";

	String name() default "";

	Class<?> type() default String.class;

	int min() default -1;
	int length() default 0;

	RailAppError error() default RailAppError.DEFAULT;
}
