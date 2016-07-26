package com.dachen.imsdk.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * 指令列表
 * 
 * @author lmc
 *
 */
public class EventPL implements Serializable {
	private static final long serialVersionUID = 1076683453047838505L;
	/**
	 * 
	 */
	public String eventType; // 事件类型 1 2 3 ...
	public String id; // ID
	public long ts; // 时间
//	public String userId; // 发送给谁的指令
	public Map<String, String> param; // 扩展map

	@Override
	public String toString() {
		return "EventPL{" +
				"eventType='" + eventType + '\'' +
				", id='" + id + '\'' +
				", ts=" + ts +
				", param=" + param +
				'}';
	}
}
