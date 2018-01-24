package com.wfwgyy.imsa.common.msg;

import org.json.JSONArray;
import org.json.JSONObject;

import com.wfwgyy.imsa.common.AppConsts;
import com.wfwgyy.imsa.common.jedis.JedisEngine;

/**
 * 用于生成和解析系统消息，消息的描述信息在msg_data保存的Json字符串中，如果该消息中具有二进制数据，可以保存在msg_urls的链接中
 * @author 闫涛 2018.01.24
 *
 */
public class ImsaMsgEngine {
	public static String createMsg(int msgTypeId, int msgVersion, String req, String[] urls) {
        JSONObject msgObj = new JSONObject();
        long msgId = JedisEngine.getKeyUniqueId(AppConsts.MSG_ID);
        msgObj.put(AppConsts.MSG_ID, msgId);
        msgObj.put(AppConsts.MSG_TYPE, 102);
        msgObj.put(AppConsts.MSG_VERSION, 1);
        msgObj.put(AppConsts.MSG_DATA, req);
        JSONArray msgUrls = new JSONArray();
        if (urls != null) {
	        for (String url : urls) {
	        	msgUrls.put(url);
	        }
        }
        msgObj.put(AppConsts.MSG_URLS, msgUrls);
		return msgObj.toString();
	}
}
