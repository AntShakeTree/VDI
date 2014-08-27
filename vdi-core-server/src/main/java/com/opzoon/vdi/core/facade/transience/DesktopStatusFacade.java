package com.opzoon.vdi.core.facade.transience;

import static com.opzoon.vdi.core.domain.DesktopPoolEntity.DESKTOP_POOL_ASSIGNMENT_DEDICATED;
import static com.opzoon.vdi.core.domain.DesktopStatus.DESKTOP_STATUS_CONNECTED;
import static com.opzoon.vdi.core.domain.DesktopStatus.DESKTOP_STATUS_ERROR;
import static com.opzoon.vdi.core.domain.DesktopStatus.DESKTOP_STATUS_RUNNING;
import static com.opzoon.vdi.core.domain.DesktopStatus.DESKTOP_STATUS_SERVING;
import static com.opzoon.vdi.core.domain.DesktopStatus.DESKTOP_STATUS_STOPPED;
import static com.opzoon.vdi.core.facade.CommonException.CONFLICT;
import static com.opzoon.vdi.core.facade.CommonException.NOT_FOUND;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.facade.FacadeHelper.exists;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;

import java.util.List;

import javax.persistence.EntityExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opzoon.vdi.core.domain.Desktop;
import com.opzoon.vdi.core.domain.DesktopPool;
import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.DesktopPoolStatus;
import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.domain.state.DesktopState;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.facade.StorageFacade;

/**
 * 桌面状态相关业务接口.
 */
public class DesktopStatusFacade {
	
	private static final Logger log = LoggerFactory.getLogger(DesktopStatusFacade.class);

	private StorageFacade storageFacade;
	
	private DesktopPoolStatusFacade desktopPoolStatusFacade;

	public DesktopStatus findDesktopStatus(Integer iddesktop) {
		return storageFacade.load(DesktopStatus.class, iddesktop);
	}

	public void removeDesktopStatus(Integer iddesktop) {
		storageFacade.update(
				"delete from DesktopStatus where iddesktop = ?",
				iddesktop);
	}

	public int assignDesktopToConnect(int userid, int availableDesktop, DesktopPoolEntity pool) {
		if (exists(storageFacade.update(
				"update DesktopStatus set ownerid = ? where iddesktop = ? and (ownerid = ? or ownerid = ?)",
				userid, availableDesktop, -1, userid))) {
			desktopPoolStatusFacade.markDesktopPoolAsFullIfNeeded(pool);
			return NO_ERRORS;
		}
		return NOT_FOUND;
	}

	public int markAsConnected(int availableDesktop) {
		if (exists(storageFacade.update(// FIXME
				"update DesktopStatus set connected = 1 where iddesktop = ? and connected != 1",
				availableDesktop))) {
			return NO_ERRORS;
		}
		return NOT_FOUND;
	}

	/**
	 * 在数据库中取消某桌面的绑定用户.
	 * 
	 * @param iddesktop 桌面ID.
	 * @param pool 桌面池.
	 */
	public void disconnectDesktop(Integer iddesktop, int assignment) {
		if (numberNotEquals(assignment, DESKTOP_POOL_ASSIGNMENT_DEDICATED)) {
			storageFacade.update(// FIXME
					"update DesktopStatus set connected = 0, ownerid = ? where iddesktop = ?",
					-1, iddesktop);
		} else {
			storageFacade.update(// FIXME
					"update DesktopStatus set connected = 0 where iddesktop = ?",
					iddesktop);
		}
	}

	public void unassignDesktop(DesktopPoolEntity pool, Integer iddesktop, int userid) {
		if (pool.getAssignment() == DesktopPool.DESKTOP_POOL_ASSIGNMENT_FLOAT) {
			storageFacade.update(
					"update DesktopStatus set status = ? where iddesktop = ? and status = ? and ownerid = ?",
					DesktopStatus.DESKTOP_STATUS_SERVING, iddesktop, DesktopStatus.DESKTOP_STATUS_CONNECTED, userid);
		}
		storageFacade.update(
				"update DesktopStatus set ownerid = ? where iddesktop = ? and ownerid = ?",
				-1, iddesktop, userid);
	}

	@SuppressWarnings("unchecked")
	public List<DesktopStatus> findAssignedDesktops(int deletedUser) {
		return (List<DesktopStatus>) storageFacade.find("from DesktopStatus where ownerid = ?", deletedUser);
	}

	public void createNewDesktopStatus(Integer iddesktop, int status, int ownerid) throws CommonException {
		DesktopStatus desktopStatus = new DesktopStatus();
		desktopStatus.setIddesktop(iddesktop);
		desktopStatus.setStatus(DesktopState.DESKTOP_STATUS_UNKNOWN);
		desktopStatus.setOwnerid(ownerid);
		try {
			storageFacade.persist(desktopStatus);
		} catch (EntityExistsException e) {
			throw new CommonException(CONFLICT);
		}
	}

	public void refreshDesktopStatus(Desktop desktop, int status) {
		if(exists(storageFacade.findFirst(
				"select count(iddesktop) from DesktopStatus where iddesktop = ?",
				desktop.getIddesktop()))) {
			storageFacade.update(
					"update DesktopStatus set status = ? where iddesktop = ?",
					status, desktop.getIddesktop());
		} else {
			try {
				this.createNewDesktopStatus(
						desktop.getIddesktop(),
						status,
						desktop.getOwnerid());
			} catch (CommonException e) {
				log.warn("createNewDesktopStatus failed.", e);
			}
		}
		DesktopPoolStatus desktopPoolStatus = desktopPoolStatusFacade.findDesktopPoolStatus(desktop.getDesktoppoolid());
		if (desktopPoolStatus == null) {
			try {
				desktopPoolStatusFacade.createNewDesktopPoolStatus(
						desktop.getDesktoppoolid());
			} catch (CommonException e) {
				log.warn("createNewDesktopPoolStatus failed.", e);
			}
		}
		desktopPoolStatusFacade.refreshDesktopPoolSparingAndAbnormalCount(desktop.getDesktoppoolid());
	}

	public void setStorageFacade(StorageFacade storageFacade) {
		this.storageFacade = storageFacade;
	}

	public void setDesktopPoolStatusFacade(
			DesktopPoolStatusFacade desktopPoolStatusFacade) {
		this.desktopPoolStatusFacade = desktopPoolStatusFacade;
	}

}
