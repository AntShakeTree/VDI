package com.opzoon.vdi.core.ws.vo.admin.user;

import java.io.Serializable;
import java.util.List;

import com.opzoon.vdi.core.domain.RSAKey;
import com.opzoon.vdi.core.ws.Services.CommonList;

public class StandardRSAKeyList extends CommonList<RSAKey> implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<RSAKey> list;
  
  @Override
  public List<RSAKey> getList() {
    return list;
  }
  @Override
  public void setList(List<RSAKey> list) {
    this.list = list;
  }
  
}