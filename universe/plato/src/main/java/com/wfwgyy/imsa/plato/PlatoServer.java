package com.wfwgyy.imsa.plato;

import java.io.UnsupportedEncodingException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import org.json.JSONObject;

import com.wfwgyy.imsa.common.AppConsts;
import com.wfwgyy.imsa.common.Turple2;
import com.wfwgyy.imsa.common.msg.ImsaMsgEngine;
import com.wfwgyy.imsa.common.net.AioTcpServer;
import com.wfwgyy.imsa.common.net.AioTcpServerThread;
import com.wfwgyy.imsa.common.net.NioTcpServer;

public class PlatoServer extends AioTcpServer {
	private static AioTcpServerThread aioTcpServerThread = null;
	public volatile static long clientCount = 0;
	
	public static synchronized void start() {
		System.out.println("消息服务器启动中...IMSA");
		if (null != aioTcpServerThread) {
			return ;
		}
		AioTcpServer server = new PlatoServer();
		Thread thread = new Thread(new AioTcpServerThread(server::processRequest, AppConsts.PLATO_PORT));
		thread.start();
	}

	@Override
	public byte[] processRequest(AsynchronousSocketChannel channel, byte[] req) {
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
        String hello = "<html><head><meta charset=\"utf-8\" /></head><body>IMSA v0.0.1...微服务工业云消息总线Plato（测试版本）<br />测试读入内容是否正确<br />Hello World!</body></html>";   
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
