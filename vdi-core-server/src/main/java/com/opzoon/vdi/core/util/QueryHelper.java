package com.opzoon.vdi.core.util;

/**
 * 数据库查询的帮助类.
 * 
 * @author Evan
 */
public abstract class QueryHelper
{

  private QueryHelper()
  {
  }

  /**
   * 根据查询条件项的数量构建出由'or'衔接的查询条件语句块.<br />
   * 例如:<br />
   * 输入: range = 3, refKey = "userId"<br />
   * 输出: "(userId = ? or userId = ? or userId = ?)".
   * 
   * @param range 查询条件项的数量.
   * @param refKey 列引用名称.
   * @return 由'or'衔接的查询条件语句块.
   */
  public static String buildOrIdWhereClause(final int range, final String refKey)
  {
    final StringBuilder whereClause = new StringBuilder();
    for (int i = 0; i < range; i++)
    {
      if(i < 1)
      {
        whereClause.append(" (");
      }
      else
      {
        whereClause.append(" or ");
      }
      whereClause.append(refKey);
      whereClause.append(" = ?");
      if(i == range - 1)
      {
        whereClause.append(")");
      }
    }
    return whereClause.toString();
  }

}
