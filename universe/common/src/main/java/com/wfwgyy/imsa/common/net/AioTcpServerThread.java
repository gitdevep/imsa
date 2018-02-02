package com.wfwgyy.imsa.common.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class AioTcpServerThread implements Runnable {
	public RequestProcessor requestProcessor = null;
	public static CountDownLatch latch; // 并发计数器
	public AsynchronousServerSocketChannel channel;
	protected AioTcpServerAcceptHandler acceptHandler = null;
	
	public AioTcpServerThread(RequestProcessor requestProcessor, short port) {
		this.requestProcessor = requestProcessor;
		try {
			channel = AsynchronousServerSocketChannel.open();
			channel.bind(new InetSocketAddress(port));
			System.out.println("打开并监听端口：" + port + "!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		//CountDownLatch初始化  
        //它的作用：在完成一组正在执行的操作之前，允许当前的现场一直阻塞  
        //此处，让现场在此阻塞，防止服务端执行完成后退出  
        //也可以使用while(true)+sleep   
        //生成环境就不需要担心这个问题，以为服务端是不会退出的  
		//while (true) {
        latch = new CountDownLatch(1);  
        //用于接收客户端的连接  
        channel.accept(this,new AioTcpServerAcceptHandler(requestProcessor));  
        try {  
            latch.await();  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }
		//}
	}
}
