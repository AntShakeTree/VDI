package com.opzoon.appstatus.executor;

import com.opzoon.appstatus.domain.RepairNode;

public class TomcatProcessRepair extends Repair {
	Repair repair;
	public static String REPAIRCOMMAND="killall -9 java ; service tomcat6 start";
	public TomcatProcessRepair(Repair repair) {
		this.repair = repair;
	}

	public TomcatProcessRepair() {
	}

	String process;

	public boolean normal(RepairNode domain, String command) {
		if (repair != null)
			repair.normal(domain, command);
		for (SSHConncetion conncetion : getConncetions()) {
			if (command == null || "".equals(command)) {
				command = "ps -ef| grep tomcat |grep -v grep |grep -v zookeeper | awk '{print $2}'";
			}
			process = conncetion.exec();
			if (process != null) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
}
