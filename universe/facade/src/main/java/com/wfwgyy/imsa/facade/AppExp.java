package com.wfwgyy.imsa.facade;

import org.json.JSONObject;

import com.wfwgyy.imsa.common.jedis.JedisEngine;
import com.wfwgyy.imsa.common.net.NioTcpClient;

public class AppExp {
	public void test() {
		JSONObject json = new JSONObject();
		json.put("reqType", "register");
		json.put("imsaMsg", "abc");
		NioTcpClient.addReq(json.toString());
		
		JSONObject j2 = new JSONObject();
		j2.put("reqType", "publish");
		j2.put("imsaMsg", "123");
		NioTcpClient.addReq(j2.toString());
		
		Thread ntc = new Thread(new NioTcpClient());
		ntc.start();

		JSONObject j3 = new JSONObject();
		j3.put("reqType", "publish");
		j3.put("imsaMsg", "def");
		NioTcpClient.addReq(j3.toString());
	}
}
