package com.wfwgyy.imsa.facade;

/**
 * Facade用于接收外界的普通请求，其会调用微服务体系来完成这些请求，最终向客户端返回响应。
 * 虽然微服务架构处理请求是异步方式的，但是Facade是以请求响应方式工作，将后台基于异步消
 * 息机制的微服务进行了封装。
 * 【闫涛 2018.01.09】v0.0.1
 */
public class App 
{
    public static void main( String[] args )
    {
    	System.out.println( "微服务工业云平台..." );
    	int iDebug = 0;
    	if (1 == iDebug) {
    		test();
    		return ;
    	}
        ImsaServer imsaServer = new ImsaServer();
        try {
			imsaServer.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void test() {
    	AppExp appExp = new AppExp();
    	appExp.test();
    }
}
