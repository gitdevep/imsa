package com.wfwgyy.imsa.common;

import com.wfwgyy.imsa.common.net.AioTcpClient;
import com.wfwgyy.imsa.common.net.AioTcpClientResponseThread;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "AioTcpClient" );
        AioTcpClient.start(AppConsts.FACADE_HOST, AppConsts.FACADE_PORT);
        try {
			Thread.sleep(2*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        AioTcpClient.sendMsg("我是一个程序员");
        AioTcpClient.sendMsg("第一步");
        AioTcpClient.sendMsg("第二步");
        AioTcpClient.sendMsg("第三步\r\n");
        AioTcpClient.sendMsg("    第100步    ");
        AioTcpClient.sendMsg("    第101步    ");
        AioTcpClient.sendMsg("    第102步    ");
        AioTcpClient.sendMsg("    第103步    ");
        AioTcpClient.sendMsg("    第104步    ");
        AioTcpClient.sendMsg("    第105步    ");
        AioTcpClient.sendMsg("    第106步    ");
        AioTcpClient.sendMsg("    第107步    ");
        
        Thread thd = new Thread(new AioTcpClientResponseThread());
        thd.start();
    }
}
