package com.opzoon.vdi.core.controller;

import com.opzoon.appstatus.domain.ClusterState;

public interface ClusterOpertator {

	public void onClusterChange(ClusterState clusterStatus);
}
