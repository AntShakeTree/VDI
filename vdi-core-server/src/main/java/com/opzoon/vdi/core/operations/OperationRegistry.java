package com.opzoon.vdi.core.operations;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.opzoon.vdi.core.cloud.ConnectionManager;
import com.opzoon.vdi.core.event.OperationDelayedEvent;
import com.opzoon.vdi.core.event.OperationStartedEvent;
import com.opzoon.vdi.core.event.StateUpdatedEvent;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.Refresher;
import com.opzoon.vdi.core.fsm.Request;
import com.opzoon.vdi.core.fsm.State;
import com.opzoon.vdi.core.fsm.StateMachine;
import com.opzoon.vdi.core.fsm.StateMatcher;
import com.opzoon.vdi.core.pool.OperationPool;
import com.opzoon.vdi.core.pool.OperationPool.OperationProcessor;
import com.opzoon.vdi.core.pool.OperationPool.SynchronizedCallable;
import com.opzoon.vdi.core.support.Configuration;

public class OperationRegistry
{

  private static Logger log = Logger.getLogger(OperationRegistry.class);

  private OperationPool operationPool;

  private StateMachine stateMachine;

  private Configuration configuration;
  private ConnectionManager connectionManager;

  public void start(final Operation operation) throws CommonException
  {
    start(operation, false);
  }

  public Object startAndAwait(final Operation operation) throws CommonException
  {
    return start(operation, true);
  }

//  public void end(final Operation operation)
//  {
//    synchronized (this)
//    {
//      runnings.remove(operation);
//      followers.remove(operation);
//    }
//  }

  public boolean refreshFrequently(final Operation operation, final OperationContext operationContext, final Refresher refresher)
  {
    while (!refresher.refresh(operationContext))
    {
      log.trace(String.format("%s is refreshed for %s.", refresher, operation));
      try
      {
        Thread.sleep(refresher.getIntevalInSeconds() * 1000);
      } catch (InterruptedException e)
      {}
      final Set<Event> events = operationPool.pullEvents(operation);
      for (final Event event : events)
      {
        boolean ended = false;
        try
        {
          ended = operation.onEvent(operationContext, event);
        } catch (CommonException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        if (ended)
        {
          return true;
        }
      }
    }
    return false;
  }
  
  public StateMachine getStateMachine()
  {
    return stateMachine;
  }

  public void publishEvent(Event event)
  {
    operationPool.publishEvent(event);
  }

  public void publishStateRequest(Request request) throws CommonException
  {
    final Set<State> effectedStates = stateMachine.publishRequest(request);
    this.publishEvent(new StateUpdatedEvent(effectedStates));
  }

  public void setOperationPool(OperationPool operationPool)
  {
    this.operationPool = operationPool;
  }

  public void setStateMachine(StateMachine stateMachine)
  {
    this.stateMachine = stateMachine;
  }

  public Configuration getConfiguration()
  {
    return configuration;
  }

  public void setConfiguration(Configuration configuration)
  {
    this.configuration = configuration;
  }

  public ConnectionManager getConnectionManager()
  {
    return connectionManager;
  }

  public void setConnectionManager(ConnectionManager connectionManager)
  {
    this.connectionManager = connectionManager;
  }

  private Object start(final Operation operation, final boolean toAwait) throws CommonException
  {
    log.trace(String.format("%s pushed to start", operation));
    final boolean[] delayed = new boolean[1];
    final OperationProcessor operationProcessor = operationPool.synchronizedExecute(
        "operations",
        new SynchronizedCallable<OperationProcessor, RuntimeException>()
        {
          @Override
          public OperationProcessor call(Object mutex)
          {
            log.trace(String.format("%s started", operation));
            Set<Operation> runningOperations = operationPool.findRunningOperations();
            for (final Operation runningOperation : runningOperations)
            {
              if (runningOperation.rejects(operation))
              {
                log.warn(runningOperation + " rejected " + operation);
                return null;
              }
              if (runningOperation.delays(operation))
              {
                delayed[0] = true;
                log.warn(runningOperation + " delayed " + operation);
                return null;
              }
            }
            final Map<Entity, StateMatcher> neededStates = operation.getNeededStates();
            if (neededStates != null)
            {
              for (final Entity entity : neededStates.keySet())
              {
                State state = OperationRegistry.this.stateMachine.loadState(entity);
                if (!neededStates.get(entity).matches(state))
                {
                  log.warn(neededStates.get(entity) + " unmatched " + state);
                  return null;
                }
              }
            }
            final OperationContext operationContext = new OperationContext();
            operationContext.setOperationRegistry(OperationRegistry.this);
            return operationPool.createOperationProcessor(operation, operationContext, toAwait);
          }
        });
    if (operationProcessor == null)
    {
      if (delayed[0])
      {
        OperationRegistry.this.publishEvent(new OperationDelayedEvent(operation));
        if (toAwait)
        {
          try
          {
            Thread.sleep(20000);
          } catch (InterruptedException e1)
          {}
          return OperationRegistry.this.startAndAwait(operation);
        } else
        {
          new Thread()
          {
            @Override
            public void run()
            {
              try
              {
                Thread.sleep(20000);
              } catch (InterruptedException e1)
              {}
              try
              {
                OperationRegistry.this.start(operation);
              } catch (CommonException e)
              {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
          }.start();
          return null;
        }
      } else
      {
        throw new CommonException(CommonException.CONFLICT);
      }
    }
    OperationRegistry.this.publishEvent(new OperationStartedEvent(operation));
    if (toAwait)
    {
      return operationPool.synchronizedExecute(
          "operation." + operationProcessor.getId(),
          new SynchronizedCallable<Object, CommonException>()
          {
            @Override
            public Object call(Object mutex) throws CommonException
            {
              operationProcessor.start();
              try
              {
                mutex.wait();
              } catch (InterruptedException e)
              {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
              if (operationProcessor.getException() != null)
              {
                throw operationProcessor.getException();
              } else
              {
                return operationProcessor.getResult();
              }
            }
          });
    } else
    {
      operationProcessor.start();
      return null;
    }
  }

}
