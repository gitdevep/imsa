package com.wfwgyy.imsa.common.net;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AioTcpClientReadThread implements Runnable {
	public AsynchronousSocketChannel clientChannel;
	public CountDownLatch readLatch;
	
	public AioTcpClientReadThread(AsynchronousSocketChannel clientChannel) {
		this.clientChannel = clientChannel;
	}

	@Override
	public void run() {
		while (true) {
			System.out.println("readResponse " + System.currentTimeMillis() + "!");
			readLatch = new CountDownLatch(1);
			ByteBuffer readBuffer = ByteBuffer.allocate(1024);  
	        clientChannel.read(readBuffer,readBuffer,new AioTcpClientReadHandler(clientChannel, readLatch));
	        System.out.println("rrqm1");
			try {
				readLatch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("rrqm2");
		}
	}
}
