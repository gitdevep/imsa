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
	private AsynchronousSocketChannel clientChannel;  
    private String host;  
    private int port;  
    public static CountDownLatch latch; 
    public static CountDownLatch writeLatch;
    public static CountDownLatch readLatch;
    private static Queue<String> requestQueue = new ConcurrentLinkedQueue();
    private static Queue<String> responseQueue = new ConcurrentLinkedQueue();
    
    public AioTcpClientThread(String host, short port) {
    	this.host = host;  
        this.port = port;  
        try {  
            //创建异步的客户端通道  
            clientChannel = AsynchronousSocketChannel.open();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
    }
    
    public static Queue<String> getResponseQueue() {
    	return responseQueue;
    }
    
    public static void addRequestToQueue(String msg) {
    	requestQueue.add(msg);
    	latch.countDown();
    }

	@Override
	public void run() { 
        //发起异步连接操作，回调参数就是这个类本身，如果连接成功会回调completed方法  
        clientChannel.connect(new InetSocketAddress(host, port), this, this);
        //thd.start();
        while (true) {
    		//创建CountDownLatch等待
            latch = new CountDownLatch(1);
            System.out.println("AioTcpClientThread.run 0:" + System.currentTimeMillis() + "!");
	        try {
	            latch.await();
	        } catch (InterruptedException e1) {
	            e1.printStackTrace();  
	        }
	        System.out.println("AioTcpClientThread.run 1:" + System.currentTimeMillis() + "!");
	        sendRequestQueueMsg();
	        readResponseQueueMsg();
        }
        /*try {  
            clientChannel.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }*/
	}

	@Override
	public void completed(Void result, AioTcpClientThread attachment) {
		System.out.println("客户端成功连接到服务器...");
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
	
	public void sendRequestQueueMsg() {
		String msg = null;
		while ((msg = requestQueue.poll()) != null) {
			writeLatch = new CountDownLatch(1);
			sendMsg(msg);
			try {
				writeLatch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void readResponseQueueMsg() {
		System.out.println("readResponse " + System.currentTimeMillis() + "!");
		readLatch = new CountDownLatch(1);
		ByteBuffer readBuffer = ByteBuffer.allocate(1024);  
        clientChannel.read(readBuffer,readBuffer,new AioTcpClientReadHandler(clientChannel));
        System.out.println("rrqm1");
		try {
			readLatch.await(10, TimeUnit.MICROSECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("rrqm2");
	}
	
	public void sendMsg(String msg){
        byte[] req = msg.getBytes();  
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);  
        writeBuffer.put(req);  
        writeBuffer.flip();
        //异步写  
        clientChannel.write(writeBuffer, writeBuffer,new AioTcpClientWriteHandler(clientChannel)); 
    }

}
