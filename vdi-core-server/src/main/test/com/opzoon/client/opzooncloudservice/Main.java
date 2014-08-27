/**   
 * @Title: Main.java t
 * @Package com.opzoon.client.opzooncloudservice 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Nathan   
 * @date 2012-12-5 下午1:49:44 
 * @version V1.0   
 */
package com.opzoon.client.opzooncloudservice;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.opzoon.appstatus.common.RepairTools;
import com.opzoon.appstatus.domain.RepairNode;
import com.opzoon.appstatus.executor.Repair;
import com.opzoon.ohvc.session.ExcecutorUtil;
import com.opzoon.ohvc.session.Session;
import com.opzoon.ohvc.session.State;

/**
 * @ClassName: Main
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author maxiaochao
 * @date 2012-12-5 下午1:49:44
 */
public class Main {
	public static void main(String[] args) {
		int i = 1;
		while(true){
			Runnable r=new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					System.out.println("=============");
				}
			};
			i++;
			System.out.println(i);
			ExcecutorUtil.execute(r);
		}
	}

	public static void test() throws IllegalAccessException, IOException {
		// State<String> string = Session.getStateBySeed("123");
		// string.openDoor();
		// string.putCache("222", "1");
		// string.putCache("223", "2");
		// string.putCache("224", "3");
		// string.putCache("225", "4");
		// string.putCache("226", "5");
		// // string.view();
		// // string2.gc();
		// // string.view();
		// for (String s : string.listAll()) {
		// System.out.println(s+"============");
		// }
		// string.closeDoor();
		// string.openDoor();
		// string.putCache("222", "1==>");
		// string.clearValidDataBySeed("123");
		// // string2.gc();
		// // string.view();
		// for (String s : string.listAll()) {
		// System.out.println(s+"============");
		// }
		// string.closeDoor();

		Callable<Void> call = new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				return null;
			}
		};
		ExecutorService service = Executors.newCachedThreadPool();
		Future<Void> f = service.submit(call);
		try {
			f.get(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			f.cancel(true);

			System.out.println(f.isCancelled());
		}
		for (int i = 1; i < 11; i++) {
			// ServerSocket serverSocket = new ServerSocket(2222+i);
			new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
					}
				}
			}).start();
		}
		while (true) {
		}
	}

	public void testRepairTools() {
		Repair repair = new Repair();
		RepairNode repairNode = new RepairNode("1.1.1.1", "root", "111111");
		RepairTools.delayRepaire(repair, repairNode, 10, TimeUnit.SECONDS);
	}
}
