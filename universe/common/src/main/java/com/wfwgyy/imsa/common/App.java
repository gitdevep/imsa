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
        
        StringBuilder requestBuffer = new StringBuilder("123456" + AppConsts.MSG_BEGIN_TAG + "我是一个天才" + AppConsts.MSG_END_TAG + "789ABC" + 
        AppConsts.MSG_BEGIN_TAG + "他是个笨蛋" + AppConsts.MSG_END_TAG + "DEFGH" + AppConsts.MSG_BEGIN_TAG + "aldfkslfjdlsfkj");

        int startPos = requestBuffer.indexOf(AppConsts.MSG_BEGIN_TAG);
        if (startPos < 0) {
        	return ;
        }
        int endPos = requestBuffer.indexOf(AppConsts.MSG_END_TAG);
        if (endPos <= startPos) {
        	return ;
        }
        
        String rawRequest = null;
        while (startPos>=0 && endPos > startPos) {
            rawRequest = requestBuffer.substring(startPos + AppConsts.MSG_BEGIN_TAG.length(), endPos);
            System.out.println("req:" + rawRequest + "!");
            requestBuffer.delete(startPos, endPos + AppConsts.MSG_END_TAG.length());
            System.out.println("buffer:" + requestBuffer.toString() + "!");
            startPos = requestBuffer.indexOf(AppConsts.MSG_BEGIN_TAG);
            endPos = requestBuffer.indexOf(AppConsts.MSG_END_TAG, startPos + 1);
        }
        
        
        int iDebug = 0;
        if (1 == iDebug) {
        	return;
        }
        
        AioTcpClient.start(AppConsts.FACADE_HOST, AppConsts.FACADE_PORT);
        try {
			Thread.sleep(2*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        AioTcpClient.sendMsg(AppConsts.MSG_BEGIN_TAG + "我是一个程序员" + AppConsts.MSG_END_TAG);
        AioTcpClient.sendMsg(AppConsts.MSG_BEGIN_TAG + "第一步" + AppConsts.MSG_END_TAG);
        AioTcpClient.sendMsg(AppConsts.MSG_BEGIN_TAG + "第二步" + AppConsts.MSG_END_TAG);
        AioTcpClient.sendMsg(AppConsts.MSG_BEGIN_TAG + "第三步\r\n" + AppConsts.MSG_END_TAG);
        AioTcpClient.sendMsg(AppConsts.MSG_BEGIN_TAG + "    第100步    " + AppConsts.MSG_END_TAG);
        AioTcpClient.sendMsg(AppConsts.MSG_BEGIN_TAG + "    第101步    " + AppConsts.MSG_END_TAG);
        AioTcpClient.sendMsg(AppConsts.MSG_BEGIN_TAG + "    第102步    " + AppConsts.MSG_END_TAG);
        AioTcpClient.sendMsg(AppConsts.MSG_BEGIN_TAG + "    第103步    " + AppConsts.MSG_END_TAG);
        AioTcpClient.sendMsg(AppConsts.MSG_BEGIN_TAG + "    第104步    " + AppConsts.MSG_END_TAG);
        AioTcpClient.sendMsg(AppConsts.MSG_BEGIN_TAG + "    第105步    " + AppConsts.MSG_END_TAG);
        AioTcpClient.sendMsg(AppConsts.MSG_BEGIN_TAG + "    第106步    " + AppConsts.MSG_END_TAG);
        AioTcpClient.sendMsg(AppConsts.MSG_BEGIN_TAG + "    第107步   " + AppConsts.MSG_END_TAG);
        
        Thread thd = new Thread(new AioTcpClientResponseThread());
        thd.start();
    }
}
