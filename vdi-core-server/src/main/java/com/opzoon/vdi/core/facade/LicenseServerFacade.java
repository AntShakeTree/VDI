package com.opzoon.vdi.core.facade;

import static com.opzoon.vdi.core.facade.CommonException.CONFLICT;
import static com.opzoon.vdi.core.facade.CommonException.NOT_FOUND;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.facade.FacadeHelper.exists;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;

import com.opzoon.vdi.core.domain.LicenseServer;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;

/**
 * 
 * @author zhanglu
 *
 */
public class LicenseServerFacade {

	private DatabaseFacade storageFacade;

	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param idlicense idlicense小于等于0，则获取全部授权，否则，获取id为idlicense的license
	 * @return
	 */
	public List<LicenseServer> listLicenseServer(final int idlicenseserver, PagingInfo pagingInfo)
	{
		StringBuilder queryClause = new StringBuilder("from LicenseServer where 1 = 1");
		List<Object> params = new ArrayList<Object>();
		if (idlicenseserver > 0) {
			queryClause.append(" and idlicense = ?");
			params.add(idlicenseserver);
		}
		queryClause.append(FacadeHelper.keyword(pagingInfo, params));
		queryClause.append(" order by idlicenseserver");
		Object[] paramsArray = params.toArray();
		List<LicenseServer> licenseServers = (List<LicenseServer>) storageFacade.find(
				queryClause.toString(),
		        paramsArray);
		return licenseServers;
	}

	/**
	 * 删除license server.
	 * 
	 * @param idlicenseserver 平台ID.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 授权不存在.
	 */
	public int deleteLicenseServer(final int idlicenseserver) {
		if (!exists(storageFacade.findFirst(
				"select count(idlicenseserver) from LicenseServer where idlicenseserver = ?",
				idlicenseserver))) {
			return NOT_FOUND;
		}
		storageFacade.update("delete from LicenseServer where idlicenseserver = ?", idlicenseserver);
		return NO_ERRORS;
	}
	
	/**
	 * 查询是否有内容相同的license
	 * @param content
	 * @return
	 */
	public int updateLicenseServer(LicenseServer licenseServer) throws CommonException 
	{
		if(!exists(storageFacade.findFirst(
				"select count(idlicenseserver) from LicenseServer where idlicenseserver = ?",
				licenseServer.getIdlicenseserver()))){
			return NOT_FOUND;
		}
		StringBuilder updateClause = new StringBuilder("update LicenseServer set idlicenseserver = idlicenseserver");
		List<Object> params = new ArrayList<Object>();
		if (licenseServer.getIp() != null) {
			updateClause.append(", ip = ?");
			params.add(licenseServer.getIp());
		}
		if (licenseServer.getPort() != null) {
			updateClause.append(", port = ?");
			params.add(licenseServer.getPort());
		}
		updateClause.append(" where idlicenseserver = ?");
		params.add(licenseServer.getIdlicenseserver());
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
	
	/**
	 * 配置license server.
	 * 
	 * @param licenseServer 授权实体.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NO_ERRORS}: 成功.
	 *         {@link com.opzoon.vdi.core.facade.CommonException#LICENSE_DECRYPT_ERROR}: License解密失败.
	 */
	public int createLicenseServer(LicenseServer licenseServer) throws CommonException {
		storageFacade.persist(licenseServer);
		return NO_ERRORS;
	}

	public void setStorageFacade(DatabaseFacade storageFacade) {
		this.storageFacade = storageFacade;
	}
	
}
