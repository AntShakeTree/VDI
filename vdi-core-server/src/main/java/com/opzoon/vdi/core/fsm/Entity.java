package com.opzoon.vdi.core.fsm;

public class Entity
{

  private final Class<? extends Stateful> type;
  private final Integer id;
  
  public Entity(Class<? extends Stateful> type, Integer id)
  {
    this.type = type;
    this.id = id;
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
    final Entity that = (Entity) object;
    return this.type.equals(that.type)
        && this.id.equals(that.id);
  }

  @Override
  public int hashCode()
  {
    return (this.type.getName() + "#" + this.id.toString()).hashCode();
  }

  public Class<? extends Stateful> getType()
  {
    return type;
  }

  public Integer getId()
  {
    return id;
  }

}
