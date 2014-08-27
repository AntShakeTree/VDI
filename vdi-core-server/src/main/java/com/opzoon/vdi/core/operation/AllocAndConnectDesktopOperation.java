package com.opzoon.vdi.core.operation;

import static com.opzoon.vdi.core.cloud.CloudManagerHelper.findCloudManager;
import static com.opzoon.vdi.core.domain.AsyncJob.ASYNC_JOB_STATUS_FAILURE;
import static com.opzoon.vdi.core.domain.AsyncJob.ASYNC_JOB_STATUS_SUCCESS;
import static com.opzoon.vdi.core.facade.CommonException.CONFLICT;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opzoon.ohvc.common.Job;
import com.opzoon.ohvc.service.VdiAgentClientImpl;
import com.opzoon.vdi.core.cloud.CloudManager;
import com.opzoon.vdi.core.domain.CloudManagerEntity;
import com.opzoon.vdi.core.domain.Desktop;
import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.DesktopPoolStatus;
import com.opzoon.vdi.core.domain.DesktopStatus;
import com.opzoon.vdi.core.domain.Domain;
import com.opzoon.vdi.core.domain.User;
import com.opzoon.vdi.core.domain.VMInstance;
import com.opzoon.vdi.core.domain.state.DesktopState;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.fsm.Entity;
import com.opzoon.vdi.core.fsm.StateMatcher;
import com.opzoon.vdi.core.matcher.NormalDesktopPoolMatcher;
import com.opzoon.vdi.core.operations.Event;
import com.opzoon.vdi.core.operations.Operation;
import com.opzoon.vdi.core.operations.OperationContext;
import com.opzoon.vdi.core.refresher.AgentJobRefresher;
import com.opzoon.vdi.core.request.AllocateDesktopRequest;
import com.opzoon.vdi.core.request.ConnectDesktopRequest;

public class AllocAndConnectDesktopOperation implements Operation {

	private static final Logger log = LoggerFactory
			.getLogger(AllocAndConnectDesktopOperation.class);

	private final int userid;
	private final int desktoppoolid;
	private final int brokerprotocol;
	private final int sessionid;
	private final String password;
	private final int jobid;

	public AllocAndConnectDesktopOperation(
			String useridNdesktoppoolidNbrokerprotocolNsessionid,
			String password, int jobid) {
		log.info("Constructing");
		String[] ps = useridNdesktoppoolidNbrokerprotocolNsessionid.split("#");
		this.userid = Integer.parseInt(ps[0], 16);
		this.desktoppoolid = Integer.parseInt(ps[1], 16);
		this.brokerprotocol = Integer.parseInt(ps[2], 16);
		this.sessionid = Integer.parseInt(ps[3], 16);
		this.password = password;
		this.jobid = jobid;
		log.info("Constructed");
	}

