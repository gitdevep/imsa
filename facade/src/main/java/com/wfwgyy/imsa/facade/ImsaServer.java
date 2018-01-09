package com.wfwgyy.imsa.facade;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 整个系统入口，将监听所有外部系统发送的请求，将请求转化为系统消息，并发布到消息总线上去。同时接收消息总线消息，如果是HTML消息
 * 发送消息，则将该消息发送给调用者。
 * @author 闫涛 2018.01.09
 *
 */
public class ImsaServer {
	/**
	 * 程序总入口，启动Imsa服务器
	 * @throws Exception
	 */
	public void start() throws Exception {  
        Selector selector = Selector.open();  
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();  
        serverSocketChannel.configureBlocking(false);  
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);  
        serverSocketChannel.socket().setReuseAddress(true);  
        serverSocketChannel.socket().bind(new InetSocketAddress(8084));  
        while(true){  
            while (selector.select() > 0) {
                Iterator<SelectionKey> selectedKeys = selector.selectedKeys() .iterator();  
                while (selectedKeys.hasNext()) {  
                    SelectionKey key = selectedKeys.next();  
                    if (key.isAcceptable()) {
                    	acceptConnection(key, selector);
                    } else if (key.isReadable()) {  
                        readRequest(key, selector);
                    } else if (key.isWritable()) {
                        sendResponse(key, prepareTestResponse());
                    }  
                }  
            }  
        }
    }
	
	/**
	 * 接受客户端的连接请求
	 * @param key
	 * @param selector
	 */
	private void acceptConnection(SelectionKey key, Selector selector) {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();  
        SocketChannel channel = null;
		try {
			channel = ssc.accept();
	        if(channel != null){  
	            channel.configureBlocking(false);  
	            channel.register(selector, SelectionKey.OP_READ);// 客户socket通道注册读操作  
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	/**
	 * 读取消息内容，并向消息总线plato发送消息
	 * @param key
	 * @param selector
	 */
	private void readRequest(SelectionKey key, Selector selector) {
		SocketChannel channel = (SocketChannel) key.channel();  
        try {
			channel.configureBlocking(false);
	        String receive = receive(channel);
	        // 如果没有接收到内容，就直接返回
	        if (receive.equals("")) {
	        	return ;
	        }
	        BufferedReader b = new BufferedReader(new StringReader(receive));  
	        String s = b.readLine();  
	        StringBuilder req = new StringBuilder();
	        while (s != null) {  
	            req.append(s + "\r\n");
	            s = b.readLine();  
	        }  
	        b.close();                        
	        channel.register(selector, SelectionKey.OP_WRITE);
	        // 发送消息
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 从消息总线接收到需要发送的HTTP响应，将响应发送给客户端
	 * @param key
	 * @param resp
	 */
	private void sendResponse(SelectionKey key, String resp) {
		SocketChannel channel = (SocketChannel) key.channel(); 
        ByteBuffer buffer = ByteBuffer.allocate(1024);  
          
        byte[] bytes = resp.toString().getBytes();  
        buffer.put(bytes);  
        buffer.flip();  
        try {
			channel.write(buffer);
	        channel.shutdownInput();  
	        channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
	}
  
    /**
     * 接收请求数据
     * @param socketChannel
     * @return
     */
    private String receive(SocketChannel socketChannel) {  
        ByteBuffer buffer = ByteBuffer.allocate(1024);  
        byte[] bytes = null;  
        int size = 0;  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
	        while ((size = socketChannel.read(buffer)) > 0) {  
	            buffer.flip();  
	            bytes = new byte[size];  
	            buffer.get(bytes);  
	            baos.write(bytes);  
	            buffer.clear();  
	        }  
	        bytes = baos.toByteArray(); 
        } catch (IOException ex) {
        	return "";
        }
  
        return new String(bytes);  
    }
    
    
    
    
    
    
    
    
    
    
    
    /**
     * 临时方法，产生向客户端发送的响应
     * @return
     */
    private String prepareTestResponse() {
        String hello = "<html><head><meta charset=\"utf-8\" /></head><body>IMSA v0.0.1...微服务工业云（测试版本）<br />测试读入内容是否正确<br />Hello World!</body></html>";   
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
