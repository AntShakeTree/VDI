/**  
* @title: VDIcloudWS v04 MD5.java 
* @package com.opzoon.client.cloudstack.domain
* @author maxiaochao
* @date 2012-9-13
* @version V04 
*/
package com.opzoon.ohvc.common.anotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * MD5 验证器
 * @ClassName: MD5.java
 * @Description: MD5.java 
 * @author: maxiaochao 
 * @date: 2012-9-13
 * @version: V04 
 */
@Retention(RUNTIME)
@Target({FIELD,METHOD})
public @interface MD5 {

}
