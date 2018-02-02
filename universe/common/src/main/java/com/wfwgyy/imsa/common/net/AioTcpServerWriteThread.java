package com.wfwgyy.imsa.common.net;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import com.wfwgyy.imsa.common.Turple2;

public class AioTcpServerWriteThread implements Runnable {
	public static RequestProcessor requestProcessor;
	public static CountDownLatch writeLatch;

	/**
	 * 从响应队列中读出响应，然后将其发出去
	 * @author 闫涛 2018.02.02 v0.0.1
	 */
	@Override
	public void run() {
		Queue<Turple2<AsynchronousSocketChannel, String>> responseQueue = AioTcpServer.responseQueue;
		Turple2<AsynchronousSocketChannel, String> item = null;
		AsynchronousSocketChannel channel = null;
        ByteBuffer writeBuffer = null;
        byte[] bytes = null;
		while (true) {
			item = responseQueue.poll();
			while (item != null) {
				writeLatch = new CountDownLatch(1);
				channel = item.v1;
				bytes = item.v2.getBytes();
				System.out.println("AioTcpServerWriteThread.run:" + item.v2 + "!");
		        writeBuffer = ByteBuffer.allocate(bytes.length);  
		        writeBuffer.put(bytes);  
		        writeBuffer.flip(); 
				channel.write(writeBuffer, writeBuffer, new AioTcpServerWriteHandler(channel, requestProcessor, bytes));
				try {
					writeLatch.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				item = responseQueue.poll();
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
