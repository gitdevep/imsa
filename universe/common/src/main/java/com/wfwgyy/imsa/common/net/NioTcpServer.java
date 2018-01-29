package com.wfwgyy.imsa.common.net;

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

import com.wfwgyy.imsa.common.AppConsts;
import com.wfwgyy.imsa.common.Turple2;
import com.wfwgyy.imsa.common.jedis.JedisEngine;
import com.wfwgyy.imsa.common.msg.ImsaMsgEngine;

/**
 * 基于NIO技术的服务器公共类，在门户Facade、消息总线Plato、微服务器控制器Caesar、微服务Apollo中的
 * 服务器均继承本类。
 * @author 闫涛 2018.01.25 v0.0.1
 *
 */
public abstract class NioTcpServer {
	protected abstract void processRequest(SelectionKey key, Selector selector);
	protected abstract void processResponse(SelectionKey key, Selector selector);
	
	/**
	 * 程序总入口，启动Imsa服务器
	 * @throws Exception
	 */
	public void start(short port) throws Exception {  
        Selector selector = Selector.open();  
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();  
        serverSocketChannel.configureBlocking(false);  
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);  
        serverSocketChannel.socket().setReuseAddress(true);  
        serverSocketChannel.socket().bind(new InetSocketAddress(port));  
        System.out.println("listen on port=" + port + "!");
        while(true){
            while (selector.select() > 0) {
                Iterator<SelectionKey> selectedKeys = selector.selectedKeys() .iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = selectedKeys.next();  
                    if (key.isAcceptable()) {
                    	acceptConnection(key, selector);
                    } else if (key.isReadable()) {
                        //readRequest(key, selector);
                    	processRequest(key, selector);
                    } else if (key.isWritable()) {
                        //sendResponse(key, prepareTestResponse());
                    	processResponse(key, selector);
                    }
                    selectedKeys.remove();
                }  
            }  
        }
    }
	
	/**
	 * 接受客户端的连接请求
	 * @param key
	 * @param selector
	 */
	protected void acceptConnection(SelectionKey key, Selector selector) {
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
	 * @return 二元元组，a代表请求文本内容，b为二进制对象URL数组
	 */
	protected Turple2<String, String[]> readRequest(SelectionKey key, Selector selector) {
		SocketChannel channel = (SocketChannel) key.channel();  
		Turple2<String, String[]> rst = new Turple2<String, String[]>("", null);
        try {
			channel.configureBlocking(false);
	        String receive = receive(channel);
	        // 如果没有接收到内容，就直接返回
	        if (receive.equals("")) {
	        	return rst;
	        }
	        BufferedReader b = new BufferedReader(new StringReader(receive));  
	        String s = b.readLine();  
	        StringBuilder req = new StringBuilder();
	        while (s != null) {  
	            req.append(s + "\r\n");
	            s = b.readLine();  
	        }  
	        b.close(); 
	        String[] urls = null;
	        channel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
	        rst = new Turple2<String, String[]>(req.toString(), urls);
	        
	        System.out.println("#######:" + channel.hashCode() + "!");
	        
	        
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return rst;
	}
	
	/**
	 * 从消息总线接收到需要发送的HTTP响应，将响应发送给客户端
	 * @param key
	 * @param resp
	 */
	protected void sendResponse(SelectionKey key, Selector selector, String resp) {
		SocketChannel channel = (SocketChannel) key.channel(); 
        ByteBuffer buffer = ByteBuffer.allocate(1024);  
          
        byte[] bytes = resp.toString().getBytes();  
        buffer.put(bytes);  
        buffer.flip();  
        try {
			channel.write(buffer);
	        //channel.shutdownInput();  
	        //channel.close();
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
}
