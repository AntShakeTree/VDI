package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.opzoon.vdi.core.domain.RSAKey;

@XmlRootElement(name = "param")
public class StandardRSAKeys implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<RSAKey> rsakeys;

  public List<RSAKey> getRsakeys()
  {
    return rsakeys;
  }

  public void setRsakeys(List<RSAKey> rsakeys)
  {
    this.rsakeys = rsakeys;
  }
  
}