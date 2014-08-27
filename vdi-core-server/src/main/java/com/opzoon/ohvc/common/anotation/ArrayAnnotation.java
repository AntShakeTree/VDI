package com.opzoon.ohvc.common.anotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 此注解表示子域是数组
 * @author tanyunhua
 * @date: 2012-9-24
 * @version: V04 
 */
@Retention(RUNTIME)
@Target({FIELD})
public @interface ArrayAnnotation {

}
