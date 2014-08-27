package com.opzoon.vdi.core.operation;

import static com.opzoon.vdi.core.domain.DesktopPoolEntity.DESKTOP_POOL_SOURCE_AUTO;
import static com.opzoon.vdi.core.util.ConditionUtils.numberEquals;
import static com.opzoon.vdi.core.util.StringUtils.strcat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.opzoon.vdi.core.domain.Desktop;
import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.DesktopPoolStatus;
import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.domain.state.DesktopState;
import com.opzoon.vdi.core.event.OperationDelayedEvent;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.StateMatcher;
import com.opzoon.vdi.core.matcher.NormalDesktopPoolMatcher;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;

public class AdjustDesktopCountOperation implements Operation
{

  private final int desktoppoolid;
  private final int cloudmanagerid;

  public AdjustDesktopCountOperation(int desktoppoolid, int cloudmanagerid)
  {
    this.desktoppoolid = desktoppoolid;
    this.cloudmanagerid = cloudmanagerid;
  }

  @Override
  public Object execute(OperationContext operationContext) throws CommonException
  {
    final DesktopPoolEntity desktopPool = operationContext.getOperationRegistry().getStateMachine().load(DesktopPoolEntity.class, desktoppoolid);
    @SuppressWarnings("unchecked")
    final List<Desktop> desktops = (List<Desktop>) operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().find(
        "from Desktop where desktoppoolid = ?",
        desktoppoolid);
    int currentDesktopCount = 0;
    Desktop firstDeletableDesktop = null;
    for (final Desktop desktop : desktops)
    {
      final DesktopState desktopState = (DesktopState) operationContext.getOperationRegistry().getStateMachine().loadState(new Entity(DesktopStatus.class, desktop.getIddesktop()));
      if (desktopState.getPhase() == DesktopState.DESKTOP_PHASE_START
          || desktopState.getPhase() == DesktopState.DESKTOP_PHASE_CREATING
          || desktopState.getPhase() == DesktopState.DESKTOP_PHASE_NORMAL
          || desktopState.getPhase() == DesktopState.DESKTOP_PHASE_DEFICIENT)
      {
        currentDesktopCount++;
        if (firstDeletableDesktop == null
            && desktopState.getPhase() != DesktopState.DESKTOP_PHASE_START
            && desktopState.getOwnerid() == -1)
        {
          firstDeletableDesktop = desktop;
        }
      }
    }
    if (currentDesktopCount < desktopPool.getMaxdesktops())
    {
      final List<Integer> newDesktopNumbers = this.calculateNewDesktopNumbers(desktops, desktopPool.getMaxdesktops() - currentDesktopCount);
      for (final Integer newDesktopNumber : newDesktopNumbers)
      {
        final String vmName = strcat(desktopPool.getVmnamepatterrn(), "-", newDesktopNumber);
        final boolean isAutoPool = numberEquals(desktopPool.getVmsource(), DESKTOP_POOL_SOURCE_AUTO);
        Desktop desktop = new Desktop();
        desktop.setDesktoppoolid(desktopPool.getIddesktoppool());
        desktop.setOwnerid(-1);
        desktop.setVmname(vmName);
        desktop.setIpaddress(isAutoPool ? (desktopPool.getIddesktoppool() + " " + vmName) : null);
        desktop.setDesktoppoolname(desktopPool.getPoolname());
        desktop.setVmsource(desktopPool.getVmsource());
        desktop.setAssignment(desktopPool.getAssignment());
        operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().persist(desktop);
        DesktopStatus desktopStatus = new DesktopStatus();
        desktopStatus.setIddesktop(desktop.getIddesktop());
        desktopStatus.setPhase(isAutoPool ? DesktopState.DESKTOP_PHASE_START : DesktopState.DESKTOP_PHASE_NORMAL);
        desktopStatus.setStatus(DesktopState.DESKTOP_STATUS_UNKNOWN);
        desktopStatus.setOwnerid(-1);
        operationContext.getOperationRegistry().getStateMachine().getDatabaseFacade().persist(desktopStatus);
        if (isAutoPool)
        {
          operationContext.getOperationRegistry().start(new CloneInstanceOperation(desktoppoolid, desktopPool.getCloudmanagerid(), desktop.getIddesktop(), null));
        }
      }
    } else if (currentDesktopCount > desktopPool.getMaxdesktops())
    {
      if (firstDeletableDesktop != null)
      {
        try
        {
          operationContext.getOperationRegistry().start(new DeleteDesktopOperation(Integer.toHexString(desktoppoolid) + "#" + Integer.toHexString(desktopPool.getCloudmanagerid()) + "#" + Integer.toHexString(firstDeletableDesktop.getIddesktop()), firstDeletableDesktop.getVmid(), false));
        } catch (CommonException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    } else
    {
      return null;
    }
    operationContext.getOperationRegistry().start(new AdjustDesktopCountOperation(desktoppoolid, cloudmanagerid));
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
    if (operation instanceof DeleteCloudManagerOperation
        && ((DeleteCloudManagerOperation) operation).getCloudmanagerid() == cloudmanagerid)
    {
      return true;
    } else if (operation instanceof DeleteDesktopPoolOperation
        && ((DeleteDesktopPoolOperation) operation).getDesktoppoolid() == desktoppoolid)
    {
      return true;
    } else if (operation instanceof AdjustDesktopCountOperation
        && ((AdjustDesktopCountOperation) operation).getDesktoppoolid() == desktoppoolid)
    {
      return true;
    }
    return false;
  }

  @Override
  public boolean onEvent(OperationContext operationContext, Event event)
  {
    if (event instanceof OperationDelayedEvent)
    {
      final OperationDelayedEvent operationDelayedEvent = (OperationDelayedEvent) event;
      final Operation delayedOperation = operationDelayedEvent.getOperation();
      if (delayedOperation instanceof DeleteCloudManagerOperation)
      {
        // TODO
        return true;
      } else if (delayedOperation instanceof DeleteDesktopPoolOperation)
      {
        // TODO
        return true;
      } else if (delayedOperation instanceof AdjustDesktopCountOperation)
      {
        // TODO
        return true;
      }
    }
    return false;
  }

  @Override
  public Map<Entity, StateMatcher> getNeededStates()
  {
    final Map<Entity, StateMatcher> neededStates = new HashMap<Entity, StateMatcher>();
    neededStates.put(new Entity(DesktopPoolStatus.class, desktoppoolid), new NormalDesktopPoolMatcher());
    return neededStates;
  }

  @Override
  public List<Object> getParams()
  {
    return Arrays.asList(new Object[] { desktoppoolid, cloudmanagerid });
  }

  @Override
  public boolean equals(Object object)
  {
    if (object == null)
    {
      return false;
    }
    if (!object.getClass().equals(this.getClass()))
    {
      return false;
    }
    final AdjustDesktopCountOperation that = (AdjustDesktopCountOperation) object;
    return this.desktoppoolid == that.desktoppoolid;
  }

  @Override
  public int hashCode()
  {
    return this.desktoppoolid;
  }

  public int getDesktoppoolid()
  {
    return desktoppoolid;
  }

  public int getCloudmanagerid()
  {
    return cloudmanagerid;
  }

//  private int calculateNewDesktopNumber(final List<Desktop> desktops)
//  {
//    final List<Integer> numbers = new LinkedList<Integer>();
//    for (final Desktop desktop : desktops)
//    {
//      numbers.add(Integer.parseInt(desktop.getVmname().split("\\-")[1]));
//    }
//    Collections.sort(numbers);
//    int lastNumber = 0;
//    for (final int number : numbers)
//    {
//      if ((number - (lastNumber++)) > 1)
//      {
//        break;
//      }
//    }
//    return lastNumber;
//  }

  private List<Integer> calculateNewDesktopNumbers(List<Desktop> desktops, int newDesktopCount)
  {
    final List<Integer> numbers = new LinkedList<Integer>();
    final List<Integer> newNumbers = new LinkedList<Integer>();
    for (final Desktop desktop : desktops)
    {
      numbers.add(Integer.parseInt(desktop.getVmname().split("\\-")[1]));
    }
    Collections.sort(numbers);
    int lastNumber = 0;
    for (final int number : numbers)
    {
      if ((number - (lastNumber++)) > 1)
      {
        newNumbers.add(lastNumber);
        if (newNumbers.size() == newDesktopCount)
        {
          return newNumbers;
        }
      }
    }
    for (int number = lastNumber + 1; newNumbers.size() < newDesktopCount; number++)
    {
      newNumbers.add(number);
    }
    return newNumbers;
  }

}
