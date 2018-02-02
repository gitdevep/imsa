package com.wfwgyy.imsa.common.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AioTcpClientThread implements CompletionHandler<Void, AioTcpClientThread>, Runnable {
	public AsynchronousSocketChannel clientChannel;  
    private String host;  
    private int port;  
    public static CountDownLatch latch; 
    public static CountDownLatch writeLatch;
    public static CountDownLatch readLatch;
    
    public AioTcpClientThread(String host, int port) {
    	this.host = host;  
        this.port = port;  
        try {  
            //创建异步的客户端通道  
            clientChannel = AsynchronousSocketChannel.open();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
    }

	@Override
	public void run() { 
        //发起异步连接操作，回调参数就是这个类本身，如果连接成功会回调completed方法  
        clientChannel.connect(new InetSocketAddress(host, port), this, this);
        latch = new CountDownLatch(1);
        try {
            latch.await();
        } catch (InterruptedException e1) {
            e1.printStackTrace();  
        }
        try {  
            clientChannel.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
	}

	@Override
	public void completed(Void result, AioTcpClientThread attachment) {
		AioTcpClient.channels.put(host + ":" + port, clientChannel);
		System.out.println("客户端成功连接到服务器...");
		Thread writeThread = new Thread(new AioTcpClientWriteThread());
		writeThread.start();
		Thread readThread = new Thread(new AioTcpClientReadThread());
		readThread.start();
	}

	@Override
	public void failed(Throwable exc, AioTcpClientThread attachment) {
		System.err.println("连接服务器失败...");  
        exc.printStackTrace();  
        try {  
            clientChannel.close();  
            latch.countDown();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
	}
	
	public void readResponseQueueMsg() {
	}

}
