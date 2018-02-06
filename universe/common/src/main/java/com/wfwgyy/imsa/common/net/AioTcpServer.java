package com.wfwgyy.imsa.common.net;

import java.io.UnsupportedEncodingException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.wfwgyy.imsa.common.AppConsts;
import com.wfwgyy.imsa.common.Turple2;

public class AioTcpServer implements RequestProcessor {
	private static AioTcpServerThread aioTcpServerThread = null;
	public volatile static long clientCount = 0;
	public static Map<Long, AsynchronousSocketChannel> clients = new ConcurrentHashMap<>(); // 每个消息对应的外部系统的连接
	public static Queue<Turple2<AsynchronousSocketChannel, String>> responseQueue = new ConcurrentLinkedQueue<>();
	
	protected AioTcpServer() {
	}
	
	public static AioTcpServer getTestInstance() {
		return new AioTcpServer();
	}
	
	public static synchronized void start() {
		System.out.println("启动中...");
		if (null != aioTcpServerThread) {
			return ;
		}
		AioTcpServer ats = new AioTcpServer();
		Thread thread = new Thread(new AioTcpServerThread(ats::processRequest, AppConsts.PLATO_PORT));
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
        String hello = "<html><head><meta charset=\"utf-8\" /></head><body>IMSA v0.0.3...微服务工业云（测试版本）<br />测试读入内容是否正确<br />Hello World!</body></html>";   
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

	
	/**
	 * 返回新生成消息msgId，全局唯一标识
	 * @param request
	 * @param urls
	 * @return
	 */
	public long publishImsaMsg(String request, String[] urls) {
		return 0;
	}
	
	public Optional<String> getFullImsaRequest(StringBuilder requestBuffer) {
		return Optional.ofNullable("");
	}
	
	protected Optional<String> getHttpGetRequest(StringBuilder requestBuffer) {
		return Optional.ofNullable("");
	}
	
	/**
	 * 获取请求类型，目前一共有三种类型：MT_HTTP_GET_REQ, MT_HTTP_POST_REQ, MT_IMSA_MSG
	 * @param requestBuffer
	 * @return
	 */
	public int getImsaRequestType(StringBuilder requestBuffer) {
		int imsaStartPos = requestBuffer.indexOf(AppConsts.MSG_BEGIN_TAG);
		if (imsaStartPos < 0) {
			imsaStartPos = 0;
		}
		int httpGetStartPos = requestBuffer.indexOf(AppConsts.MSG_HTTP_GET_BEGINE);
		if (httpGetStartPos < 0) {
			httpGetStartPos = 0;
		}
		int httpPostStartPos = requestBuffer.indexOf(AppConsts.MSG_HTTP_POST_BEGINE);
		if (httpPostStartPos < 0) {
			httpPostStartPos = 0;
		}
		if (0== imsaStartPos && 0==httpGetStartPos && 0==httpPostStartPos) {
			return AppConsts.MT_NONE;
		}
		if (0 == imsaStartPos) {
			imsaStartPos = Integer.MAX_VALUE;
		}
		if (0 == httpGetStartPos) {
			httpGetStartPos = Integer.MAX_VALUE;
		}
		if (0 == httpPostStartPos) {
			httpPostStartPos = Integer.MAX_VALUE;
		}
		if (imsaStartPos < httpGetStartPos && imsaStartPos < httpPostStartPos) {
			return AppConsts.MT_IMSA_MSG;
		}
		if (httpGetStartPos < imsaStartPos && httpGetStartPos < httpPostStartPos) {
			return AppConsts.MT_HTTP_GET_REQ;
		}
		if (httpPostStartPos < imsaStartPos && httpPostStartPos < httpGetStartPos) {
			return AppConsts.MT_HTTP_POST_REQ;
		}
		return 0;
	}

}
