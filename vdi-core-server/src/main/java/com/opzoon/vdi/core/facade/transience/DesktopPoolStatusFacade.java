package com.opzoon.vdi.core.facade.transience;

import static com.opzoon.vdi.core.domain.DesktopPoolStatus.DESKTOPPOOL_STATUS_FULL;
import static com.opzoon.vdi.core.domain.DesktopPoolStatus.DESKTOPPOOL_STATUS_MAINTAIN;
import static com.opzoon.vdi.core.domain.DesktopPoolStatus.DESKTOPPOOL_STATUS_VALID;
import static com.opzoon.vdi.core.domain.DesktopStatus.DESKTOP_STATUS_CONNECTED;
import static com.opzoon.vdi.core.domain.DesktopStatus.DESKTOP_STATUS_DESTROYING;
import static com.opzoon.vdi.core.domain.DesktopStatus.DESKTOP_STATUS_ERROR;
import static com.opzoon.vdi.core.domain.DesktopStatus.DESKTOP_STATUS_PROVISIONING;
import static com.opzoon.vdi.core.domain.DesktopStatus.DESKTOP_STATUS_RUNNING;
import static com.opzoon.vdi.core.domain.DesktopStatus.DESKTOP_STATUS_SERVING;
import static com.opzoon.vdi.core.domain.DesktopStatus.DESKTOP_STATUS_STARTING;
import static com.opzoon.vdi.core.domain.DesktopStatus.DESKTOP_STATUS_STOPPED;
import static com.opzoon.vdi.core.domain.DesktopStatus.DESKTOP_STATUS_STOPPING;
import static com.opzoon.vdi.core.facade.CommonException.CONFLICT;
import static com.opzoon.vdi.core.facade.FacadeHelper.exists;

import javax.persistence.EntityExistsException;

import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.DesktopPoolStatus;
import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.domain.state.DesktopState;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.facade.StorageFacade;

/**
 * 桌面池状态相关业务接口.
 */
public class DesktopPoolStatusFacade {

	private StorageFacade storageFacade;

	/**
	 * 查找用户在桌面池中正在连接的桌面.
	 * 
	 * @param userid 用户ID.
	 * @param iddesktoppool 桌面池ID.
	 * @return 桌面ID.
	 */
	public Integer findConnectedDesktop(int userid, int iddesktoppool) {
		return  (Integer) storageFacade.findFirst(
				"select ds.iddesktop from DesktopStatus ds left join ds.desktop d where d.desktoppoolid = ? and ds.ownerid = ?",
				iddesktoppool, userid);
	}

	public boolean isNormal(int iddesktoppool) {
		return exists(storageFacade.findFirst(
				"select count(iddesktoppool) from DesktopPoolStatus where iddesktoppool = ? and status = ?",
				iddesktoppool, DESKTOPPOOL_STATUS_VALID));
	}
	
	/**
	 * 如果桌面池已满, 设置其状态为DESKTOP_POOL_STATUS_FULL.
	 * 
	 * @param pool 桌面池.
	 */
	public void markDesktopPoolAsFullIfNeeded(DesktopPoolEntity pool) {
		if (pool.getMaxdesktops() <= (Long) storageFacade.findFirst(
				"select count(ds.iddesktop) from DesktopStatus ds left join ds.desktop d where d.desktoppoolid = ? and ds.ownerid != ?",
				pool.getIddesktoppool(), -1)) {
			this.markDesktopPoolAsFull(pool.getIddesktoppool());
		}
	}
	
	/**
	 * 如果桌面池不满, 设置其状态为DESKTOP_POOL_STATUS_NORMAL.
	 * 
	 * @param pool 桌面池.
	 */
	public void markDesktopPoolAsNotFullIfNeeded(DesktopPoolEntity pool) {
		if (pool == null) {
			return;
		}
		if (exists(storageFacade.findFirst(
				"select count(iddesktoppool) from DesktopPoolStatus where iddesktoppool = ? and status = ?",
				pool.getIddesktoppool(), DESKTOPPOOL_STATUS_FULL))) {
			if (pool.getMaxdesktops() > (Long) storageFacade.findFirst(
					"select count(ds.iddesktop) from DesktopStatus ds left join ds.desktop d where d.desktoppoolid = ? and ds.ownerid != ?",
					pool.getIddesktoppool(), -1)) {
				this.markDesktopPoolAsNormal(pool.getIddesktoppool());
			}
		}
	}

