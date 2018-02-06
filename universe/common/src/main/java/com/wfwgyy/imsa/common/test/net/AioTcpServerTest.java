package com.wfwgyy.imsa.common.test.net;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;

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
	
	@Test
	public void testProcessImsaRequests1() {
		StringBuilder requestBuffer = new StringBuilder("123456");
		requestBuffer.append("GET /abc/index/a?b=1&c=2 HTTP/1.1\r\n");
		requestBuffer.append("Host: 192.168.0.101:8088\r\n");
		requestBuffer.append("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3236.0 Safari/537.36\r\n");
		requestBuffer.append("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\r\n");
		requestBuffer.append("Accept-Encoding: gzip, deflate\r\n");
		requestBuffer.append("Accept-Language: zh-CN,zh;q=0.9,en;q=0.8\r\n");
		requestBuffer.append("Proxy-Connection: keep-alive\r\n");
		requestBuffer.append("Upgrade-Insecure-Requests: 1\r\n");
		requestBuffer.append("X-Lantern-Version: 4.4.2\r\n");
		requestBuffer.append("\r\n");
		requestBuffer.append("789abc");
		AsynchronousSocketChannel channel = null;
		try {
			channel = AsynchronousSocketChannel.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AioTcpServer aio = AioTcpServer.getTestInstance();
		aio.processImsaRequests(channel, requestBuffer);
		System.out.println("如果看到完整的HTTP GET请求请按1");
		Scanner sc = new Scanner(System.in);
		int rst = sc.nextInt();
		Assert.assertTrue("解析正常HTTP GET请求用例", 1==rst);
	}
	
	@Test
	public void testProcessImsaRequests2() {
		StringBuilder requestBuffer = new StringBuilder("123456");
		requestBuffer.append("GET /abc/index/a?b=1&c=2 HTTP/1.1\r\n");
		requestBuffer.append("Host: 192.168.0.101:8088\r\n");
		requestBuffer.append("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3236.0 Safari/537.36\r\n");
		requestBuffer.append("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\r\n");
		requestBuffer.append("Accept-Encoding: gzip, deflate\r\n");
		requestBuffer.append("Accept-Language: zh-CN,zh;q=0.9,en;q=0.8\r\n");
		requestBuffer.append("789abc");
		AsynchronousSocketChannel channel = null;
		try {
			channel = AsynchronousSocketChannel.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AioTcpServer aio = AioTcpServer.getTestInstance();
		aio.processImsaRequests(channel, requestBuffer);
		System.out.println("如果没有看到HTTP GET请求请按1");
		Scanner sc = new Scanner(System.in);
		int rst = sc.nextInt();
		Assert.assertTrue("解析正常HTTP GET请求用例", 1==rst);
	}

}
