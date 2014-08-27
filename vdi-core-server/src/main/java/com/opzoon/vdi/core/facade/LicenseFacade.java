package com.opzoon.vdi.core.facade;

import static com.opzoon.vdi.core.facade.CommonException.CONFLICT;
import static com.opzoon.vdi.core.facade.CommonException.FORBIDDEN;
import static com.opzoon.vdi.core.facade.CommonException.NOT_FOUND;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.facade.FacadeHelper.exists;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;

import com.opzoon.vdi.core.domain.License;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;

/**
 * 
 * @author tanyunhua
 *
 */
public class LicenseFacade {

	private DatabaseFacade storageFacade;

	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param idlicense idlicense小于等于0，则获取全部授权，否则，获取id为idlicense的license
	 * @return
	 */
	public List<License> listLicense(final int idlicense, PagingInfo pagingInfo)
	{
		StringBuilder queryClause = new StringBuilder("from License where 1 = 1");
		List<Object> params = new ArrayList<Object>();
		if (idlicense > 0) {
			queryClause.append(" and idlicense = ?");
			params.add(idlicense);
		}
		queryClause.append(FacadeHelper.keyword(pagingInfo, params));
		queryClause.append(" order by idlicense");
		Object[] paramsArray = params.toArray();
		List<License> licenses = (List<License>) storageFacade.find(
				queryClause.toString(),
		        paramsArray);
		for(License license: licenses)
		{
			license.setConnectCount("");
		}
		return licenses;
	}

	/**
	 * 删除授权.
	 * 
	 * @param idlicense 平台ID.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 授权不存在.
	 */
	public int deleteLicense(final int idlicense) {
		if (!exists(storageFacade.findFirst(
				"select count(idlicense) from License where idlicense = ?",
				idlicense))) {
			return NOT_FOUND;
		}
		storageFacade.update("delete from License where idlicense = ?", idlicense);
		return NO_ERRORS;
	}
	
	/**
	 * 查询是否有内容相同的license
	 * @param content
	 * @return
	 */
	public boolean haveLicense(final String content)
	{
		return exists(storageFacade.findFirst(
				"select count(idlicense) from License where content = ?",
				content));
	}
	
	/**
	 * 导入授权.
	 * 
	 * @param license 授权实体.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功.
	 *         {@link com.opzoon.vdi.core.facade.CommonException#LICENSE_DECRYPT_ERROR}: License解密失败.
	 */
	public int createLicense(License license) throws CommonException {
		storageFacade.persist(license);
		return NO_ERRORS;
	}
	/**
	 * 更新license
	 * @param license
	 * @return
	 * @throws CommonException
	 */
	public int updateLicense(License license) throws CommonException {
		if (!exists(storageFacade.findFirst(
				"select count(idlicense) from License where idlicense = ?",
				license.getIdlicense()))) {
			return NOT_FOUND;
		}
		StringBuilder updateClause = new StringBuilder("update License set idlicense = idlicense");
		List<Object> params = new ArrayList<Object>();
		if (license.getExpire() >= 0 ) {
			updateClause.append(", expire = ?");
			params.add(license.getExpire());
		}
		updateClause.append(" where idlicense = ?");
		params.add(license.getIdlicense());
		Object[] paramsArray = params.toArray();
		try {
			if (!exists(storageFacade.update(updateClause.toString(), paramsArray))) {
				return NOT_FOUND;
			}
		} catch (EntityExistsException e) {
			throw new CommonException(CONFLICT);
		}
		return NO_ERRORS;
	}

	public void setStorageFacade(DatabaseFacade storageFacade) {
		this.storageFacade = storageFacade;
	}
	
}
