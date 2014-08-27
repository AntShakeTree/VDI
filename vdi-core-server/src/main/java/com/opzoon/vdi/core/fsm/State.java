package com.opzoon.vdi.core.fsm;

import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.facade.DatabaseFacade;

public interface State
{

  boolean accepts(DatabaseFacade databaseFacade, Request request);

  State accept(DatabaseFacade databaseFacade, Request request) throws CommonException;

}
