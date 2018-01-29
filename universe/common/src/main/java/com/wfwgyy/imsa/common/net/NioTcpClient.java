package com.wfwgyy.imsa.common.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 系统所有组件向消息总线Plato注册，向消息总线发送消息的类均需继承本类。
 * @author 闫涛 2018.01.25 v0.0.1
 *
 */
public class NioTcpClient implements Runnable {
	//private static Lock lock = new ReentrantLock(); // 在并发量大的情况下采用有性能优势，在并发量小的情况如本处采用synchronized性能更好
	private static Queue<String> reqs = new ConcurrentLinkedQueue();
	private Selector selector;
	private SocketChannel socketChannel;
	private String serverHost = "192.168.223.38";
	private short serverPort = 8089;
	private volatile boolean stop;
	
	public static void addReq(String req) {
		reqs.add(req);
	}
	
	public NioTcpClient() {
		try {
            //打开多路复用器
            selector = Selector.open();
            //打开管道
            socketChannel = SocketChannel.open();
            //设置管道为非阻塞模式
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
	}

	@Override
	public void run() {
		try {
            doConnect();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        while (!stop) {
        	System.out.println("############### size=" + reqs.size() + "!");
            try {
                //阻塞等待1s，若超时则返回
                selector.select(1000);
                //获取所有selectionkey
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                //遍历所有selectionkey
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    //获取之后删除
                    it.remove();
                    try {
                        //处理该selectionkey
                        handleInput(key);
                    } catch (Exception e) {
                    	System.out.println("exception:" + e.getMessage() + "!");
                        if (key != null) {
                            //取消selectionkey
                            key.cancel();
                            if (key.channel() != null) {
                                //关闭该通道
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        if (selector != null) {
            try {
                //关闭多路复用器
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
	public void handleInput(SelectionKey key) throws IOException{
        //若该selectorkey可用
        if (key.isValid()) {
            //将key转型为SocketChannel
            SocketChannel sc = (SocketChannel) key.channel();
            //判断是否连接成功
            if (key.isConnectable()) {
                //若已经建立连接
                if (sc.finishConnect()) {
                    //向多路复用器注册可读事件
                    sc.register(selector, SelectionKey.OP_READ);
                    //向管道写数据
                    doWrite(sc);
                }else {
                    //连接失败 进程退出
                    System.exit(1);
                }
            }
            
            if (key.isWritable()) {
            	doWrite(sc);
            }

            //若是可读的事件
            if (key.isReadable()) {
                //创建一个缓冲区
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                System.out.println("before  :  "+readBuffer);
                //从管道中读取数据然后写入缓冲区中
                int readBytes = sc.read(readBuffer);
                System.out.println("after :  "+readBuffer);
                //若有数据
                if (readBytes > 0) {
                    //反转缓冲区
                    readBuffer.flip();
                    System.out.println(readBuffer);

                    byte[] bytes = new byte[readBuffer.remaining()];
                    //获取缓冲区并写入字节数组中
                    readBuffer.get(bytes);
                    //将字节数组转换为String类型
                    String body = new String(bytes);
                    System.out.println(body.length());
                    System.out.println("Now is : " + body + "!");
                    sc.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                    //this.stop = true;
                } else if (readBytes < 0) {
                	System.out.println("######## !!!!!!!!!!!!!!!!! readBytes=" + readBytes + "!");
                    key.cancel();
                    sc.close();
                } else {
                    sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                }
            }
        }
    }
	

    public void doConnect() throws IOException {
    	if (socketChannel.isConnected()) {
    		return ;
    	}
        //通过ip和端口号连接到服务器
        if (socketChannel.connect(new InetSocketAddress(serverHost, serverPort))) {
            //向多路复用器注册可读事件
            socketChannel.register(selector, SelectionKey.OP_READ);
            //向管道写数据
            doWrite(socketChannel);
        } else {
            //若连接服务器失败,则向多路复用器注册连接事件
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }
    
    private void doWrite(SocketChannel sc) throws IOException {
    	System.out.println("doWrite 1");
    	String reqJson = reqs.poll();
    	System.out.println("doWrite 2:" + reqJson + "!");
    	if (null == reqJson) {
    		return ;
    	}
        //要写的内容
        byte[] req = reqJson.getBytes();
        //为字节缓冲区分配指定字节大小的容量
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        //将内容写入缓冲区
        writeBuffer.put(req);
        //反转缓冲区
        writeBuffer.flip();
        //输出打印缓冲区的可读大小
        System.out.println(writeBuffer.remaining());
        //将内容写入管道中
        sc.write(writeBuffer);
        if (!writeBuffer.hasRemaining()) {
            //若缓冲区中无可读字节，则说明成功发送给服务器消息
            System.out.println("Send order 2 server succeed.");
        }
        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }
}
