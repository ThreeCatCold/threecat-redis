package com.threecat.redis.jedis.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.File;

/**
 * 基于jedis的redis客户端
 */
public class RedisClient
{
	private Logger logger = LoggerFactory.getLogger(RedisClient.class);
	private JedisPool jedisPool;

	private RedisConfig redisConfig;

	private RedisClient()
	{
		loadConfigs();
	}

	private void loadConfigs()
	{
		String configPath =
				System.getProperty("user.dir") + File.separator + "config" + File.separator + "redis-config.properties";
		redisConfig = new RedisConfig(configPath);
		this.jedisPool = new JedisPool(redisConfig.getPoolConfig(), redisConfig.getIp(), redisConfig.getPort(),
				redisConfig.getConnectionTimeout(), redisConfig.getAuthPassword());
	}

	private static class SingletonHolder
	{
		private static final RedisClient INSTANCE = new RedisClient();
	}

	/**
	 * 单例客户端
	 *
	 * @return
	 */
	public static RedisClient getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	/**
	 * 获取jedis客户端
	 *
	 * @return jedis
	 */
	public Jedis getJedis()
	{
		Jedis jedis = null;
		try
		{
			jedis = jedisPool.getResource();
			// 我这里的demo都是只使用0号数据库，这是建议。
			jedis.select(0);
		}
		catch (Exception e)
		{
			logger.error("Get jedis from jedisPool error: " + e.getMessage());
		}
		return jedis;
	}

	public void closeJedis(Jedis jedis)
	{
		if (jedis != null)
		{
			jedis.close();
		}
	}
}
