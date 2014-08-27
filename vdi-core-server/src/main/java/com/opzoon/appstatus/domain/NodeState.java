/**   
 * @Title: NodeState.java 
 * Package com.opzoon.appstatus.domain 
 * Description: TODO(用一句话描述该文件做什么) 
 * @author David   
 * @date 2013-9-10 上午10:19:09 
 * @version V1.0   
 */
package com.opzoon.appstatus.domain;

import org.apache.curator.framework.CuratorFrameworkFactory;

/**
 * ClassName: NodeState Description: TODO(这里用一句话描述这个类的作用)
 * 
 * @author david
 * @date 2013-9-10 上午10:19:09
 * 
 */
public enum NodeState {

	/**
	 * Sent for the first successful connection to the server. NOTE: You will
	 * only get one of these messages for any CuratorFramework instance.
	 */
	RUNNING,
	/**
	 * The connection is confirmed to be lost. Close any locks, leaders, etc.
	 * and attempt to re-create them. NOTE: it is possible to get a
	 * {@link #RECONNECTED} state after this but you should still consider any
	 * locks, etc. as dirty/unstable
	 */
	LOST,

	/**
	 * The connection has gone into read-only mode. This can only happen if you
	 * pass true for {@link CuratorFrameworkFactory.Builder#canBeReadOnly()}.
	 * See the ZooKeeper doc regarding read only connections: <a
	 * href="http://wiki.apache.org/hadoop/ZooKeeper/GSoCReadOnlyMode"
	 * >http://wiki.apache.org/hadoop/ZooKeeper/GSoCReadOnlyMode</a>. The
	 * connection will remain in read only mode until another state change is
	 * sent.
	 */
	READY,
	/**
	 * node state is error zkShell error start or close client failure.
	 */
	ERROR;
	private NodeState() {
		this.value = this.ordinal();
	}

	private NodeState(int value, String message) {
		this.value = value;
		this.message = message;
	}

	private int value;
	private String message;

	public int getValue() {
		return value;
	}

	public NodeState setValue(int value) {
		this.value = value;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public NodeState setMessage(String message) {
		this.message = message;
		return this;
	}

	// TODO :继承 error
	public static int VDICORE_APPSTATUE_ZK_START_ERROR = 501;
	public static int VDICORE_APPSTATUE_ZK_STOPPED_ERROR = 502;
	public static int VDICORE_APPSTATUE_ZK_NETWORK_ERROR = 503;
	public static int VDICORE_APPSTATUE_CLIENT_START_ERROR = 504;
	public static int VDICORE_APPSTATUS_DEFAULT_INIT_ERROR = 505;
	public static int VDICORE_APPSTATUS_UNKOWN_ERROR = 506;

	public static NodeState parseNodestate(int value) {
		/*for (NodeState nodestate : NodeState.values()) {

			if (nodestate.value == value) {
				return nodestate;
			} else {
				ERROR.setValue(0x01);
				return ERROR;
			}
		}
		return null;*/
		for (NodeState nodestate : NodeState.values()) {
			if (nodestate.value == value) {
				return nodestate;
			}
		}
		ERROR.setValue(VDICORE_APPSTATUS_UNKOWN_ERROR);
		return ERROR;
	}
}
