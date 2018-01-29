package com.wfwgyy.imsa.common;

public class AppConsts {
	// 系统消息所需属性
	public final static String MSG_ID = "msg_id";
	public final static String MSG_TYPE = "msg_type";
	public final static String MSG_VERSION = "msg_version";
	public final static String MSG_DATA = "msg_data";
	public final static String MSG_URLS = "msg_urls";
	// 消息类型
	public final static int MT_HTTP_GET_REQ = 1; // HTTP GET请求
	// 消息版本
	public final static int MT_MSG_V1 = 1;
	public final static int MT_MSG_V2 = 2;
	public final static int MT_MSG_V3 = 3;
	public final static int MT_MSG_V4 = 4;
	public final static int MT_MSG_V5 = 5;
	// Redis服务器
	public final static String REDIS_HOST = "192.168.223.38";
	public final static short REDIS_PORT = 6379;
	// 消息服务器相关配置
	public final static String PLATO_HOST = "192.168.223.38";
	public final static short PLATO_PORT = 8089;
	// 门户Facade相关
	public final static short FACADE_PORT = 8088;
}
