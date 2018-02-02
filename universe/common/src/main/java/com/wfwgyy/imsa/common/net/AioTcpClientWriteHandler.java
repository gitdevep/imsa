package com.wfwgyy.imsa.common.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class AioTcpClientWriteHandler implements CompletionHandler<Integer, ByteBuffer> {
	private CountDownLatch writeLatch;
	private AsynchronousSocketChannel clientChannel;  
    
    public AioTcpClientWriteHandler(AsynchronousSocketChannel clientChannel, CountDownLatch writeLatch) {
        this.clientChannel = clientChannel;
        this.writeLatch = writeLatch;
    }
    
	@Override
	public void completed(Integer result, ByteBuffer buffer) {
		//完成全部数据的写入  
        if (buffer.hasRemaining()) {  
            clientChannel.write(buffer, buffer, this);  
        }
        writeLatch.countDown();
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		System.err.println("数据发送失败...");  
        try {  
            clientChannel.close();
        	writeLatch.countDown();
        } catch (IOException e) {  
        }
	}

}