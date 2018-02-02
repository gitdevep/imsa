package com.wfwgyy.imsa.common.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AioTcpServerReadHandler implements CompletionHandler<Integer, ByteBuffer> {
	public RequestProcessor requestProcessor = null;
	private AsynchronousSocketChannel channel;
	public AioTcpServerReadHandler(RequestProcessor requestProcessor, AsynchronousSocketChannel channel) {
		this.requestProcessor = requestProcessor;
		this.channel = channel;
	}

	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		//flip操作  
        attachment.flip();  
        //根据  
        byte[] message = new byte[attachment.remaining()];  
        attachment.get(message);
        byte[] rawResp = requestProcessor.processRequest(channel, message);
        if (rawResp != null) {
        	doWrite(rawResp);
        }
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		try {  
            this.channel.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
	}
	
	//发送消息  
    private void doWrite(byte[] bytes) {
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);  
        writeBuffer.put(bytes);  
        writeBuffer.flip(); 
        channel.write(writeBuffer, writeBuffer, new AioTcpServerWriteHandler(channel, requestProcessor, bytes));
    }  

}
