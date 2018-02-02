package com.wfwgyy.imsa.common.net;

import java.nio.channels.AsynchronousSocketChannel;

@FunctionalInterface
public interface RequestProcessor {
	public byte[] processRequest(AsynchronousSocketChannel channel, byte[] req);
}
