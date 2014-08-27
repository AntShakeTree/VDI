package com.opzoon.appstatus.common;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.opzoon.appstatus.domain.RepairNode;
import com.opzoon.appstatus.executor.Repair;
import com.opzoon.ohvc.session.DelayItem;
import com.opzoon.ohvc.session.ExcecutorUtil;
import com.opzoon.ohvc.session.Session;

public class RepairTools {
	private static DelayQueue<DelayItem<RepairNode>> QUEUE = new DelayQueue<DelayItem<RepairNode>>();
	private static final AtomicBoolean isRun = new AtomicBoolean(false);

	public static void delayRepaire(Repair repair, RepairNode repairNode,
			long vluae, TimeUnit unit) {
		long nanoTime = TimeUnit.NANOSECONDS.convert(vluae, unit);
		QUEUE.put(new DelayItem<RepairNode>(repairNode, nanoTime));
		start(repair, repairNode);
		Session.setCache(repairNode.getHost(), repairNode);
	}

	private static void start(final Repair repair, RepairNode repairNode) {
		Runnable run = new Runnable() {
			@Override
			public void run() {
				boolean isrun = true;
				while (isrun) {
					if (QUEUE.isEmpty()) {
						isrun = false;
					}
					DelayItem<RepairNode> delayItem = null;
					try {
						delayItem = QUEUE.take();
					} catch (InterruptedException e1) {
						isrun = false;
					}
					RepairNode node = delayItem.getItem();
					if (Session.getCache(node.getHost()) != null) {
						
						repair.repair(node.getCommand());
						//移除 修复队列
						RepairTools.removeRepair(node.getHost());
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		if (!isRun.get()) {
			ExcecutorUtil.execute(run);
			isRun.compareAndSet(false,true);
		}
	}

	public static void removeRepair(String host) {
		 Session.removeCache(host);
	}

	private RepairTools() {
	}


}
