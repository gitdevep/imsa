package com.wfwgyy.imsa.common.test.net;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import com.wfwgyy.imsa.common.AppConsts;
import com.wfwgyy.imsa.common.net.AioTcpServer;

public class AioTcpServerTest {

	@Test
	public void testGetImsaRequestType1() {
		//fail("Not yet implemented");
		StringBuilder requestBuffer = new StringBuilder("123456" + AppConsts.MSG_BEGIN_TAG + "789abcdGET / HTTP/1.1\4\n");
		AioTcpServer aio = AioTcpServer.getTestInstance();
		int msgType = aio.getImsaRequestType(requestBuffer);
		Assert.assertTrue("获取IMSA消息类型成功", AppConsts.MT_IMSA_MSG==msgType);
		//Assert.assertTrue("测试通过", 1==1);
	}
	
	@Test
	public void testGetImsaRequestType2() {
		StringBuilder requestBuffer = new StringBuilder("123456" + AppConsts.MSG_HTTP_GET_BEGINE + "789abcd" + AppConsts.MSG_BEGIN_TAG + "eee");
		AioTcpServer aio = AioTcpServer.getTestInstance();
		int msgType = aio.getImsaRequestType(requestBuffer);
		Assert.assertTrue("获取GET消息类型成功", AppConsts.MT_HTTP_GET_REQ==msgType);
	}
	
	@Test
	public void testGetImsaRequestType3() {
		StringBuilder requestBuffer = new StringBuilder("123456" + AppConsts.MSG_HTTP_POST_BEGINE + "789abcd" + AppConsts.MSG_BEGIN_TAG + "eee");
		AioTcpServer aio = AioTcpServer.getTestInstance();
		int msgType = aio.getImsaRequestType(requestBuffer);
		Assert.assertTrue("获取GET消息类型成功", AppConsts.MT_HTTP_POST_REQ==msgType);
	}

}
