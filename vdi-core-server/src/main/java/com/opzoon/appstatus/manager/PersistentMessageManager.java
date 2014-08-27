package com.opzoon.appstatus.manager;

import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.ZKPaths;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opzoon.appstatus.common.AppStatusConstants;
import com.opzoon.appstatus.common.ParseJSON;
import com.opzoon.appstatus.domain.AppstatusMessage;
import com.opzoon.appstatus.domain.PersistentMessage;
import com.opzoon.appstatus.facade.AppstatusPersistentMessageMonitor;

@Component("persistentMessageManager")
public class PersistentMessageManager {
	
	private static Logger log = Logger.getLogger(PersistentMessageManager.class);
	
	private CuratorFramework client;
	
	private PathChildrenCache cache;
	@Autowired
	private AppstatusPersistentMessageMonitor appstatusPersistentMessageMonitor;
	
	public void start() throws Exception {
		cache = new PathChildrenCache(client, AppStatusConstants.MESSAGE_PATH, true);
		addMessageNodeListener();
		cache.start();
	}

	public void stop() throws IOException {
		if(null != cache) cache.close(); 
	}
	
	private void addMessageNodeListener() {
		log.info("<=AppStatus=> addMessageNodeListener");
		PathChildrenCacheListener plistener = new PathChildrenCacheListener() {

			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				ChildData childData = event.getData();
				if (null != childData) {
					String data = ZKPaths.getNodeFromPath(childData.getPath());
					String nodeContent =new String(childData.getData());
					switch (event.getType()) {
						case CHILD_ADDED : {
							String jsonData = getMessageNodeData(data);
							log.info("<=AppStatus=> addMessageNodeListener  Node added: " + jsonData);
							
							AppstatusMessage message = ParseJSON.parseAppstatusMessage(nodeContent);
							switch (message.getMessageType()) {
								case PersistenMessage :
									PersistentMessage pm = ParseJSON.fromJson(jsonData, PersistentMessage.class);
									appstatusPersistentMessageMonitor.monitor(pm);
									break;
								default :
									break;
							}
							break;
						}

						case CHILD_UPDATED : {
//							String message = new String(childData.getData());
//							log.info("<=AppStatus=> addNodeStateListener  Node changed: " + data + "; TaskMessage: " + message); // TaskMessage{}
							String jsonData = getMessageNodeData(data);
							log.info("<=AppStatus=> addMessageNodeListener  Node update: " + jsonData);
							AppstatusMessage message = ParseJSON.parseAppstatusMessage(jsonData);
							switch (message.getMessageType()) {
								case PersistenMessage :
									PersistentMessage pm = ParseJSON.fromJson(nodeContent, PersistentMessage.class);
									appstatusPersistentMessageMonitor.monitor(pm);
									break;
								default :
									break;
							}
							break;
						}

						case CHILD_REMOVED : {
							String jsonData = getMessageNodeData(data);
							log.info("<=AppStatus=> addMessageNodeListener  Node remove: " + jsonData);
							AppstatusMessage message = ParseJSON.parseAppstatusMessage(jsonData);
							switch (message.getMessageType()) {
								case PersistenMessage :
									PersistentMessage pm = ParseJSON.fromJson(jsonData, PersistentMessage.class);
									appstatusPersistentMessageMonitor.monitor(pm);
									break;
								default :
									break;
							}
							break;
						}

						default :
							break;
					}
				}
			}
		};
		cache.getListenable().addListener(plistener);
	}
	
	public void createMessageNode(String name, String data) {
		NodeManager.createNode(client, AppStatusConstants.MESSAGE_PATH, name, data);
	}
	
	public String getMessageNodeData(String name) {
		return NodeManager.getNodeData(client, AppStatusConstants.MESSAGE_PATH, name);
	}
	
	public void removeMessageNode(String name) {
		NodeManager.removeNode(client, AppStatusConstants.MESSAGE_PATH, name);
	}
	
	public void saveOrUpdateMessageNode(String name, String data) {
		NodeManager.saveOrUpdateNode(client, AppStatusConstants.MESSAGE_PATH, name, data);
	}

	public PersistentMessageManager setClient(CuratorFramework client) {
		this.client = client;
		return this;
	}
	
}
