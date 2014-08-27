package com.opzoon.appstatus.domain;

/**
 * ClassName: ServiceType Description: EMPTY Single 单机模式主机开机 Single EMPTY
 * 单机模式主机关闭/失效 EMPTY Down （E）开机阶段，失效主机重新加入集群（隐含不符合a>m条件） Down EMPTY
 * （A）少数派存活时，正常主机全部关闭/失效 （B）少数派存活时，删除全部正常主机 Down Down （A）少数派存活时，正常主机关闭/失效
 * （B）少数派存活时，删除正常主机（隐含不符合a>m条件） （C）少数派存活时，删除失效主机，不符合a>m条件
 * （D）少数派存活时，加入新主机，不符合a>m条件 （E）少数派存活时，失效主机重新加入集群，不符合a>m条件 Down Running
 * （C）少数派存活时，删除失效主机，符合a>m条件 （D）少数派存活时，加入新主机，符合a>m条件 （E）少数派存活时，失效主机重新加入集群，符合a>m条件
 * Running EMPTY （A）多数派存活时，正常主机全部关闭/失效 （B）多数派存活时，删除全部正常主机 Running Down
 * （A）多数派存活时，正常主机关闭/失效，不符合a>m条件 （B）多数派存活时，删除正常主机，不符合a>m条件 Running Running
 * （A）多数派存活时，正常主机关闭/失效，符合a>m条件 （B）多数派存活时，删除正常主机，符合a>m条件
 * （C）多数派存活时，删除失效主机（隐含符合a>m条件） （D）多数派存活时，加入新主机（隐含符合a>m条件）
 * （E）多数派存活时，失效主机重新加入集群（隐含符合a>m条件）
 * 
 * @author David
 * @date 2013-7-17 下午5:38:00
 */
public enum ClusterState
{
	EMPTY, SINGLE, DOWN, CLUSTER;
}
