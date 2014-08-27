/**   
 * Title: TaskMessage.java 
 * Package com.opzoon.appstatus.domain 
 * @Description: TaskMessage.java 
 * @author David(mxc)   
 * @date 2013-7-30 下午3:42:13 
 * @version V3.0(迭代3)   
 */
package com.opzoon.appstatus.domain;

/**
 * ClassName: TaskMessage Description: TaskMessage
 * 
 * @author David
 * @date 2013-7-30 下午3:42:13
 */
public class TaskMessage extends AppstatusMessage {

	private TaskState messageState;

	@Override
	public void setMessageType(MessageType messageType) {
		// TODO Auto-generated method stub
		super.setMessageType(MessageType.TaskMesage);
	}

	// 外部调用
	private String content;

	/**
	 * @return messageState
	 */
	public TaskState getMessageState() {
		return messageState;
	}

	/**
	 * @param messageState
	 *            the messageState to set
	 */
	public void setMessageState(TaskState messageState) {
		this.messageState = messageState;
	}

	/**
	 * @return content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

}
