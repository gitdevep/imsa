package com.wfwgyy.imsa.facade;

import com.wfwgyy.imsa.common.jedis.JedisEngine;

public class AppExp {
	public void test() {
		redisCreateId();
	}
	
	/**
	 * 在redis上找到对应key的ID值，作为消息唯一标识，然后将ID的值递增1存回
	 */
	private void redisCreateId() {
		String key = "msg_id";
		long msgId = JedisEngine.getKeyUniqueId(key);
		System.out.println("msgId=" + msgId + "!");
		msgId = JedisEngine.getKeyUniqueId(key);
		System.out.println("msgId2=" + msgId + "!");
		for (int i=1; i<=10; i++) {
			msgId = JedisEngine.getKeyUniqueId(key);
			System.out.println("round" + i + ": msgId=" + msgId + "!");
		}
	}
}
