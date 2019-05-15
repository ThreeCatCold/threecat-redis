package com.threecat.redis.sentinel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.io.File;

public class SentinelClient
{
	private Logger logger = LoggerFactory.getLogger(SentinelClient.class);

	private JedisSentinelPool sentinelPool;

	private SentinelConfig sentinelConfig;

	private SentinelClient()
	{
		loadConfigs();
	}

	private void loadConfigs()
	{
		String configPath =
				System.getProperty("user.dir") + File.separator + "config" + File.separator + "sentinel-config.properties";
		sentinelConfig = new SentinelConfig(configPath);
		sentinelPool = new JedisSentinelPool(sentinelConfig.getMasterName(), sentinelConfig.getClusterConfig(),
				sentinelConfig.getPoolConfig(), sentinelConfig.getConnectionTimeout(),
				sentinelConfig.getAuthPassword());
	}

	private static class SingletonHolder
	{
		private static final SentinelClient INSTANCE = new SentinelClient();
	}

	/**
	 * 单例客户端
	 *
	 * @return
	 */
	public static SentinelClient getInstance()
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
			jedis = sentinelPool.getResource();
			// 我这里的demo都是只使用0号数据库，这是建议。
			jedis.select(0);
		}
		catch (Exception e)
		{
			logger.error("Get jedis from jedisSentinelPool error: " + e.getMessage());
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
