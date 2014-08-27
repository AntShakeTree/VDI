package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.ws.Services.MultiStatusResponse;

@XmlRootElement(name = "response")
public class RSAKeyIdsResponse extends MultiStatusResponse<RSAKeyIdAndError> implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private List<RSAKeyIdAndError> body;

  @Override
  public List<RSAKeyIdAndError> getBody() {
    return body;
  }
  @Override
  public void setBody(List<RSAKeyIdAndError> body) {
    this.body = body;
  }
  
}