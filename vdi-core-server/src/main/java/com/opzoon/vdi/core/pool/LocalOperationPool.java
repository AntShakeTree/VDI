package com.opzoon.vdi.core.pool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;

public class LocalOperationPool implements OperationPool
{
  
//  private final BlockingQueue<Operation> awaitings = new LinkedBlockingQueue<Operation>();
  private final Queue<Operation> runnings = new LinkedList<Operation>();

  private final Map<Operation, OperationFollower> followers = new HashMap<Operation, OperationFollower>();
  
  private final ConcurrentMap<String, Mutex> mutexes = new ConcurrentHashMap<String, Mutex>();
  
  private final AtomicInteger operationProcessorId = new AtomicInteger();

  @SuppressWarnings("unchecked")
  @Override
  public <R, E extends Exception> R synchronizedExecute(String name, SynchronizedCallable<R, E> synchronizedCallable) throws E
  {
    Mutex mutex = new Mutex();
    mutexes.putIfAbsent(name, mutex);
    mutex = mutexes.get(name);
    synchronized (mutex)
    {
      try
      {
        return synchronizedCallable.call(mutex);
      } catch (Exception e)
      {
        throw (E) e;
      }
    }
  }

  @Override
  public Set<Operation> findRunningOperations()
  {
    Set<Operation> runningOperations = new HashSet<Operation>();
//    awaitingsAndRunnings.addAll(awaitings);
    runningOperations.addAll(runnings);
    return runningOperations;
  }

//  @Override
//  public void start(final Operation operation, final OperationContext operationContext)
//  {
//    this.createThread(operation, operationContext, false).start();
//  }

  @Override
  public OperationProcessor createOperationProcessor(final Operation operation, final OperationContext operationContext, final boolean notifyingOperationMutex)
  {
    runnings.offer(operation);
    followers.put(operation, new OperationFollower());
    final LocalOperationProcessor localOperationProcessor = new LocalOperationProcessor(Integer.toHexString(operationProcessorId.addAndGet(1)));
    final Thread thread = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          localOperationProcessor.setResult(operation.execute(operationContext));
        } catch (CommonException e)
        {
          localOperationProcessor.setException(e);
        }
        LocalOperationPool.this.<Void, RuntimeException>synchronizedExecute(
            "operations",
            new SynchronizedCallable<Void, RuntimeException>()
            {
              @Override
              public Void call(Object mutex)
              {
                followers.remove(operation);
                runnings.remove(operation);
                return null;
              }
            });
        if (notifyingOperationMutex)
        {
          LocalOperationPool.this.<Void, RuntimeException>synchronizedExecute(
              "operation." + localOperationProcessor.getId(),
              new SynchronizedCallable<Void, RuntimeException>()
              {
                @Override
                public Void call(Object mutex)
                {
                  mutex.notifyAll();
                  return null;
                }
              });
        }
      }
    };
    localOperationProcessor.setThread(thread);
    return localOperationProcessor;
  }

  @Override
  public void publishEvent(Event event)
  {
    for (final OperationFollower follower : followers.values())
    {
      follower.addEvent(event);
    }
  }

  @Override
  public Set<Event> pullEvents(Operation operation)
  {
    return this.followers.get(operation).pullEvents();
  }
  
  private static class Mutex
  {
    
  }
  
  private static class LocalOperationProcessor implements OperationProcessor
  {

    private final String id;
    
    private Thread thread;
    
    private Object result;
    
    private CommonException exception;

    public LocalOperationProcessor(String id)
    {
      this.id = id;
    }

    @Override
    public void start()
    {
      thread.start();
    }

    @Override
    public String getId()
    {
      return id;
    }

    public void setThread(Thread thread)
    {
      this.thread = thread;
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

  private static class OperationFollower
  {

    private final Queue<Event> events = new LinkedList<Event>();

    public Set<Event> pullEvents()
    {
      synchronized (events)
      {
        final HashSet<Event> eventsToReturn = new HashSet<Event>(events);
        events.clear();
        return eventsToReturn;
      }
    }

    public void addEvent(Event event)
    {
      synchronized (events)
      {
        events.offer(event);
      }
    }

  }

}