	/**
	 * 设置桌面状态为异常.
	 * 
	 * @param desktopid 桌面ID.
	 */
	public void markAsError(int desktopid, int iddesktoppool) {
		if(!exists(storageFacade.update(
				"update DesktopStatus set status = ? where iddesktop = ? and status != ?",
				DESKTOP_STATUS_ERROR, desktopid, DESKTOP_STATUS_ERROR))) {
			return;
		}
		this.refreshDesktopPoolSparingAndAbnormalCount(iddesktoppool);
	}

	/**
	 * 设置桌面状态为关闭.
	 * 
	 * @param desktopid 桌面ID.
	 */
	public void markAsStopped(int desktopid, int iddesktoppool) {
		storageFacade.update(
				"update DesktopStatus set status = ? where iddesktop = ? and status != ?",
				DESKTOP_STATUS_STOPPED, desktopid, DESKTOP_STATUS_DESTROYING);
		this.refreshDesktopPoolSparingAndAbnormalCount(iddesktoppool);
	}

	/**
	 * 设置桌面状态为开启.
	 * 
	 * @param desktopid 桌面ID.
	 */
	public void markAsRunning(int desktopid) {
		// TODO Check CONNECTED on stop/abnormal ?
		storageFacade.update(
				"update DesktopStatus set status = ? where iddesktop = ? and status != ? and status != ?",
				DESKTOP_STATUS_RUNNING, desktopid, DESKTOP_STATUS_CONNECTED, DESKTOP_STATUS_DESTROYING);
	}

	public void markAsServing(int desktopid, int iddesktoppool) {
		storageFacade.update(
				"update DesktopStatus set status = ? where iddesktop = ? and status != ? and status != ?",
				DESKTOP_STATUS_SERVING, desktopid, DESKTOP_STATUS_CONNECTED, DESKTOP_STATUS_DESTROYING);
		this.refreshDesktopPoolSparingAndAbnormalCount(iddesktoppool);
	}

	public int markAsDestroying(int desktopid, int iddesktoppool) {
		return storageFacade.update("update DesktopStatus set status = ? where iddesktop = ? and status != ?", DESKTOP_STATUS_DESTROYING, desktopid, DESKTOP_STATUS_DESTROYING);
	}

	public void markAsProvisioning(int desktopid, int iddesktoppool) {
		storageFacade.update("update DesktopStatus set status = ? where iddesktop = ?", DESKTOP_STATUS_PROVISIONING, desktopid);
	}

	public void markAsStarting(int desktopid, int iddesktoppool) {
		storageFacade.update(
				"update DesktopStatus set status = ? where iddesktop = ? and status != ?",
				DESKTOP_STATUS_STARTING, desktopid, DESKTOP_STATUS_DESTROYING);
	}

	public void markAsStopping(int desktopid, int iddesktoppool) {
		storageFacade.update(
				"update DesktopStatus set status = ? where iddesktop = ? and status != ?",
				DESKTOP_STATUS_STOPPING, desktopid, DESKTOP_STATUS_DESTROYING);
	}

	public void createNewDesktopPoolStatus(Integer iddesktoppool) throws CommonException {
		DesktopPoolStatus desktopPoolStatus = new DesktopPoolStatus();
		desktopPoolStatus.setIddesktoppool(iddesktoppool);
		desktopPoolStatus.setStatus(DESKTOPPOOL_STATUS_VALID);
		desktopPoolStatus.setSparingdesktops(0);
		desktopPoolStatus.setAbnormaldesktops(0);
		try {
			storageFacade.persist(desktopPoolStatus);
		} catch (EntityExistsException e) {
			throw new CommonException(CONFLICT);
		}
	}

	public void removeDesktopPoolStatus(int iddesktoppool) {
		storageFacade.update(
				"delete from DesktopPoolStatus where iddesktoppool = ?",
				iddesktoppool);
	}

	public DesktopPoolStatus findDesktopPoolStatus(Integer iddesktoppool) {
		return storageFacade.load(DesktopPoolStatus.class, iddesktoppool);
	}

	/**
	 * 查找当前热备数.
	 * 
	 * @param desktoppoolid 桌面池ID.
	 * @return 当前热备数.
	 */
	public int countSparing(int desktoppoolid) {
		return (Integer) storageFacade.findFirst(
				"select sparingdesktops from DesktopPoolStatus where iddesktoppool = ?",
				desktoppoolid);
	}

