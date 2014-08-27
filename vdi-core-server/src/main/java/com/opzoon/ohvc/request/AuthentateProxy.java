/**  
 * @title: VDIcloudWS v04 AuthentateProxy.java 
 * @package com.opzoon.client.request
 * @author maxiaochao
 * @date 2012-9-18
 * @version V04 
 */
package com.opzoon.ohvc.request;

import com.opzoon.ohvc.domain.Certificate;
import com.opzoon.ohvc.domain.Login;

/**
 * @ClassName: AuthentateProxy.java
 * @Description: AuthentateProxy.java
 * @author: maxiaochao
 * @date: 2012-9-18
 * @version: V04
 */
public interface AuthentateProxy {

    public Certificate start(String ip, Login login) throws Exception;


}
