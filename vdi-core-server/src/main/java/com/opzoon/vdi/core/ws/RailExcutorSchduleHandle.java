package com.opzoon.vdi.core.ws;

import java.util.List;

import org.apache.log4j.Logger;

import com.opzoon.ohvc.common.ConfigUtil;
import com.opzoon.ohvc.common.Job;
import com.opzoon.ohvc.common.JobStatus;
import com.opzoon.ohvc.common.RailAppError;
import com.opzoon.ohvc.common.RailResponse;
import com.opzoon.ohvc.domain.AgentRailApplication;
import com.opzoon.ohvc.domain.AgentRailApplicationServer;
import com.opzoon.ohvc.domain.RailInformation;
import com.opzoon.ohvc.service.VdiAgentClientImpl;
import com.opzoon.vdi.core.app.domain.RailApplication;
import com.opzoon.vdi.core.app.domain.RailApplicationServer;
import com.opzoon.vdi.core.app.domain.RailApplicationView;
import com.opzoon.vdi.core.facade.DatabaseFacade;
import com.opzoon.vdi.core.util.BeanUtils;

/**
 * ClassName: RailExcutorSchduleHandle
 * 
 * @Description: 定时更新rail
 * @author David
 * @date 2013-1-31 下午6:34:46
 */
public class RailExcutorSchduleHandle {
	private DatabaseFacade databaseFacade;
	private static Logger log = Logger.getLogger(RailExcutorSchduleHandle.class);

