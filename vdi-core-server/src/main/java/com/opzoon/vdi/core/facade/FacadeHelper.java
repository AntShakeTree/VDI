package com.opzoon.vdi.core.facade;

import static com.opzoon.vdi.core.util.StringUtils.strcat;
import static com.opzoon.vdi.core.util.ConditionUtils.numberEquals;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;

/**
 * 业务接口帮助类.
 */
public abstract class FacadeHelper {
  
  private static final Logger log = LoggerFactory.getLogger(FacadeHelper.class);
	
	private FacadeHelper () {}
	
	/**
	 * 判断从数据库查询出来的数值是否大于0.
	 * 
	 * @param count 从数据库count出来的数值.
	 * @return 是否大于0.
	 */
	public static boolean exists(Object count) {
		if (count instanceof Long) {
			return 0 < (Long) count;
		}
		return 0 < (Integer) count;
	}
	
	/**
	 * 在查询语句前拼接count并查询, 结果存储在amountContainer参数中.
	 * 
	 * @param storageFacade 存储接口.
	 * @param countColumn 计数列名.
	 * @param querySB 查询语句.
	 * @param paramsArray 查询参数.
	 * @param amountContainer 结果容器.
	 */
	public static void count(StorageFacade storageFacade, String countColumn, StringBuilder querySB, Object[] paramsArray, int[] amountContainer) {
		String query = querySB.toString();
		if (query.startsWith("select")) {
			query = query.replaceFirst("select ((distinct )?[\\w]+)", strcat("select count($1.", countColumn, ")"));
		} else {
			query = strcat("select count(", countColumn, ") ", query);
		}
		amountContainer[0] = ((Long) storageFacade.findFirst(query, paramsArray)).intValue();
	}
	
	/**
	 * 分页查询.
	 * 
	 * @param storageFacade 存储接口.
	 * @param whereClause 查询语句.
	 * @param paramsArray 查询参数.
	 * @param pagingInfo 分页信息.
	 * @return 查询结果.
	 */
	@SuppressWarnings("rawtypes")
	public static List pagingFind(StorageFacade storageFacade, Object whereClause, Object[] paramsArray, PagingInfo pagingInfo) {
		if (numberEquals(pagingInfo.getPagesize(), -1)) {
			return storageFacade.find(
			        strcat(whereClause, " order by ", pagingInfo.getSortkey(), pagingInfo.getAscend() < 1 ? " desc" : ""),
			        paramsArray);
		} else {
			return storageFacade.find(
			        (pagingInfo.getPage() - 1) * pagingInfo.getPagesize(),
			        pagingInfo.getPagesize(),
			        strcat(whereClause, " order by ", pagingInfo.getSortkey(), pagingInfo.getAscend() < 1 ? " desc" : ""),
			        paramsArray);
		}
	}

	public static String keyword(String prefix, PagingInfo pagingInfo, List<Object> params) {
		if (pagingInfo == null) {
			return "";
		}
		if (pagingInfo.getDataindex() != null && pagingInfo.getContent() != null) {
			params.add("%" + pagingInfo.getContent() + "%");
			return " and " + (prefix == null ? "" : (prefix + ".")) + pagingInfo.getDataindex() + " like ?";
		} else {
			return "";
		}
	}

	public static String keyword(PagingInfo pagingInfo, List<Object> params) {
		return keyword(null, pagingInfo, params);
	}
	
	/**
	 * 分页信息.
	 */
	public static abstract class PagingInfo {

		private String dataindex;
		private String content;
		private String sortkey;
		private int ascend;
		private int pagesize;
		private int page;
		
		public PagingInfo() {}
		
		/**
		 * 从另一个分页信息对象拷贝属性.
		 * 
		 * @param pagingInfo 另一个分页信息对象.
		 */
		public void copyFrom(PagingInfo pagingInfo) {
			this.setDataindex(pagingInfo.getDataindex());
			this.setContent(pagingInfo.getContent());
			this.setAscend(pagingInfo.getAscend());
			this.setPage(pagingInfo.getPage());
			this.setPagesize(pagingInfo.getPagesize());
			this.setSortkey(pagingInfo.getSortkey());
		}
		
		/**
		 * 在内存中按照分页信息裁剪列表.
		 * TODO Use Collections.subList().
		 * 
		 * @param list 列表.
		 * @return 裁剪后的列表.
		 */
		public <E> List<E> subList(List<E> list) {
			if (this.getPagesize() == -1) {
				return list;
			}
			List<E> sublist = new LinkedList<E>();
			final int start = (this.getPage() - 1) * this.getPagesize();
			int index = 0;
			outter:
			for (E element : list) {
				if (index >= start) {
					sublist.add(element);
					if (sublist.size() >= this.getPagesize()) {
						break outter;
					}
				}
				++index;
			}
			return sublist;
		}

		public String getDataindex() {
			return dataindex;
		}
		public void setDataindex(String dataindex) {
			this.dataindex = dataindex;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		/**
		 * @return 排序关键字.
		 */
		public String getSortkey() {
			return sortkey;
		}
		public void setSortkey(String sortkey) {
			this.sortkey = sortkey;
		}
		/**
		 * @return 升序为1, 降序为0.
		 */
		public int getAscend() {
			return ascend;
		}
		public void setAscend(int ascend) {
			this.ascend = ascend;
		}
		/**
		 * @return 每页数量. -1表示不分页.
		 */
		public int getPagesize() {
			return pagesize;
		}
		public void setPagesize(int pagesize) {
			this.pagesize = pagesize;
		}
		/**
		 * @return 页号, 以1开始.
		 */
		public int getPage() {
			return page;
		}
		public void setPage(int page) {
			this.page = page;
		}
		
	}

  public static void waitUntilDatabaseIsReady(DatabaseFacade databaseFacade)
  {
    for (;;)
    {
      try
      {
        databaseFacade.find("from CloudManagerEntity");
        return;
      } catch (Exception e)
      {
        log.warn("Database is not ready. Waiting for a while...", e);
        try
        {
          Thread.sleep(10000);
        } catch (InterruptedException e1)
        {}
      }
    }
  }

}
