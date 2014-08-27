package com.opzoon.vdi.core.pool;

import java.util.Set;

import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;

/**
 * OperationPool APIs.<br />
 * OperationPool is a module of VDI Core which encapsules logics on operations, events and synchronization.
 */
public interface OperationPool
{

  /**
   * Executing a code snippet in {@link SynchronizedCallable#call(Object)} while the current thread is owning a mutex whose name is specified.<br />
   * If the mutex with the specified name is being owned by another thread then this method will block the current thread until the mutex is released.<br />
   * The mutex should be passed to {@link SynchronizedCallable#call(Object)}.
   * 
   * @param mutexName Name of the mutex. It is ensured that same mutex names represent an unique mutex.
   * @param synchronizedCallable SynchronizedCallable containing the code snippet needed to be executed.
   * @return What {@link SynchronizedCallable#call(Object)} returned.
   * @throws E What {@link SynchronizedCallable#call(Object)} threw.
   */
  <R, E extends Exception> R synchronizedExecute(String mutexName, SynchronizedCallable<R, E> synchronizedCallable) throws E;

  /**
   * Finding all running operations.<br />
   * This method is synchronized with mutex 'operations'.
   * 
   * @return All running operations.
   */
  Set<Operation> findRunningOperations();

  /**
   * Creating a OperationProcessor for the operation.<br />
   * This method is synchronized with mutex 'operations'.
   * 
   * @param operation The operation.
   * @param operationContext OperationContext.
   * @param notifyingOperationMutex If true, all threads owning mutex '"operation." + {@link OperationProcessor#getId()}' will be notified when the operation is finished.
   * @return OperationProcessor.
   */
  OperationProcessor createOperationProcessor(Operation operation, OperationContext operationContext, boolean notifyingOperationMutex);

  /**
   * Publishing an event to all the running operations.
   * 
   * @param event The event.
   */
  void publishEvent(Event event);

  /**
   * Pulling all events received by the operation.<br />
   * One event can not be pulled twice.
   * 
   * @param operation The operation.
   * @return All events received.
   */
  Set<Event> pullEvents(Operation operation);
  
  /**
   * A modified version of {@link java.util.concurrent.Callable} used in {@link OperationPool#synchronizedExecute(String, SynchronizedCallable)} returning an object and throwing an exception whose types are specified.<br />
   * The mutex owned by the current thread would be passed into method {@link #call(Object)}.
   *
   * @param <R> Returning type.
   * @param <E> Throwing type.
   */
  public static interface SynchronizedCallable<R, E extends Exception>
  {
    
    /**
     * Closure containing the code snippet which will be executed in the synchronized context.
     * 
     * @param mutex The mutex owned by the current thread.
     * @return Execution result.
     * @throws E Execution exception.
     */
    R call(Object mutex) throws E;
    
  }
  
  /**
   * Processor which is used to start an operation.
   */
  public static interface OperationProcessor
  {
    
    /**
     * Starting the operation asynchronously.
     */
    void start();

    /**
     * Returning a non-null and unique id of the processor.
     * 
     * @return A non-null and unique id of the processor.
     */
    String getId();

    /**
     * Returning the executing result of the operation.
     * 
     * @return Executing result of the operation.
     */
    Object getResult();

    /**
     * Returning the exception raised from the execution of the operation.
     * 
     * @return Exception raised from the execution of the operation.
     */
    CommonException getException();
    
  }

}