	@SuppressWarnings("unchecked")
	public void updateRailByAgent() {
		List<com.opzoon.vdi.core.app.domain.RailApplicationServer> rs = (List<com.opzoon.vdi.core.app.domain.RailApplicationServer>) this.databaseFacade
				.find("from RailApplicationServer");
		for (com.opzoon.vdi.core.app.domain.RailApplicationServer railAppServer : rs) {
			int connections = railAppServer.getConnections();
			if (railAppServer.getStatus() == RailApplicationServer.RAIL_APPLICATION_SERVER_STATUS_DELEING && railAppServer.getConnections() <= 0) {
				this.databaseFacade.update("delete from RailApplicationServer where idapplicationserver=?", railAppServer.getIdapplicationserver());
				this.databaseFacade.update("delete from RailApplication where applicationserverid=?", railAppServer.getIdapplicationserver());
				continue;
			}
			
			// 更新应用的状态
			try {
			
				RailResponse<AgentRailApplicationServer> railResponse = VdiAgentClientImpl.getPerformanceCounter(railAppServer.getServername(),
						Integer.parseInt(ConfigUtil.getBykey("performanceCounter")));
				AgentRailApplicationServer railApplicationServer = railResponse.getBody();
				BeanUtils.copyProperties(railAppServer, railApplicationServer, false);
				if (!VdiAgentClientImpl.getRDPStatus(railAppServer.getServername())) {
					railAppServer.setStatus(RailApplicationServer.RAIL_APPLICATION_SERVER_STATUS_RUNNING);
				} else {
					railAppServer.setStatus(RailApplicationServer.RAIL_APPLICATION_SERVER_STATUS_SERVING);
				}
				// Evan
				Object countPubulishO = this.databaseFacade.findFirst("select count(*) from RailApplication where published=? and applicationserverid=?", true,
						railAppServer.getIdapplicationserver());
				int countPubulish = 0;
				if (countPubulishO != null) {
					countPubulish = Integer.parseInt(countPubulishO + "");
				}
				railAppServer.setApppublished(countPubulish);
				// Evan
				Object countAllO = this.databaseFacade.findFirst("select count(*) from RailApplication where  applicationserverid=?",
						railAppServer.getIdapplicationserver());
				int countAll = 0;
				if (countAllO != null) {
					countAll = Integer.parseInt(countAllO + "");
				}
				railAppServer.setAppinstalled(countAll);
				// Vdi agent
				RailResponse<RailInformation> resRailInformation = VdiAgentClientImpl.getHostJoinInformation(railAppServer.getServername());

				BeanUtils.copyProperties(railAppServer, resRailInformation.getBody(), false);
				railAppServer.setConnections(connections);
				this.databaseFacade.merge(railAppServer);
				
			
				RailResponse<List<AgentRailApplication>> ls = VdiAgentClientImpl.listRailApplications(railAppServer.getServername());
				if(ls.getHead().getError()!=0){
					this.databaseFacade.update("delete from RailApplication where applicationserverid=?", railAppServer.getIdapplicationserver());
					continue;
				}
				if (ls.getBody().size() <= 0) {
					// 先更新成离线
					this.databaseFacade.update("update RailApplication set status=?,servername=? where applicationserverid=?", RailApplication.OFFLINE,
							railAppServer.getServername(), railAppServer.getIdapplicationserver());
				}
				for (AgentRailApplication railAppllication : ls.getBody()) {
					com.opzoon.vdi.core.app.domain.RailApplication RailAppllication2 = (com.opzoon.vdi.core.app.domain.RailApplication) this.databaseFacade
							.findFirst("from RailApplication where servername=? and applicationid=?", railAppServer.getServername(),
									railAppllication.getApplicationid());
					if (RailAppllication2 != null) {
						BeanUtils.copyProperties(RailAppllication2, railAppllication, false);
						RailAppllication2.setStatus(RailApplication.ONLINE);
						this.databaseFacade.merge(RailAppllication2);
					} else {
						railAppllication.setServername(railAppServer.getServername());
						railAppllication.setApplicationserverid(railAppServer.getIdapplicationserver());
						railAppllication.setStatus(RailApplication.ONLINE);
						com.opzoon.vdi.core.app.domain.RailApplication RailAppllication3 = new com.opzoon.vdi.core.app.domain.RailApplication();
						BeanUtils.copyProperties(RailAppllication3, railAppllication, false);
						this.databaseFacade.persist(RailAppllication3);
					}
				}
				Job<String> job = VdiAgentClientImpl.listRailApplicationsFreshen(railAppServer.getServername());
				if (job.getError() == RailAppError.RAIL_AGETN_NOTINSTALL_ROLE.getError()) {
					this.databaseFacade.update("delete from RailApplication where applicationserverid=?", railAppServer.getIdapplicationserver());
					continue;
				}
			} catch (Exception e) {
				log.error(e.getCause() + ":" + e.getMessage() + "  " + e.getLocalizedMessage());
				this.databaseFacade.update("update RailApplicationServer set status=? where idapplicationserver=?",
						RailApplicationServer.RAIL_APPLICATION_SERVER_STATUS_ERROR, railAppServer.getIdapplicationserver());
				this.databaseFacade.update("update RailApplication set status=? where applicationserverid=?", RailApplication.Error,
						railAppServer.getIdapplicationserver());

				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void updateRailView() {
		List<RailApplicationView> rvs = (List<RailApplicationView>) this.databaseFacade.find("from RailApplicationView");
		for (RailApplicationView railApplicationView : rvs) {
			// Evan
			Object countO = this.databaseFacade.findFirst("select count(*) from RailApplication where applicationid=? and status=1 and published=true",
					railApplicationView.getApplicationid());
			int count = 0;
			if (countO != null) {
				count = Integer.parseInt("" + countO);

			}
			if (count > 0) {
				railApplicationView.setStatus(1);
				railApplicationView.setPublished(true);
			} else {
				railApplicationView.setPublished(false);
				railApplicationView.setStatus(RailApplication.Error);
			}
			railApplicationView.setReplication(count);
			if (railApplicationView.getStatus() != 1 && railApplicationView.getReplication() <= 0) {
				Object numg = this.databaseFacade.findFirst("select count(*) from RailApplicationToGroup where applicationid=?",
						railApplicationView.getApplicationid());
				Object numu = this.databaseFacade.findFirst("select count(*) from RailApplicationToUser where applicationid=?",
						railApplicationView.getApplicationid());
				Object numO = this.databaseFacade.findFirst("select count(*) from RailApplicationToOrganization where applicationid=?",
						railApplicationView.getApplicationid());
				int num1 = 0;
				if (numg != null) {
					num1 = Integer.parseInt(numg + "");
				}
				int num2 = 0;
				if (numu != null) {
					num2 = Integer.parseInt(numu + "");
				}
				int num3 = 0;
				if (numO != null) {
					num3 = Integer.parseInt(numO + "");
				}
				if (num1 + num2 + num3 == 0) {
					this.databaseFacade.update("delete from RailApplicationView where applicationid=?", railApplicationView.getApplicationid());
					continue;
				}
			}
			this.databaseFacade.merge(railApplicationView);
		}

	}

	public void deleteView() {
		@SuppressWarnings("unchecked")
		List<RailApplicationView> rvs = (List<RailApplicationView>) this.databaseFacade.find("from RailApplicationView");
		for (RailApplicationView railApplicationView : rvs) {
			// Evan
			Object countO = this.databaseFacade.findFirst("select count(*) from RailApplication where applicationid=? and status=1 and published=true",
					railApplicationView.getApplicationid());
			int count = 0;
			if (countO != null) {
				count = Integer.parseInt("" + countO);

			}
			if (count > 0) {
				railApplicationView.setStatus(1);
				railApplicationView.setPublished(true);
			} else {
				railApplicationView.setPublished(false);
				railApplicationView.setStatus(RailApplication.Error);
			}
			railApplicationView.setReplication(count);
			if (railApplicationView.getStatus() != 1 && railApplicationView.getReplication() <= 0) {
				Object numg = this.databaseFacade.findFirst("select count(*) from RailApplicationToGroup where applicationid=?",
						railApplicationView.getApplicationid());
				Object numu = this.databaseFacade.findFirst("select count(*) from RailApplicationToUser where applicationid=?",
						railApplicationView.getApplicationid());
				Object numO = this.databaseFacade.findFirst("select count(*) from RailApplicationToOrganization where applicationid=?",
						railApplicationView.getApplicationid());
				int num1 = 0;
				if (numg != null) {
					num1 = Integer.parseInt(numg + "");
				}
				int num2 = 0;
				if (numu != null) {
					num2 = Integer.parseInt(numu + "");
				}
				int num3 = 0;
				if (numO != null) {
					num3 = Integer.parseInt(numO + "");
				}
				if (num1 + num2 + num3 == 0) {
					this.databaseFacade.remove(railApplicationView);
				}
			}

			this.databaseFacade.merge(railApplicationView);
		}
	}

	/**
	 * @return databaseFacade
	 */
	public DatabaseFacade getDatabaseFacade() {
		return databaseFacade;
	}

	/**
	 * @param databaseFacade
	 *            the databaseFacade to set
	 */
	public void setDatabaseFacade(DatabaseFacade databaseFacade) {
		this.databaseFacade = databaseFacade;
	}

}
