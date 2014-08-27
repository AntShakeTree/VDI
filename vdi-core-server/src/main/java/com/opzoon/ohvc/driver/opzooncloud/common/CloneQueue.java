package com.opzoon.ohvc.driver.opzooncloud.common;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.opzoon.ohvc.driver.opzooncloud.domain.OpzoonCloudVMInstance;

/**
 * 
 * @author maxiaochao
 * 
 */
public class CloneQueue {
	private static BlockingQueue<String> queue = new ArrayBlockingQueue<String>(
			100);

	public static void put(String e) {
		try {
			queue.put(e);
		} catch (InterruptedException e1) {
		}
	}

	public static String take() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			return null;
		}
	}

	public static boolean contains(String e) {
		return queue.contains(e);
	}
	
	
	public static void main(String[] args) {
		String v11 = "112";
		String v13 = "113";
		put(v11);
		put(v13);
		System.out.println(queue.size());
		String v12 = take();
		System.out.println(v12);
		String v14 = take();
		System.out.println(v14);
		System.out.println(queue.size());

	}
}
