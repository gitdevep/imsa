package com.wfwgyy.imsa.common.net;

public class AioTcpClient {
	private static AioTcpClientThread clientThread;
	
	public static synchronized void start(String host, short port) {
		System.out.println("客户端启动中......");
		if (null != clientThread) {
			return ;
		}
		clientThread = new AioTcpClientThread(host, port);
		Thread thread = new Thread(clientThread);
		thread.start();
	}
	
	public static boolean sendMsg(String msg) {
		if(msg.equals("q")) return false;  
        clientThread.sendMsg(msg);  
        return true;  
	}
}
