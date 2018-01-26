package com.wfwgyy.imsa.common.jedis;

import java.util.Optional;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisEngine {	
	private static int MAX_TOTAL = 100;
	private static int MAX_IDLE = 10;
	private static int MAX_WAIT_MILLIS = 90 * 1000;
	private static String HOST = "10.1.6.166";
	private static short PORT = 6379;
	private static String UNIQUE_ID_INIT_VAL = "1"; // 唯一标识数字的初始值
	private static JedisPool pool;
	

	/**
	 * 取出指定key的当前长整型唯一标识数字，并将该数字加1再存回原来的key中，类似Mysql中的自增字段功能
	 * @param key
	 * @return 长整型唯一标识数字
	 * @author 闫涛 2018.01.24 v0.0.1
	 */
	public static long getKeyUniqueId(String key) {
		Optional<String> val = get("msg_id");
		long uniqueId = Long.parseLong(val.orElseGet(() -> createUniqueIdKey(key)));
		set(key, "" + (uniqueId + 1));
		return uniqueId;
	}
	
	/**
	 * 如果key在redis中不存在，则创建该key，并将初始化值设置为1
	 * @param key
	 * @return
	 * @author 闫涛 2018.01.24 v0.0.1
	 */
	public static String createUniqueIdKey(String key) {
		System.out.println("key=" + key + "!");
		set(key, UNIQUE_ID_INIT_VAL);
		return UNIQUE_ID_INIT_VAL;
	}
	
	
	
	
	
	
	/**
	 * 生成Redis连接池对象
	 * @author 闫涛 2018.01.24 v0.0.1
	 */
	private static void createJedisPool() {
		// 建立连接池配置参数
        JedisPoolConfig config = new JedisPoolConfig();
     // 设置最大连接数
        config.setMaxTotal(MAX_TOTAL);
        // 设置最大阻塞时间，记住是毫秒数milliseconds
        config.setMaxWaitMillis(MAX_WAIT_MILLIS);
        // 设置空间连接
        config.setMaxIdle(MAX_IDLE);
        // 创建连接池
        pool = new JedisPool(config, HOST, PORT);
	}
	
	/**
	 * 在调用本类方法之前，先调用本方法，确保已经创建了Redis连接池
	 * @author 闫涛 2018.01.24 v0.0.1
	 */
	private static void initJedisEngine() {
		if (null == pool) {
			createJedisPool();
		}
	}
	
	/**
	 * 从Redis连接池中获取一个连接，需要在用完后调用releaseRedis将其返回到连接池中
	 * @return
	 * @author 闫涛 2018.01.24 v0.0.1
	 */
	private static Jedis getJedis() {
		if (null == pool) {
			initJedisEngine();
		}
		return pool.getResource();
	}
	
	/**
	 * 将之前获取的jedis对象归还到连接池中
	 * @param jedis
	 * @author 闫涛 2018.01.24 v0.0.1
	 */
	private static void releaseJedis(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}
	
	/**
	 * 从Redis中获取指定Key所对应的值，该值可能为空
	 * @param key
	 * @return 可能为空的字符串
	 * @author 闫涛 2018.01.24 v0.0.1
	 */
	private static Optional<String> get(String key) {
		Jedis jedis = getJedis();
		if (null == jedis) {
			return Optional.empty();
		}
		return Optional.ofNullable(jedis.get(key));
	}
	
	/**
	 * 设置Redis中指定Key的值，需要由调用者保证Key和Val不为空
	 * @param key
	 * @param val
	 * @return 设置成功还是失败
	 * @author 闫涛 2018.01.24 v0.0.1
	 */
	private static boolean set(String key, String val) {
		Jedis jedis = getJedis();
		if (null == jedis) {
			return false;
		}
		jedis.set(key, val);
		return true;
	}
}
