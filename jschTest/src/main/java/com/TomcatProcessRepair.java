package com;

public class TomcatProcessRepair extends Repair {
	Repair repair;
	public TomcatProcessRepair(Repair repair) {
		this.repair = repair;
	}

	public TomcatProcessRepair() {
	}

	String process;

	public boolean normal(String command) {
		if (repair != null)
			repair.normal(command);
		if (command == null || "".equals(command)) {
			command = "ps -ef| grep tomcat |grep -v grep |grep -v zookeeper | awk '{print $2}'";
		}
		process = conncetion.exec(command);
		return process != null;
	}

	public boolean repair(String command) {
		String result = "";

		try {
			if (repair != null)
				repair.repair(command);
			if (command != null && !"".equals(command.trim())) {
				result = conncetion.exec(command);
			} else {
				if (process != null) {
					conncetion.exec("kill " + process);
				}
				result = conncetion.exec("service tomcat6 start");
			}
		} finally {
			conncetion.close();
		}
		return result != null && result.contains("OK");
	}

}
