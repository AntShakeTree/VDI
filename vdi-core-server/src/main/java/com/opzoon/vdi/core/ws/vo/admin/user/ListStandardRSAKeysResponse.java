package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.ws.Services.Response;

@XmlRootElement(name = "response")
public class ListStandardRSAKeysResponse extends Response<StandardRSAKeyList> implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private StandardRSAKeyList body;
  
  public StandardRSAKeyList getBody() {
    return body;
  }
  public void setBody(StandardRSAKeyList body) {
    this.body = body;
  }
  
}