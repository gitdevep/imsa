package com.wfwgyy.imsa.common.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AioTcpServerWriteHandler implements CompletionHandler<Integer, ByteBuffer> {
	private RequestProcessor requestProcessor = null;
	private AsynchronousSocketChannel channel;
	private byte[] req = null;
	
	public AioTcpServerWriteHandler(AsynchronousSocketChannel channel, RequestProcessor requestProcessor, byte[] req) {
		this.channel = channel;
		this.requestProcessor = requestProcessor;
		this.req = req;
	}

	@Override
	public void completed(Integer result, ByteBuffer buffer) {
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);  
        writeBuffer.put(req);  
        writeBuffer.flip();  
		//如果没有发送完，就继续发送直到完成  
        if (buffer.hasRemaining())  
            channel.write(buffer, buffer, this);  
        else{  
            //创建新的Buffer  
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);  
            //异步读  第三个参数为接收消息回调的业务Handler  
            channel.read(readBuffer, readBuffer, new AioTcpServerReadHandler(requestProcessor, channel));  
        }
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		try {  
            channel.close();  
        } catch (IOException e) {  
        } 
	}
	
	
	
	
	
	public static String prepareResponse() {
        String hello = "<html><head><meta charset=\"utf-8\" /></head><body>IMSA v0.0.2...微服务工业云（测试版本）<br />测试读入内容是否正确<br />Hello World!</body></html>";   
        StringBuilder resp = new StringBuilder();
        resp.append("HTTP/1.1 200 OK" + "\r\n");
        resp.append("Server: Microsoft-IIS/5.0 " + "\r\n");
        resp.append("Date: Thu,08 Mar 200707:17:51 GMT" + "\r\n");
        resp.append("Connection: Keep-Alive" + "\r\n");
        resp.append("Content-Length: " + hello.getBytes().length + "\r\n");
        resp.append("Content-Type: text/html\r\n");
        resp.append("\r\n" + hello);
        return resp.toString();
	}

}
