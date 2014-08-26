/**   
 * Title: SendQueue.java 
 * Package com.opzoon.appstatus.queue 
 * Description:
 * @author david  
 * @Date 2013-7-17 下午5:28:46 
 * @version V1.0   
 */
package com.vdi.support.desktop.lls.manager.support;

import java.util.concurrent.ArrayBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vdi.common.ParseJSON;
import com.vdi.support.desktop.lls.domain.task.Task;
import com.vdi.support.desktop.lls.manager.LLSConnection;
import com.vdi.support.desktop.lls.manager.LLSExcutor;
import com.vdi.support.desktop.lls.services.TaskService;

/**
 * ClassName: SendQueue Description: 分发队列
 * 
 * @author maxiaochao
 */
@Component("llsSendQueue")
public class LLSQueryLLSQueue implements LLSExcutor {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(LLSQueryLLSQueue.class);
	// Queue<E>
	private final static ArrayBlockingQueue<Task> SENDQUEUE = new ArrayBlockingQueue<Task>(
			1000);
	private @Autowired TaskService taskService;
	private @Resource(name = "llsHandle") LLSConnection connection;
	private @Autowired TaskHandle taskHandle;
	public static ArrayBlockingQueue<?> getSendqueue() {
		return SENDQUEUE;
	}

	/**
	 * @throws InterruptedException
	 * @throws LLSException
	 * 
	 * @Title: send
	 * @Description: 发送
	 * @return void
	 * @throws
	 */
	@Scheduled(fixedDelay = 1000)
	public void sendSchdulTask() {
		while (SENDQUEUE.size() != 0) {
			LOGGER.info("sendSchdulTask:{}", SENDQUEUE.size());
			Task origintask = null;
			try {
				origintask = SENDQUEUE.take();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			String tid = origintask.getTaskIdentity();
			origintask = taskService.getTask(tid);
			origintask=ParseJSON.convertObjectToDomain(origintask.getContent(),Task.class);

//			Session.setCache(origintask.getTaskIdentity(), origintask);
			if (!origintask.isTaskFinished(origintask)) {
				try {
					origintask.setTaskIdentity(tid);
					SENDQUEUE.put(origintask);
				} catch (InterruptedException e) {
				}
			}else{
				taskHandle.handle(origintask);
			}
				
			
		}

	}



	@Override
	public void addTaskExcutorQueue(Task task) {
		LOGGER.info("addTaskExcutorQueue :{}", task.getTaskIdentity());
		try {
			SENDQUEUE.put(task);
		} catch (InterruptedException e) {
		}
	}

	@PostConstruct
	public void destroyHook() {
		Thread hook = new Thread(new Runnable() {

			@Override
			public void run() {
				connection.close();
			}
		});
		Runtime.getRuntime().addShutdownHook(hook);
	}
}
