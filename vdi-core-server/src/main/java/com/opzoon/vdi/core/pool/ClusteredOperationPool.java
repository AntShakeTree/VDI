package com.opzoon.vdi.core.pool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.opzoon.appstatus.common.exception.AppstatusRestException;
import com.opzoon.appstatus.domain.OpzoonDistributeLock;
import com.opzoon.appstatus.facade.AppstatusDistributeLockService;
import com.opzoon.vdi.core.controller.Controller;
import com.opzoon.vdi.core.controller.TaskInfo;
import com.opzoon.vdi.core.controller.executor.ExecutorBase;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;

public class ClusteredOperationPool implements OperationPool
{

  private static Logger log = Logger
      .getLogger(ClusteredOperationPool.class);
  
  private Controller controller;
  private AppstatusDistributeLockService appstatusDistributeLockService;

  @Override
  public <R, E extends Exception> R synchronizedExecute(String name, SynchronizedCallable<R, E> synchronizedCallable) throws E
  {
    OpzoonDistributeLock opzoonDistributeLock = null;
//    OpzoonDistributeLock opzoonDistributeLock = new OpzoonDistributeLock(name);
//    try
//    {
//      appstatusDistributeLockService.acquired(opzoonDistributeLock);
//    } catch (AppstatusRestException e1)
//    {
//      // TODO Auto-generated catch block
//      log.warn("Exception", e1);
//    }
    try
    {
      return synchronizedCallable.call(opzoonDistributeLock);
    } catch (Exception e)
    {
      throw (E) e;
    } finally
    {
//      try
//      {
//        appstatusDistributeLockService.release(opzoonDistributeLock);
//      } catch (AppstatusRestException e1)
//      {
//        // TODO Auto-generated catch block
//        log.warn("Exception", e1);
//      }
    }
  }

  @Override
  public Set<Operation> findRunningOperations()
  {
    Set<Operation> runningOperations = new HashSet<Operation>();
    return runningOperations;
  }

  @Override
  public OperationProcessor createOperationProcessor(final Operation operation, final OperationContext operationContext, final boolean notifyingOperationMutex)
  {
    final SimpleOperationProcessor simpleOperationProcessor = new SimpleOperationProcessor(operation);
//    try
//    {
//      simpleOperationProcessor.setResult(operation.execute(operationContext));
//    } catch (CommonException e)
//    {
//      simpleOperationProcessor.setException(e);
//    }
    return simpleOperationProcessor;
  }

  @Override
  public void publishEvent(Event event)
  {
  }

  @Override
  public Set<Event> pullEvents(Operation operation)
  {
    return new HashSet<Event>();
  }

  public void setController(Controller controller)
  {
    this.controller = controller;
  }
  
  public void setAppstatusDistributeLockService(
      AppstatusDistributeLockService appstatusDistributeLockService)
  {
    this.appstatusDistributeLockService = appstatusDistributeLockService;
  }

  private class SimpleOperationProcessor implements OperationProcessor
  {
    
    private Operation operation;
    private Object result;
    private CommonException exception;

    public SimpleOperationProcessor(Operation operation)
    {
      this.operation = operation;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start()
    {
      final TaskInfo task = new TaskInfo();
      String executorName = "com.opzoon.vdi.core.controller.executor.OperationExecutor";
      try
      {
        task.setExecutorClass((Class<? extends ExecutorBase>) Class.forName(executorName));
      } catch (ClassNotFoundException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      List<Object> params = operation.getParams();
      Iterator<Object> iter = params.iterator();
      int size = params.size();
      task.setPara1(operation.getClass().getName());
      if (size > 0)
      {
        Object n = iter.next();
        task.setPara2(n == null ? null : n.toString());
        if (size > 1)
        {
          n = iter.next();
          task.setPara3(n == null ? null : n.toString());
          if (size > 2)
          {
            n = iter.next();
            task.setPara4(n == null ? null : n.toString());
            if (size > 3)
            {
              n = iter.next();
              task.setPara5(n == null ? null : n.toString());
            }
          }
        }
      }
      controller.sendTask(task);
    }

    @Override
    public String getId()
    {
      return null;
    }

    public Object getResult()
    {
      return result;
    }

    public void setResult(Object result)
    {
      this.result = result;
    }

    public CommonException getException()
    {
      return exception;
    }

    public void setException(CommonException exception)
    {
      this.exception = exception;
    }
    
  }

}
