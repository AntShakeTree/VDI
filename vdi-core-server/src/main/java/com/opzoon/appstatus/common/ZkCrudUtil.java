/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.opzoon.appstatus.common;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

/**
 * @ClassName: Description: CrudExamples author david
 * @date 2013-7-26 下午7:57:30
 * @version V0.2.1023（迭代3）  
 */
public class ZkCrudUtil
{
	public static void create(CuratorFramework client, String path, byte[] payload) throws Exception
	{
		// this will create the given ZNode with the given data
		Stat stat = client.checkExists().forPath(path);
		if (stat == null)
		{
			client.create().forPath(path, payload);
		}
		else
		{
			try
			{
				client.setData().forPath(path, payload);
			}
			catch (KeeperException.NoNodeException e)
			{
				client.create().forPath(path, payload);
			}
		}
	}

	public static void createEphemeral(CuratorFramework client, String path, byte[] payload) throws Exception
	{
		// this will create the given EPHEMERAL ZNode with the given data
		Stat stat = client.checkExists().forPath(path);
		if (stat == null)
		{
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, payload);
		}

	}

	public static void createNotCreateParentEphemeral(CuratorFramework client, String path, byte[] payload) throws Exception
	{
		// this will create the given EPHEMERAL ZNode with the given data
		Stat stat = client.checkExists().forPath(path);
		if (stat == null)
		{
			client.create().withMode(CreateMode.EPHEMERAL).forPath(path, payload);
		}

	}

	public static String createEphemeralSequential(CuratorFramework client, String path, byte[] payload) throws Exception
	{
		// this will create the given EPHEMERAL-SEQUENTIAL ZNode with the given
		// data using Curator protection.
		/*
		 * Protection Mode: It turns out there is an edge case that exists when
		 * creating sequential-ephemeral nodes. The creation can succeed on the
		 * server, but the server can crash before the created node name is
		 * returned to the client. However, the ZK session is still valid so the
		 * ephemeral node is not deleted. Thus, there is no way for the client
		 * to determine what node was created for them. Even without
		 * sequential-ephemeral, however, the create can succeed on the sever
		 * but the client (for various reasons) will not know it. Putting the
		 * create builder into protection mode works around this. The name of
		 * the node that is created is prefixed with a GUID. If node creation
		 * fails the normal retry mechanism will occur. On the retry, the parent
		 * path is first searched for a node that has the GUID in it. If that
		 * node is found, it is assumed to be the lost node that was
		 * successfully created on the first try and is returned to the caller.
		 */
		return client.create().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, payload);
	}

	public static void setData(CuratorFramework client, String path, byte[] payload) throws Exception
	{
		// set data for the given node
		try
		{
			client.setData().forPath(path, payload);
		}
		catch (KeeperException.NoNodeException e)
		{
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, payload);
		}
	}

	public static void setDataAsyncWithCallback(CuratorFramework client, BackgroundCallback callback, String path, byte[] payload) throws Exception
	{
		// this is another method of getting notification of an async completion
		client.setData().inBackground(callback).forPath(path, payload);
	}

	public static void delete(CuratorFramework client, String path) throws Exception
	{
		// delete the given node
		Stat stat = client.checkExists().forPath(path);
		if (stat != null)
		{
			client.delete().forPath(path);
		}
	}
	
	public static boolean isExists(CuratorFramework client, String path) throws Exception
	{
		Stat stat = client.checkExists().forPath(path);
		return (stat != null);
	}

	public static void guaranteedDelete(CuratorFramework client, String path) throws Exception
	{
		// delete the given node and guarantee that it completes

		/*
		 * Guaranteed Delete Solves this edge case: deleting a node can fail due
		 * to connection issues. Further, if the node was ephemeral, the node
		 * will not get auto-deleted as the session is still valid. This can
		 * wreak havoc with lock implementations. When guaranteed is set,
		 * Curator will record failed node deletions and attempt to delete them
		 * in the background until successful. NOTE: you will still get an
		 * exception when the deletion fails. But, you can be assured that as
		 * long as the CuratorFramework instance is open attempts will be made
		 * to delete the node.
		 */
		Stat stat = client.checkExists().forPath(path);
		if (stat != null)
			client.delete().guaranteed().forPath(path);
	}

	public static List<String> watchedGetChildren(CuratorFramework client, String path) throws Exception
	{
		/**
		 * Get children and set a watcher on the node. The watcher notification
		 * will come through the CuratorListener (see setDataAsync() above).
		 */
		return client.getChildren().watched().forPath(path);
	}

	public static List<String> watchedGetChildren(CuratorFramework client, String path, Watcher watcher) throws Exception
	{
		/**
		 * Get children and set the given watcher on the node.
		 */
		return client.getChildren().usingWatcher(watcher).forPath(path);
	}

	/*
	 * @author zhengyi
	 */
	public static String getData(CuratorFramework client, String path) throws Exception {
		// get data from the given node
		return new String(client.getData().forPath(path));
	}
}
