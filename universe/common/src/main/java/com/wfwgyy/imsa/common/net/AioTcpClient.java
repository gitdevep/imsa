package com.wfwgyy.imsa.common.net;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.wfwgyy.imsa.common.Turple2;

public class AioTcpClient {
	private static AioTcpClientThread clientThread;
    public static Queue<Turple2<AsynchronousSocketChannel, String>> requestQueue = new ConcurrentLinkedQueue();
    public static Queue<String> responseQueue = new ConcurrentLinkedQueue();
    public static Map<String, AsynchronousSocketChannel> channels = new ConcurrentHashMap<>();
	
	public static synchronized void start(String host, int port) {
		System.out.println("客户端启动中......");
		if (null != clientThread) {
			return ;
		}
		clientThread = new AioTcpClientThread(host, port);
		Thread thread = new Thread(clientThread);
		thread.start();
	}
	
	public static void sendMsg(String host, int port, String msg) {
		requestQueue.add(new Turple2<>(channels.get(host + ":" + port), msg));
	}
}
