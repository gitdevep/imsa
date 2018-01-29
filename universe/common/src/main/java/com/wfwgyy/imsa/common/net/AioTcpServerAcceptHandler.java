package com.wfwgyy.imsa.common.net;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AioTcpServerAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AioTcpServerThread> {
	public RequestProcessor requestProcessor = null;
	public AioTcpServerAcceptHandler(RequestProcessor requestProcessor) {
		this.requestProcessor = requestProcessor;
	}

	@Override
	public void completed(AsynchronousSocketChannel channel, AioTcpServerThread serverThread) {
		//继续接受其他客户端的请求  
        AioTcpServer.clientCount++;  
        System.out.println("连接的客户端数：" + AioTcpServer.clientCount);  
        serverThread.channel.accept(serverThread, this);  
        //创建新的Buffer  
        ByteBuffer buffer = ByteBuffer.allocate(1024);  
        //异步读  第三个参数为接收消息回调的业务Handler  
        channel.read(buffer, buffer, new AioTcpServerReadHandler(requestProcessor, channel));
	}

	@Override
	public void failed(Throwable exec, AioTcpServerThread serverThread) {
		exec.printStackTrace();  
        serverThread.latch.countDown();
	}

}