	public void markDesktopPoolAsMaintaining(int iddesktoppool) {
		storageFacade.update(
				"update DesktopPoolStatus set status = ? where iddesktoppool = ?",
				DESKTOPPOOL_STATUS_MAINTAIN, iddesktoppool);
	}

	public void markDesktopPoolAsNormal(int iddesktoppool) {
		storageFacade.update(
				"update DesktopPoolStatus set status = ? where iddesktoppool = ? and status != ?",
				DESKTOPPOOL_STATUS_VALID, iddesktoppool, DesktopPoolStatus.DESKTOPPOOL_STATUS_MAINTAIN);
	}

	/**
	 * 查找某用户可用的桌面.
	 * 
	 * @param userid 用户ID.
	 * @param iddesktoppool 桌面池ID.
	 * @return 可用的桌面的ID.
	 */
	public DesktopStatus findAvailableDesktop(int userid, int iddesktoppool) {
		// One user one desktop of each pool.
		DesktopStatus alreadyAssignedDesktopStatus = this.findAssignedDesktopStatus(iddesktoppool, userid);
		if (alreadyAssignedDesktopStatus == null) {
			return this.findFirstAvailableDesktop(iddesktoppool);
		} else {
			return alreadyAssignedDesktopStatus;
		}
	}

	/**
	 * 增减桌面池的热备中桌面和问题桌面的数量.
	 * 
	 * @param iddesktoppool 桌面池ID.
	 * @param sparingCount 热备中桌面增量.
	 * @param abnormalCount 问题桌面增量.
	 */
	public void refreshDesktopPoolSparingAndAbnormalCount(int iddesktoppool) {
		long refreshedSparingCount = (Long) storageFacade.findFirst(
				"select count(ds.iddesktop) from DesktopStatus ds left join ds.desktop d where d.desktoppoolid = ? and ds.status = ?",
				iddesktoppool, DesktopStatus.DESKTOP_STATUS_SERVING);
		storageFacade.update(
				"update DesktopPoolStatus set sparingdesktops = ? where iddesktoppool = ?",
				(int) refreshedSparingCount, iddesktoppool);
		long refreshedAbnormalCount = (Long) storageFacade.findFirst(
				"select count(ds.iddesktop) from DesktopStatus ds left join ds.desktop d where d.desktoppoolid = ? and ds.status = ?",
				iddesktoppool, DesktopStatus.DESKTOP_STATUS_ERROR);
		storageFacade.update(
				"update DesktopPoolStatus set abnormaldesktops = ? where iddesktoppool = ?",
				(int) refreshedAbnormalCount, iddesktoppool);
	}

	public DesktopStatus findAssignedDesktopStatus(int iddesktoppool, int userid) {
		return (DesktopStatus) storageFacade.findFirst(
				"select ds from DesktopStatus ds left join ds.desktop d where d.desktoppoolid = ? and ds.ownerid = ?",
				iddesktoppool, userid);
	}

	private DesktopStatus findFirstAvailableDesktop(int iddesktoppool) {
		return (DesktopStatus) storageFacade.findFirst(
				"select ds from DesktopStatus ds  join ds.desktop d where d.desktoppoolid = ? and (ds.phase = ? and ds.status != ? and ds.status!=?) and ds.ownerid = ?",
				iddesktoppool, DesktopState.DESKTOP_PHASE_NORMAL,DesktopState.DESKTOP_STATUS_ERROR, DesktopState.DESKTOP_STATUS_UNKNOWN,-1);
	}
//	private Integer findFirstAvailableDesktop(int userid, int iddesktoppool) { 
//		return (Integer) storageFacade.findFirst(
//				"select ds.iddesktop from DesktopStatus ds left join ds.desktop d where" +
//				" d.desktoppoolid = ?" +
//				" and ( ( ds.status != ? and ds.status != ? and ds.ownerid = ? ) or ( ds.status != ? and ds.ownerid = ? ) )",
//				iddesktoppool, DESKTOP_STATUS_CONNECTED, DESKTOP_STATUS_ABNORMAL, -1, DESKTOP_STATUS_ABNORMAL, userid);
//	}

	private void markDesktopPoolAsFull(int iddesktoppool) {
		storageFacade.update(
				"update DesktopPoolStatus set status = ? where iddesktoppool = ?",
				DESKTOPPOOL_STATUS_FULL, iddesktoppool);
	}

	public void setStorageFacade(StorageFacade storageFacade) {
		this.storageFacade = storageFacade;
	}

}
