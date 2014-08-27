package com.opzoon.vdi.core.operations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.StateMatcher;
import com.opzoon.vdi.core.pool.LocalOperationPool;

public class Tester
{
  
  public static void main(String[] args) throws CommonException
  {
    OperationRegistry r = new OperationRegistry();
    r.setOperationPool(new LocalOperationPool());
    Operation earlier = new Earlier();
    Operation later = new Later();
    r.start(earlier);
    r.start(later);
  }

  private static final class Earlier extends AbstractOperation
  {

    @Override
    public boolean delays(Operation operation)
    {
      return true;
    }
    
  }

  private static final class Later extends AbstractOperation
  {
    
  }
  
  private static abstract class AbstractOperation implements Operation
  {

    @Override
    public Object execute(OperationContext operationContext)
        throws CommonException
    {
      System.out.println(this.getClass());
      try
      {
        Thread.sleep(1000);
      } catch (InterruptedException e) {}
      return null;
    }

    @Override
    public boolean rejects(Operation operation)
    {
      return false;
    }

    @Override
    public boolean delays(Operation operation)
    {
      return false;
    }

    @Override
    public boolean onEvent(OperationContext operationContext, Event event)
        throws CommonException
    {
      System.out.println(event.getClass());
      return false;
    }

    @Override
    public Map<Entity, StateMatcher> getNeededStates()
    {
      return null;
    }

    @Override
    public List<Object> getParams()
    {
      return Arrays.asList(new Object[] {});
    }
    
  }

}
