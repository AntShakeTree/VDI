package com.opzoon.vdi.core.operation;

import com.opzoon.vdi.core.cloud.CloudManager;
import com.opzoon.vdi.core.cloud.CloudManagerHelper;
import com.opzoon.vdi.core.domain.CloudManagerEntity;
import com.opzoon.vdi.core.domain.Desktop;
import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.VMInstance;

public abstract class OperationHelper
{
  
  private OperationHelper() {}
  
  public static String loadIP(final CloudManagerEntity cme, final DesktopPoolEntity desktopPool, final Desktop desktop) throws Exception
  {
    if (desktopPool.getVmsource() == DesktopPoolEntity.DESKTOP_POOL_SOURCE_MANUAL)
    {
      return desktop.getIpaddress();
    } else
    {
      final CloudManager cloudManager = CloudManagerHelper.findCloudManager(cme);
      final VMInstance instance;
      try {
        instance = cloudManager.getVM(desktop.getVmid());
      } catch (Exception e) {
        throw e;
      }
      return instance.getIpaddress();
    }
  }

}
