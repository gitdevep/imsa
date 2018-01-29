package com.wfwgyy.imsa.common.net;

@FunctionalInterface
public interface RequestProcessor {
	public byte[] processRequest(byte[] req);
}
