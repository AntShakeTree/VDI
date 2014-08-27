package com.opzoon.appstatus.executor;

import java.util.ArrayList;
import java.util.List;

import com.opzoon.appstatus.domain.RepairNode;

public class Repair {
	private List<SSHConncetion> conncetions = new ArrayList<SSHConncetion>();
	public boolean normal(RepairNode value ,String command) {
		return true;
	}

	public void addRepaireNode(RepairNode value) {
		SSHConncetion conncetion = new SSHConncetion();
		conncetion.build(value.getHost(), value.getUseranme(),
				value.getPassword());
		conncetions.add(conncetion);
	}



	public void repair(String command) {
		for (SSHConncetion conncetion : conncetions) {
			conncetion.setCommand(command);
			conncetion.exec();
		}
	}

	public List<SSHConncetion> getConncetions() {
		return conncetions;
	}

}
