package com.wfwgyy.imsa.common.net;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import com.wfwgyy.imsa.common.Turple2;

public class AioTcpClientWriteThread implements Runnable {
	public static CountDownLatch writeLatch;

	@Override
	public void run() {
		Queue<Turple2<AsynchronousSocketChannel, String>> requestQueue = AioTcpClient.requestQueue;
		Turple2<AsynchronousSocketChannel, String> item = null;
		String msg = null;
		while (true) {
			while ((item = requestQueue.poll()) != null) {
				writeLatch = new CountDownLatch(1);
				sendMsg(item);
				try {
					writeLatch.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	
	public void sendMsg(Turple2<AsynchronousSocketChannel, String> item){
        byte[] req = item.v2.getBytes();  
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);  
        writeBuffer.put(req);  
        writeBuffer.flip();
        //异步写  
        item.v1.write(writeBuffer, writeBuffer,new AioTcpClientWriteHandler(item.v1)); 
    }

}
