/**   
 * @Title: ParseJSON.java 
 * @Package com.opzoon.appstatus.common 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Nathan   
 * @date 2013-7-19 下午4:46:10 
 * @version V1.0   
 */
package com.opzoon.appstatus.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opzoon.appstatus.domain.AppstatusMessage;
import com.opzoon.appstatus.domain.ClusterConfigure;
import com.opzoon.appstatus.domain.MessageType;
import com.opzoon.appstatus.domain.Node;
import com.opzoon.appstatus.domain.TaskMessage;

/**
 * @ClassName: ParseJSON
 * @Description: 解析 josn工具类
 * @author maxiaochao
 * @date 2013-7-19 下午4:46:10
 * @version V0.2.1023（迭代3）  
 */
public class ParseJSON {
	private static Gson gson = new Gson();
	/**
	 * Title: parseClusterConfigure Description: parse ClusterConfigure
	 * 
	 * @param config
	 *            string
	 * @return List<ParseJSON>
	 * @throws
	 */
	private static Logger log = Logger.getLogger(ParseJSON.class);

	public static List<ClusterConfigure> parseClusterConfigures(String config) {
		log.debug("<=AppStatus=> parseClusterConfigures" + config);
		return gson.fromJson(config, new TypeToken<List<ClusterConfigure>>() {
		}.getType());
	}

	/**
	 * Title: toClusterConfigures Description: 由节点生成配置
	 * 
	 * @param node
	 * @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static void toClusterConfigures(Collection<Node> nodes) {
		List<ClusterConfigure> pers = new ArrayList<ClusterConfigure>();
		for (Node node : nodes) {
			ClusterConfigure clusterConfigure = new ClusterConfigure();
			clusterConfigure.setNodeAddress(node.getNodeAddress());
			clusterConfigure.setServiceType(node.getServiceType());
			clusterConfigure.setMyid(node.getMyid());
			pers.add(clusterConfigure);
		}
		for (Node node : nodes) {
			node.setClusterConfigure(new Gson().toJson(pers));
		}
		log.debug("<=AppStatus=> toClusterConfigures" + nodes);
	}

	public static String toTaskMessage(TaskMessage taskMessage) {
		String json = gson.toJson(taskMessage);
		log.debug("<=AppStatus=> toClusterConfigures" + json);
		return json;
	}

	public static TaskMessage parseTaskMessage(String taskMessage) {
		return gson.fromJson(taskMessage, TaskMessage.class);
	}

	public static AppstatusMessage parseAppstatusMessage(String json) {
		return gson.fromJson(json, AppstatusMessage.class);

	}

	public static Gson getGSON() {
		return gson;
	}
	/**
	 * 生成 josn 字符串
	 * @param src
	 * @return
	 */
	public static String toJson(Object src) {
		return gson.toJson(src);
	}
	public static <T> T fromJson(String json,Class<T> classOfT) {
		return gson.fromJson(json, classOfT);
	}

}
