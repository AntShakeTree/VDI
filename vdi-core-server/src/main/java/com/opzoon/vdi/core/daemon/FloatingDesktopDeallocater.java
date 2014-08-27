package com.opzoon.vdi.core.daemon;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.domain.FloatingDesktopExpire;
import com.opzoon.vdi.core.domain.state.DesktopState;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.operation.DeallocDesktopOperation;
import com.opzoon.vdi.core.operations.OperationRegistry;

public class FloatingDesktopDeallocater {
  private static Logger logger = Logger.getLogger(FloatingDesktopDeallocater.class);
	private OperationRegistry operationRegistry;

	@Scheduled(fixedDelay = 10 * 1000)
	public void run() {
		logger.debug("FloatingDesktopDeallocater  ...... deamon");
		@SuppressWarnings("unchecked")
		List<FloatingDesktopExpire> expires = (List<FloatingDesktopExpire>) operationRegistry
				.getStateMachine()
				.getDatabaseFacade()
				.find("from FloatingDesktopExpire where expire < ?", new Date());
		for (FloatingDesktopExpire expire : expires) {
			int desktoppoolid = expire.getDesktoppoolid();
			int desktopid = expire.getDesktopid();
			operationRegistry
					.getStateMachine()
					.getDatabaseFacade()
					.update("delete from FloatingDesktopExpire where idexpire = ?",
							expire.getIdexpire());
			DesktopState desktopState = (DesktopState) operationRegistry
					.getStateMachine().loadState(
							new Entity(DesktopStatus.class, desktopid));
			if (desktopState.getConnectivity() == DesktopState.DESKTOP_CONNECTIVITY_STANDBY) {
				try {
					operationRegistry
							.start(new DeallocDesktopOperation(Integer
									.toHexString(desktoppoolid)
									+ "#"
									+ Integer.toHexString(desktopid), true,
									null, null));
				} catch (CommonException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void setOperationRegistry(OperationRegistry operationRegistry) {
		this.operationRegistry = operationRegistry;
	}
}
