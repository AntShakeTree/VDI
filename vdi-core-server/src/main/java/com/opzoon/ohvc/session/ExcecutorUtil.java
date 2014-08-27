package com.opzoon.ohvc.session;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.opzoon.ohvc.common.Constants;

/**
 * 线程工具类 ，开启各种线材，call，run，schedue 线材池数，通过cpu数量计算的。 单核48个这个值是经验值，跑会议软件跑出来的经验。
 * 
 * @ClassName: ExcecutorUtil.java
 * @Description: ExcecutorUtil.java
 * @author: maxiaochao
 * @date: 2012-9-17
 * @version: V04
 */
public class ExcecutorUtil {
	final static org.apache.log4j.Logger log = Logger
			.getLogger(ExcecutorUtil.class);
	// 线程池名称
	public static String ThreadPool_Common = "CommonPool";
	// 2个线程池，一个给Recall用，另外一个公用
	private static TPoolExecutor commonPoolExecutor;
	private static TPoolExecutor recallPoolExecutor;
	// 线程池配置
	public static int tpoolRatioWait2Coom = 600; // 默认48，公共线程池和追话线程池使用同一个配置
	static {
		commonPoolExecutor = new TPoolExecutor(ThreadPool_Common);

	}

	/**
	 * 兼容GDC，使用公共线程池
	 * 
	 * @param task
	 */
	@SuppressWarnings("rawtypes")
	public static Future invoke(Callable task) {
		return commonPoolExecutor.invoke(task);
	}

	/**
	 * 兼容GDC，使用公共线程池
	 * 
	 * @param task
	 */
	@SuppressWarnings("rawtypes")
	public static ScheduledFuture invokeSchedule(Runnable command,
			long initialDelay, long period, TimeUnit unit) {
		return commonPoolExecutor.invoke(command, initialDelay, period, unit);
	}

	public static void execute(Runnable task) {
    log.info("Submit the runnable");
		commonPoolExecutor.execute(task);
	}

	

	/**
	 * 启动线程池中的所有线程
	 * 
	 * @param tasks
	 */
	public static List<Future<Integer>> invokeAllInCommonPool(
			Collection<Callable<Integer>> tasks) {
		return commonPoolExecutor.invokeAll(tasks);
	}

	/**
	 * 启动线程池中的所有线程
	 * 
	 * @param tasks
	 */
	public static List<Future<Integer>> invokeAllInRecallPool(
			Collection<Callable<Integer>> tasks) {
		return recallPoolExecutor.invokeAll(tasks);
	}

	/**
	 * 关闭公共连接池
	 * 
	 */
	public static void shutdownCommonPool() {
		commonPoolExecutor.shutdown();
	}

	/**
	 * 重建公共连接池
	 * 
	 */
	public static void resetCommonPool() {
		shutdownCommonPool();
		commonPoolExecutor = new TPoolExecutor(ThreadPool_Common);
	}

	public static ExecutorService getPool() {
		return commonPoolExecutor.getPool();
	}
}

/**
 * 
 * @ClassName: ExcecutorUtil.java
 * @Description: ExcecutorUtil.java
 * @author: maxiaochao
 * @date: 2012-9-25
 * @version: V04
 */
class TPoolExecutor {
	final static Logger log = Logger.getLogger(ExcecutorUtil.class);
	String name;
	private ThreadPoolExecutor pool;
	private ScheduledThreadPoolExecutor scheduledPool;

	public TPoolExecutor(String name) {
		this.name = name;
		pool = newFixedThreadPool();
		scheduledPool = (ScheduledThreadPoolExecutor) Executors
				.newScheduledThreadPool(Constants.SCHEDULEDPOOL_SIZE);
	}

	/**
	 * 返回当前线程池
	 * 
	 * @return: ExecutorService
	 * @return
	 */
	public ExecutorService getPool() {
		return pool;
	}

	/**
	 * 初始化固定的线程池
	 * 
	 * @return
	 */
	private ThreadPoolExecutor newFixedThreadPool() {
		int N_CPUS = Runtime.getRuntime().availableProcessors();// 向 Java
		// 虚拟机返回可用处理器的数目
		log.info("The server has " + N_CPUS + " cpu");
		double cpuUsagePercent = 0.5;
		int ratioWait2Com = ExcecutorUtil.tpoolRatioWait2Coom;
		double nThreads = N_CPUS * cpuUsagePercent * ratioWait2Com;
		log.info(name + " has " + nThreads + " threads");
		ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors
				.newFixedThreadPool((int) nThreads);// 共享的无界队列方式来运行这些线程
		pool.setKeepAliveTime(20, TimeUnit.SECONDS);
		return pool;
	}

	/**
	 * 关闭连接池
	 * 
	 */
	public void shutdown() {
		pool.shutdownNow();
		scheduledPool.shutdownNow();
	}

	/**
	 * 通过线程池执行一个线程任务
	 * 
	 * @param task
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Future invoke(Callable task) {
	
		return pool.submit(task);
	}

	/**
	 * 通过线程池执行一个线程任务
	 * 
	 * @param task
	 */
	public void execute(Runnable task) {

    log.info("Submit the runnable into the pool");
		pool.execute(task);
	}


	

	/**
	 * 通过线程池执行一个线程任务
	 * 
	 * @param task
	 */
	@SuppressWarnings("rawtypes")
	public ScheduledFuture invoke(Runnable command, long initialDelay,
			long period, TimeUnit unit) {
		if (pool == null) {
			return null;
		}
		log.info(name + ": living thread=" + pool.getActiveCount());
		return scheduledPool.scheduleAtFixedRate(command, initialDelay, period,
				unit);
	}

	/**
	 * 启动线程池中的所有线程
	 * 
	 * @param tasks
	 */
	public List<Future<Integer>> invokeAll(Collection<Callable<Integer>> tasks) {
		try {
			return pool.invokeAll(tasks);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	
}
