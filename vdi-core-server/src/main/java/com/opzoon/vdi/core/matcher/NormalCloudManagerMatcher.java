package com.opzoon.vdi.core.matcher;

import com.opzoon.vdi.core.domain.state.CloudManagerState;
import com.opzoon.vdi.core.fsm.State;
import com.opzoon.vdi.core.fsm.StateMatcher;

public class NormalCloudManagerMatcher implements StateMatcher
{

  @Override
  public boolean matches(State state)
  {
    CloudManagerState cloudManagerState = (CloudManagerState) state;
    return cloudManagerState.getPhase() == CloudManagerState.CLOUD_MANAGER_PHASE_NORMAL;
  }

}
