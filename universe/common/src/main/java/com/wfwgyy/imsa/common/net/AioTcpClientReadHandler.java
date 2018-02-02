package com.wfwgyy.imsa.common.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

public class AioTcpClientReadHandler implements CompletionHandler<Integer, ByteBuffer> {
	private CountDownLatch readLatch;
	private AsynchronousSocketChannel clientChannel;
    
    public AioTcpClientReadHandler(AsynchronousSocketChannel clientChannel, CountDownLatch readLatch) {  
        this.clientChannel = clientChannel;
        this.readLatch = readLatch;
    }
    
	@Override
	public void completed(Integer result, ByteBuffer buffer) {
		buffer.flip();  
        byte[] bytes = new byte[buffer.remaining()];  
        buffer.get(bytes);  
        String body;
        Queue<String> responseQueue = AioTcpClient.responseQueue;
        try {  
            body = new String(bytes,"UTF-8");  
            System.out.println("@@@@@客户端收到结果:"+ body);
            responseQueue.add(body);
        } catch (UnsupportedEncodingException e) {  
        	System.out.println("AioTcpClientReadHandler.completed 4");
            e.printStackTrace();  
        }
        readLatch.countDown();
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		System.err.println("数据读取失败...");  
        try {  
            clientChannel.close();
            readLatch.countDown();
        } catch (IOException e) {  
        }
	}

}
