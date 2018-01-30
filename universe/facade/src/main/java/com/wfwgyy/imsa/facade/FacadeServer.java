package com.wfwgyy.imsa.facade;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.wfwgyy.imsa.common.AppConsts;
import com.wfwgyy.imsa.common.Turple2;
import com.wfwgyy.imsa.common.jedis.JedisEngine;
import com.wfwgyy.imsa.common.msg.ImsaMsgEngine;
import com.wfwgyy.imsa.common.net.AioTcpServer;
import com.wfwgyy.imsa.common.net.AioTcpServerThread;

/**
 * 整个系统入口，将监听所有外部系统发送的请求，将请求转化为系统消息，并发布到消息总线上去。同时接收消息总线消息，如果是HTML消息
 * 发送消息，则将该消息发送给调用者。
 * @author 闫涛 2018.01.09
 *
 */
public class FacadeServer extends AioTcpServer {
	private static AioTcpServerThread aioTcpServerThread = null;
	public volatile static long clientCount = 0;
	
	public static synchronized void start() {
		System.out.println("门户服务器Facade启动中...IMSA");
		if (null != aioTcpServerThread) {
			return ;
		}
		AioTcpServer server = new FacadeServer();
		Thread thread = new Thread(new AioTcpServerThread(server::processRequest, AppConsts.FACADE_PORT));
		thread.start();
	}

	@Override
	public byte[] processRequest(byte[] req) {
		// TODO Auto-generated method stub
		String expression = "";
		try {
			expression = new String(req, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
        System.out.println("服务器收到消息: " + expression); 
        String calrResult = null;
        try{  
            calrResult = prepareResponse();
        }catch(Exception e){  
            calrResult = "计算错误：" + e.getMessage();  
        }  
		return calrResult.getBytes();
	}
	
	public String prepareResponse() {
        String hello = "<html><head><meta charset=\"utf-8\" /></head><body>IMSA v0.0.1...微服务工业云门户服务器Facade<br />测试读入内容是否正确<br />Hello World!</body></html>";   
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
