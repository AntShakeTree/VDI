package com.opzoon.vdi.core.controller.executor;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opzoon.vdi.core.controller.TaskInfo;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.operation.AsyncDeleteDesktopPoolOperation;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;
import com.opzoon.vdi.core.operations.OperationRegistry;

public class OperationExecutor extends ExecutorBase {
  
  @Autowired
  private OperationRegistry operationRegistry;

	private static final Logger log = LoggerFactory.getLogger(OperationExecutor.class);

	@Override
	public ExecuteResult execute(TaskInfo task) {
	  try
    {
      Class<?> clazz = Class.forName(task.getPara1());
      Constructor<?> con = clazz.getConstructors()[0];
      int size = con.getParameterTypes().length;
      List<Object> ps = new LinkedList<Object>();
      if (size > 0)
      {
        addParam(ps, con.getParameterTypes()[0], task.getPara2());
        if (size > 1)
        {
          addParam(ps, con.getParameterTypes()[1], task.getPara3());
          if (size > 2)
          {
            addParam(ps, con.getParameterTypes()[2], task.getPara4());
            if (size > 3)
            {
              addParam(ps, con.getParameterTypes()[3], task.getPara5());
            }
          }
        }
      }
      Operation o = (Operation) con.newInstance(ps.toArray());
      final OperationContext operationContext = new OperationContext();
      operationContext.setOperationRegistry(operationRegistry);
      ExecuteResult result = new ExecuteResult();
      o.execute(operationContext);
      result.setErrorCode(0);
      return result;
    } catch (Exception e)
    {
      log.error("Exception", e);
      ExecuteResult result = new ExecuteResult();
      result.setErrorCode(1);// FIXME
      return result;
    }
	}

  private void addParam(List<Object> ps, Class<?> clazz, String para)
  {
    if (para == null)
    {
      ps.add(para);
      return;
    }
    if (String.class.isAssignableFrom(clazz))
    {
      ps.add(para);
    } else if (int.class.isAssignableFrom(clazz))
    {
      ps.add(Integer.parseInt(para));
    } else if (Integer.class.isAssignableFrom(clazz))
    {
      ps.add(Integer.valueOf(para));
    } else
    {
      ps.add(Boolean.parseBoolean(para));
    }
  }
	
}
