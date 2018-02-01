package com.wfwgyy.imsa.common.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

public class AioTcpClientReadHandler implements CompletionHandler<Integer, ByteBuffer> {
	private AsynchronousSocketChannel clientChannel;
    
    public AioTcpClientReadHandler(AsynchronousSocketChannel clientChannel) {  
        this.clientChannel = clientChannel;
    }
    
	@Override
	public void completed(Integer result, ByteBuffer buffer) {
		System.out.println("AioTcpClientReadHandler.completed 1");
		buffer.flip();  
        byte[] bytes = new byte[buffer.remaining()];  
        buffer.get(bytes);  
        String body;  
        System.out.println("AioTcpClientReadHandler.completed 2");
        Queue<String> responseQueue = AioTcpClientThread.getResponseQueue();
        try {  
            body = new String(bytes,"UTF-8");  
            System.out.println("@@@@@客户端收到结果:"+ body);
            responseQueue.add(body);
        } catch (UnsupportedEncodingException e) {  
        	System.out.println("AioTcpClientReadHandler.completed 4");
            e.printStackTrace();  
        }
        System.out.println("AioTcpClientReadHandler.completed 5");
        AioTcpClientThread.latch.countDown();
        System.out.println("AioTcpClientReadHandler.completed 6:latch=" + AioTcpClientThread.latch.getCount() + "!");
        AioTcpClientThread.readLatch.countDown();
        System.out.println("AioTcpClientReadHandler.completed 7");
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		System.err.println("数据读取失败...");  
        try {  
            clientChannel.close();  
            AioTcpClientThread.latch.countDown();
            AioTcpClientThread.readLatch.countDown();
        } catch (IOException e) {  
        }
	}

}
