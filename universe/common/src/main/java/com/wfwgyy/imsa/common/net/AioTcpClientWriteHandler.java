package com.wfwgyy.imsa.common.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class AioTcpClientWriteHandler implements CompletionHandler<Integer, ByteBuffer> {
	private AsynchronousSocketChannel clientChannel;  
    
    public AioTcpClientWriteHandler(AsynchronousSocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }
    
	@Override
	public void completed(Integer result, ByteBuffer buffer) {
		//完成全部数据的写入  
        if (buffer.hasRemaining()) {  
            clientChannel.write(buffer, buffer, this);  
        } else {
        	AioTcpClientThread.latch.countDown();
        	AioTcpClientThread.writeLatch.countDown();
        }
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		System.err.println("数据发送失败...");  
        try {  
            clientChannel.close();  
            AioTcpClientThread.latch.countDown();
        	AioTcpClientThread.writeLatch.countDown();
        } catch (IOException e) {  
        }
	}

}