package com.opzoon.vdi.core.fsm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.facade.DatabaseFacade;
import com.opzoon.vdi.core.pool.OperationPool;
import com.opzoon.vdi.core.pool.OperationPool.SynchronizedCallable;

public class StateMachine
{

  private static Logger log = Logger.getLogger(StateMachine.class);

  private OperationPool operationPool;
  
  private DatabaseFacade databaseFacade;

  public Set<State> loadStates(final Set<Entity> entities)
  {
    return operationPool.synchronizedExecute(
        "states",
        new SynchronizedCallable<Set<State>, RuntimeException>()
        {
          @Override
          public Set<State> call(Object mutex)
          {
            final Set<State> states = new HashSet<State>();
            for (final Entity entity : entities)
            {
              final Stateful stateful = (Stateful) databaseFacade.load(entity.getType(), entity.getId());
              states.add(stateful.loadState());
            }
            return states;
          }
        });
  }

  public State loadState(final Entity entity)
  {
    return operationPool.synchronizedExecute(
        "states",
        new SynchronizedCallable<State, RuntimeException>()
        {
          @Override
          public State call(Object mutex)
          {
            return ((Stateful) databaseFacade.load(entity.getType(), entity.getId())).loadState();
          }
        });
  }

  public <E> E load(Class<E> entityType, Integer id)
  {
    synchronized (this)
    {
      return databaseFacade.load(entityType, id);
    }
  }

  public void persist(final Object entity) throws CommonException
  {
    operationPool.synchronizedExecute(
        "states",
        new SynchronizedCallable<Void, CommonException>()
        {
          @Override
          public Void call(Object mutex) throws CommonException
          {
            try {
              databaseFacade.persist(entity);
            } catch (Exception e) {
              throw new CommonException(CommonException.CONFLICT);
            }
            return null;
          }
        });
  }

//  public List<?> find(final String queryS, final Object... params) throws CommonException
//  {
//    return operationPool.synchronizedExecute(
//        "states",
//        new SynchronizedCallable<List<?>, CommonException>()
//        {
//          @Override
//          public List<?> call(Object mutex) throws CommonException
//          {
//            return databaseFacade.find(queryS, params);
//          }
//        });
//  }

  public Set<State> publishRequest(final Request request) throws CommonException
  {
    return operationPool.synchronizedExecute(
        "states",
        new SynchronizedCallable<Set<State>, CommonException>()
        {
          @Override
          public Set<State> call(Object mutex) throws CommonException
          {
            log.trace(String.format("Trying to publish %s", request));
            final Set<Entity> effectedEntities = request.getTargetEntities();
            final Map<Entity, Stateful> statefuls = new HashMap<Entity, Stateful>();
            final Map<Entity, State> states = new HashMap<Entity, State>();
            for (final Entity entity : effectedEntities)
            {
              final Stateful stateful = (Stateful) databaseFacade.load(entity.getType(), entity.getId());
              final State state = stateful.loadState();
              if (!state.accepts(databaseFacade, request))
              {
                log.warn(String.format("%s did not accept %s", state, request));
                throw new CommonException(CommonException.CONFLICT);
              }
              statefuls.put(entity, stateful);
              states.put(entity, state);
            }
            final Set<State> effectedStates = new HashSet<State>();
            for (final Entity entity : states.keySet())
            {
              final State state = states.get(entity);
              final State effectedState;
              try
              {
                effectedState = state.accept(databaseFacade, request);
              } catch (CommonException e)
              {
                // TODO Auto-generated catch block
                log.warn(String.format("%s failed to accept %s", state, request), e);
                throw e;
              }
              if (effectedState != null)
              {
                final Stateful stateful = statefuls.get(entity);
                stateful.mergeState(effectedState);
                databaseFacade.merge(stateful);
                effectedStates.add(effectedState);
              }
            }
            return effectedStates;
          }
        });
  }

  public DatabaseFacade getDatabaseFacade()
  {
    return databaseFacade;
  }

  public void setOperationPool(OperationPool operationPool)
  {
    this.operationPool = operationPool;
  }

  public void setDatabaseFacade(DatabaseFacade databaseFacade)
  {
    this.databaseFacade = databaseFacade;
  }

}
