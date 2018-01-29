package com.wfwgyy.imsa.plato;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import org.json.JSONObject;

import com.wfwgyy.imsa.common.AppConsts;
import com.wfwgyy.imsa.common.Turple2;
import com.wfwgyy.imsa.common.msg.ImsaMsgEngine;
import com.wfwgyy.imsa.common.net.NioTcpServer;

public class PlatoServer extends NioTcpServer {
	/**
	 * 程序总入口，启动Imsa服务器
	 * @throws Exception
	 */
	public void start(short port) throws Exception {
		super.start(port);
    }
	
	/**
	 * 接受客户端的连接请求
	 * @param key
	 * @param selector
	 */
	protected void acceptConnection(SelectionKey key, Selector selector) {
		super.acceptConnection(key, selector);
	}
	
	/**
	 * 读取消息内容，并向消息总线plato发送消息
	 * @param key
	 * @param selector
	 */
	protected void processRequest(SelectionKey key, Selector selector) {
		Turple2<String, String[]> reqObj = super.readRequest(key, selector);
		String msgStr = ImsaMsgEngine.createMsg(AppConsts.MT_HTTP_GET_REQ, AppConsts.MT_MSG_V1, reqObj.v1, null);
		System.out.println("" + System.currentTimeMillis() + ":" + msgStr + "!");
		// 发送消息到消息总线
	}
	
	/**
	 * 从消息总线接收到需要发送的HTTP响应，将响应发送给客户端
	 * @param key
	 * @param resp
	 */
	protected void processResponse(SelectionKey key, Selector selector) {
		String resp = prepareTestResponse();
		super.sendResponse(key, selector, resp);
	}
    
    
    
    
    
    
    
    
    
    
    
    /**
     * 临时方法，产生向客户端发送的响应
     * @return
     */
    private String prepareTestResponse() {
        String hello = "<html><head><meta charset=\"utf-8\" /></head><body>IMSA v0.0.1...微服务工业云（测试版本）<br />测试读入内容是否正确<br />Hello World!</body></html>";   
        StringBuilder resp = new StringBuilder();
        resp.append("HTTP/1.1 200 OK" + "\r\n");
        resp.append("Server: Microsoft-IIS/5.0 " + "\r\n");
        resp.append("Date: Thu,08 Mar 200707:17:51 GMT" + "\r\n");
        resp.append("Connection: Keep-Alive" + "\r\n");
        resp.append("Content-Length: " + hello.getBytes().length + "\r\n");
        resp.append("Content-Type: text/html\r\n");
        resp.append("\r\n" + hello);
        return resp.toString();
    }
}
