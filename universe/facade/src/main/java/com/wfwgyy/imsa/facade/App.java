package com.wfwgyy.imsa.facade;

import org.json.JSONObject;

import com.wfwgyy.imsa.common.jedis.JedisEngine;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        JedisEngine.test();
        JSONObject obj = new JSONObject();
        System.out.println("New project architecture");
    }
}
