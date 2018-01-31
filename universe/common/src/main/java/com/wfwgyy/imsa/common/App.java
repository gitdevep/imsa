package com.wfwgyy.imsa.common;

import com.wfwgyy.imsa.common.net.AioTcpClient;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        AioTcpClient.start(AppConsts.FACADE_HOST, AppConsts.FACADE_PORT);
        try {
			Thread.sleep(2*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        AioTcpClient.sendMsg("我是一个程序员");
    }
}
