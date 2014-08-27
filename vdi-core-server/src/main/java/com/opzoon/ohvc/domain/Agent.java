package com.opzoon.ohvc.domain;

import org.json.JSONObject;

import com.opzoon.ohvc.common.anotation.Json;
import com.opzoon.vdi.core.domain.BaseDomain;

/**
 * Agent
 * 
 * @author maxiaochao
 * @version: V04
 * @since V04 2012-10-18
 */
@Json
public class Agent extends BaseDomain<Agent> {
	@Json(name = "head", type = JSONObject.class)
	private Head head;
	@Json(name = "body", type = JSONObject.class)
	private JobResult body;

	public Head getHead() {
		return head;
	}

	public void setHead(Head head) {
		this.head = head;
	}

	public JobResult getBody() {
		return body;
	}

	public void setBody(JobResult body) {
		this.body = body;
	}

}
