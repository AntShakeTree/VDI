package com;

public class ZookeeperProcessRepaire extends Repair {
	Repair repair;

	public ZookeeperProcessRepaire(Repair repair) {

		this.repair = repair;

	}

	public ZookeeperProcessRepaire() {
		if (repair != null) {
			//
		}
	}

	@Override
	public boolean normal(String command) {
		boolean isNormal = false;
		if (this.repair != null)
			isNormal = this.repair.normal(command);
		// 自己的实现
		// isNormal=this.exec(command);
		return isNormal;
	}

	@Override
	public boolean repair(String command) {
		try {
			if (this.repair != null)
				this.repair.repair(command);
			// 自己的实现
		} finally {
			// close();
		}

		return false;
	}

	public static void main(String[] args) {
		TomcatProcessRepair tomcatProcessConstraint= new TomcatProcessRepair();
		Repair repair = new ZookeeperProcessRepaire(tomcatProcessConstraint);
		SSHConncetion conncetion = new SSHConncetion();
		conncetion.build("20.1.136.100", "root", "111111");
		tomcatProcessConstraint.setConncetion(conncetion);
		repair.repair("");
	}
}