	@Override
	public Object execute(OperationContext operationContext)
			throws CommonException {
		log.info("Exectuing");
		final DesktopStatus candidateDesktop;
		final DesktopStatus allocatedDesktop = (DesktopStatus) operationContext
				.getOperationRegistry()
				.getStateMachine()
				.getDatabaseFacade()
				.findFirst(
						"select ds from DesktopStatus ds left join ds.desktop d where d.desktoppoolid = ? and ds.ownerid = ?",
						desktoppoolid, userid);
		if (allocatedDesktop == null) {
			candidateDesktop = (DesktopStatus) operationContext
					.getOperationRegistry()
					.getStateMachine()
					.getDatabaseFacade()
					.findFirst(
							"select ds from DesktopStatus ds left join ds.desktop d where d.desktoppoolid = ? and ds.phase = ? and ds.status = ? and ds.ownerid = ?",
							desktoppoolid, DesktopState.DESKTOP_PHASE_NORMAL,
							DesktopState.DESKTOP_STATUS_SERVING, -1);
		} else {
			candidateDesktop = allocatedDesktop;
		}
		if (candidateDesktop == null) {
			operationContext
					.getOperationRegistry()
					.getStateMachine()
					.getDatabaseFacade()
					.update("update AsyncJob set jobstatus = ?, jobprocstatus = ?, jobresultcode = ?, jobresult = ? where jobId = ?",
							ASYNC_JOB_STATUS_FAILURE, 100,
							CommonException.NOT_FOUND, "", jobid);
			throw new CommonException(CommonException.NOT_FOUND);
		}
		Desktop desktop = operationContext.getOperationRegistry()
				.getStateMachine().getDatabaseFacade()
				.load(Desktop.class, candidateDesktop.getIddesktop());
		String ipaddress = null;
		VMInstance vm = null;
		DesktopPoolEntity desktopPool = operationContext.getOperationRegistry()
				.getStateMachine().load(DesktopPoolEntity.class, desktoppoolid);
		if (desktopPool.getVmsource() == DesktopPoolEntity.DESKTOP_POOL_SOURCE_MANUAL) {
			ipaddress = desktop.getIpaddress();
		} else {
			final CloudManager cloudManager = findCloudManager(operationContext
					.getOperationRegistry()
					.getStateMachine()
					.getDatabaseFacade()
					.load(CloudManagerEntity.class,
							desktopPool.getCloudmanagerid()));
			try {
				vm = cloudManager.getVM(desktop.getVmid());
			} catch (Exception e) {
				// TODO
				e.printStackTrace();
				operationContext
						.getOperationRegistry()
						.getStateMachine()
						.getDatabaseFacade()
						.update("update AsyncJob set jobstatus = ?, jobprocstatus = ?, jobresultcode = ?, jobresult = ? where jobId = ?",
								ASYNC_JOB_STATUS_FAILURE, 100,
								CommonException.HYPERVISOR_ABNORMAL, "", jobid);
				throw new CommonException(CommonException.HYPERVISOR_ABNORMAL);
			}
			ipaddress = vm.getIpaddress();
			if (!vm.getState().equalsIgnoreCase("Running")) {
				if (vm.getState().equalsIgnoreCase("Stopped")) {
					try {
						cloudManager.startVM(desktop.getVmid());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				operationContext
						.getOperationRegistry()
						.getStateMachine()
						.getDatabaseFacade()
						.update("update AsyncJob set jobstatus = ?, jobprocstatus = ?, jobresultcode = ?, jobresult = ? where jobId = ?",
								ASYNC_JOB_STATUS_FAILURE, 100,
								CommonException.CONFLICT, "", jobid);
				throw new CommonException(CONFLICT);
			}
		}
		try {
			if (!VdiAgentClientImpl.getRDPStatus(ipaddress)) {
				throw new CommonException(CommonException.CONFLICT);
			}
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			operationContext
					.getOperationRegistry()
					.getStateMachine()
					.getDatabaseFacade()
					.update("update AsyncJob set jobstatus = ?, jobprocstatus = ?, jobresultcode = ?, jobresult = ? where jobId = ?",
							ASYNC_JOB_STATUS_FAILURE, 100,
							CommonException.CONFLICT, "", jobid);
			throw new CommonException(CommonException.CONFLICT);
		}
		if (candidateDesktop.getOwnerid() == -1) {
			try {
				operationContext.getOperationRegistry().publishStateRequest(
						new AllocateDesktopRequest(desktoppoolid,
								candidateDesktop.getIddesktop(), userid));
			} catch (CommonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				operationContext
						.getOperationRegistry()
						.getStateMachine()
						.getDatabaseFacade()
						.update("update AsyncJob set jobstatus = ?, jobprocstatus = ?, jobresultcode = ?, jobresult = ? where jobId = ?",
								ASYNC_JOB_STATUS_FAILURE, 100, e.getError(),
								"", jobid);
				throw e;
			}
		}
		User user = operationContext.getOperationRegistry().getStateMachine()
				.getDatabaseFacade().load(User.class, userid);
		operationContext
				.getOperationRegistry()
				.getStateMachine()
				.getDatabaseFacade()
				.update("update Desktop set ownername = ?,realname=?  where iddesktop = ?",
						user.getUsername(),user.getRealname(), candidateDesktop.getIddesktop());
		if (user.getDomainid() == Domain.DEFAULT_DOMAIN_ID) {
			try {
				Job<String> job;
				job = VdiAgentClientImpl.createUser(ipaddress,
						user.getUsername(), password);
				operationContext.getOperationRegistry().refreshFrequently(this,
						operationContext, new AgentJobRefresher(job));
				if (job.getError() == -520027389) {
					operationContext
							.getOperationRegistry()
							.getStateMachine()
							.getDatabaseFacade()
							.update("update AsyncJob set jobstatus = ?, jobprocstatus = ?, jobresultcode = ?, jobresult = ? where jobId = ?",
									ASYNC_JOB_STATUS_FAILURE, 100,
									CommonException.INVALID_PASSWOED_POLICY,
									"", jobid);
					throw new CommonException(
							CommonException.INVALID_PASSWOED_POLICY);
				}
				job = VdiAgentClientImpl.updateUserPassword(ipaddress,
						user.getUsername(), password);
				operationContext.getOperationRegistry().refreshFrequently(this,
						operationContext, new AgentJobRefresher(job));

			} catch (Exception e) {
				e.printStackTrace();
				log.error("CommonException ===> :: "
						+ ((CommonException) e).getError());

				if (e instanceof CommonException) {
					log.error("CommonException ===> :: "
							+ ((CommonException) e).getError());
					throw (CommonException) e;
				}
			}
		} else {
			try {
				Job<String> job;
				job = VdiAgentClientImpl.addUserToLocalGroup(ipaddress,
						user.getUsername(), desktopPool.getDomainname(),
						"Remote Desktop Users");
				operationContext.getOperationRegistry().refreshFrequently(this,
						operationContext, new AgentJobRefresher(job));
				job = VdiAgentClientImpl.addUserToLocalGroup(ipaddress,
						user.getUsername(), desktopPool.getDomainname(),
						"Administrators");
				operationContext.getOperationRegistry().refreshFrequently(this,
						operationContext, new AgentJobRefresher(job));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			VdiAgentClientImpl.logonUser(ipaddress, user.getDomainname(),
					user.getUsername(), password, brokerprotocol);
		} catch (Exception e) {
			// e1.printStackTrace();
			log.error(e.getMessage(),e);
			operationContext
					.getOperationRegistry()
					.getStateMachine()
					.getDatabaseFacade()
					.update("update AsyncJob set jobstatus = ?, jobprocstatus = ?, jobresultcode = ?, jobresult = ? where jobId = ?",
							ASYNC_JOB_STATUS_FAILURE, 100,
							CommonException.CONFLICT, "", jobid);
			throw new CommonException(CommonException.CONFLICT);
		}

		final boolean spice = brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTPS
				|| brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE_OVER_HTTP
				|| brokerprotocol == DesktopPoolEntity.PROTOCOL_SPICE;
		final String hostname;
		final int hostport;
		if (desktopPool.getVmsource() == DesktopPoolEntity.DESKTOP_POOL_SOURCE_MANUAL) {
			hostname = ipaddress;
			hostport = 3389;
		} else {
			hostname = spice ? vm.getHost() : vm.getIpaddress();
			hostport = spice ? vm.getPort() : 3389;
		}
		final int[] connectionIdWrapper = new int[1];
		try {
			operationContext.getOperationRegistry().publishStateRequest(
					new ConnectDesktopRequest(desktopPool.getIddesktoppool(),
							desktop.getIddesktop(), sessionid, brokerprotocol,
							hostname, hostport, operationContext
									.getOperationRegistry().getConfiguration(),
							operationContext.getOperationRegistry()
									.getConnectionManager(),
							connectionIdWrapper));
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (desktopPool.getAssignment() == DesktopPoolEntity.DESKTOP_POOL_ASSIGNMENT_FLOATING) {
				operationContext.getOperationRegistry().start(
						new DeallocDesktopOperation(Integer
								.toHexString(desktoppoolid)
								+ "#"
								+ Integer.toHexString(desktop.getIddesktop()),
								true, sessionid, user.getUsername()));
				operationContext
						.getOperationRegistry()
						.getStateMachine()
						.getDatabaseFacade()
						.update("update AsyncJob set jobstatus = ?, jobprocstatus = ?, jobresultcode = ?, jobresult = ? where jobId = ?",
								ASYNC_JOB_STATUS_FAILURE, 100,
								CommonException.UNKNOWN, "", jobid);
				throw new CommonException(CommonException.UNKNOWN);
			}
		}
		operationContext
				.getOperationRegistry()
				.getStateMachine()
				.getDatabaseFacade()
				.update("update AsyncJob set jobstatus = ?, jobprocstatus = ?, jobresultcode = ?, jobresult = ? where jobId = ?",
						ASYNC_JOB_STATUS_SUCCESS, 100, 0,
						"" + connectionIdWrapper[0], jobid);
		return connectionIdWrapper[0];
	}

	@Override
	public boolean rejects(Operation operation) {
		return this.equals(operation);
	}

	@Override
	public boolean delays(Operation operation) {
		return false;
	}

	@Override
	public boolean onEvent(OperationContext operationContext, Event event) {
		return false;
	}

	@Override
	public Map<Entity, StateMatcher> getNeededStates() {
		final Map<Entity, StateMatcher> neededStates = new HashMap<Entity, StateMatcher>();
		neededStates.put(new Entity(DesktopPoolStatus.class, desktoppoolid),
				new NormalDesktopPoolMatcher());
		return neededStates;
	}

	@Override
	public List<Object> getParams() {
		return Arrays.asList(new Object[] {
				Integer.toHexString(userid) + "#"
						+ Integer.toHexString(desktoppoolid) + "#"
						+ Integer.toHexString(brokerprotocol) + "#"
						+ Integer.toHexString(sessionid), password, jobid });
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (!object.getClass().equals(this.getClass())) {
			return false;
		}
		final AllocAndConnectDesktopOperation that = (AllocAndConnectDesktopOperation) object;
		return this.userid == that.userid
				&& this.desktoppoolid == that.desktoppoolid;
	}

	@Override
	public int hashCode() {
		return (userid + "-" + desktoppoolid).hashCode();
	}

}
