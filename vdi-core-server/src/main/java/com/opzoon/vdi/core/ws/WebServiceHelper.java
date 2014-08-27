package com.opzoon.vdi.core.ws;

import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;

import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;

/**
 * WebServices的帮助类.
 */
public abstract class WebServiceHelper {
	
	private WebServiceHelper () {}
	
	public static interface Validater<T> {
		
		int validationAndFix(T t);
		
	}

	/**
	 * 修正分页信息参数.<br />
	 * 修正后的分页信息满足:<br />
	 * 1 页码大于0;<br />
	 * 2 每页数量不为0, 默认为15, -1为不分页;<br />
	 * 3 排序列名不为null, 默认为参数中的默认排序列名.
	 * 
	 * @param pagingInfo 分页信息.
	 * @param defaultSortkey 默认排序列名.
	 */
	public static void fixListParam(PagingInfo pagingInfo, final String defaultSortkey) {
		new Validater<PagingInfo>() {
			@Override
			public int validationAndFix(PagingInfo pagingInfo) {
				if (pagingInfo.getPage() < 1) {
					pagingInfo.setPage(1);
				}
				if (pagingInfo.getPagesize() < 0) {
					pagingInfo.setPagesize(-1);
				} else if (pagingInfo.getPagesize() == 0) {
					pagingInfo.setPagesize(15);
				}
				if (pagingInfo.getSortkey() == null || pagingInfo.getSortkey().trim().length() < 1) {
					pagingInfo.setSortkey(defaultSortkey);
				}
				return NO_ERRORS;
			}
		}.validationAndFix(pagingInfo);
	}

}
