package com.wfwgyy.imsa.common.net;

import java.util.Queue;

public class AioTcpClientResponseThread implements Runnable {
	@Override
	public void run() {
		Queue<String> responseQueue = null;
		String resp = null;
		while (true) {
			responseQueue = AioTcpClient.responseQueue;
			resp = responseQueue.poll();
			if (resp != null) {
				System.out.println("######## \r\n" + resp + "\r\n#############\r\n###########\r\n");
			}
		}
	}

}
