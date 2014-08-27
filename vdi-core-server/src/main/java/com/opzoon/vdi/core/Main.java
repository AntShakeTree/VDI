package com.opzoon.vdi.core;

import java.util.Arrays;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.opzoon.vdi.core.facade.DatabaseFacade;

public class Main {
	
//	public static void main(String[] args) {
//		ClassPathXmlApplicationContext c = new ClassPathXmlApplicationContext("/spring-database.xml");
//		DatabaseFacade f = (DatabaseFacade) c.getBean("databaseFacade");
//		f.update("update User set username = '' where iduser = 999999999");
//	}
	
	static int[] results = new int[999];
	static int index = 0;
	
	public static void main(String[] args) {
		Thread observer = new Thread() {
			@Override
			public void run() {
				for (;;) {
					synchronized (results) {
						System.out.println("results: " + Arrays.toString(results));
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
				}
			}};
		com.opzoon.ohvc.session.ExcecutorUtil.execute(observer);
		for (;;) {
			final int id;
			synchronized (results) {
				id = index++;
			}
			results[id] = 1;// RUNNING
			Thread t = new Thread() {
				@Override
				public void run() {
					synchronized (results) {
						results[id] = 2;// DONE
					}
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {}
				}};
			com.opzoon.ohvc.session.ExcecutorUtil.execute(t);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}

}
