/**  
* @title: VDIcloudWS v04 Json.java 
* @package com.opzoon.client.domain
* @author maxiaochao
* @date 2012-9-10
* @version V04 
*/
package com.opzoon.ohvc.common.anotation;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * JSON验证器
 * @ClassName: Json.java
 * @Description: Json.java 
 * @author: maxiaochao 
 * @date: 2012-9-10
 * @version: V04 
 */
@Retention(RUNTIME)
@Target(value={FIELD,METHOD,TYPE})
public @interface Json {

	String name() default "";
	
	Class<?> type() default String.class;

	String parentName() default "";

	
}
