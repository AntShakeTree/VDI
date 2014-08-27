package com.opzoon.appstatus.executor;


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

}
