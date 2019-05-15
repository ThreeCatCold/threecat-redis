package com.threecat.redis.jedis.clients;

import com.threecat.redis.util.NumberUtils;
import com.threecat.redis.util.PropertiesUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;

public class RedisConfig
{
	private String ip;

	private int port;

	private String authPassword;

	private JedisPoolConfig poolConfig;

	private int connectionTimeout;

	public RedisConfig(String configPath)
	{
		this.poolConfig = new JedisPoolConfig();
		Map<String, String> redisConfigs = PropertiesUtils.loadMapProperties(configPath, false);

		this.ip = redisConfigs.get("redis.ip");
		this.port = NumberUtils.parseInt(redisConfigs.get("redis.port"), 6379);
		this.authPassword = redisConfigs.get("redis.password");
		// 默认3s会话超时时间
		this.connectionTimeout = NumberUtils.parseInt(redisConfigs.get("redis.connectionTimeout"), 3000);

		// 连接池最大活动对象数
		poolConfig.setMaxTotal(NumberUtils.parseInt(redisConfigs.get("redis.pool.maxTotal"), 100));
		// 连接池最大空闲连接数
		poolConfig.setMaxIdle(NumberUtils.parseInt(redisConfigs.get("redis.pool.maxIdle"), 10));
		// 连接池最小空闲连接数
		poolConfig.setMinIdle(NumberUtils.parseInt(redisConfigs.get("redis.pool.minIdle"), 10));
		// 当池内没有返回对象时，最大等待时间
		poolConfig.setMaxWaitMillis(NumberUtils.parseLong(redisConfigs.get("redis.pool.maxWaitMillis"), 10000));
		// 当调用borrow Object方法时，是否进行有效性检查
		poolConfig.setTestOnBorrow(NumberUtils.parseBoolean(redisConfigs.get("redis.pool.testOnBorrow"), true));
		// 调用return Object方法时，是否进行有效性检查
		poolConfig.setTestOnReturn(NumberUtils.parseBoolean(redisConfigs.get("redis.pool.testOnReturn"), true));
		// 空闲链接”检测线程，检测的周期，毫秒数。如果为负值，表示不运行“检测线程”。默认为-1.
		poolConfig.setTimeBetweenEvictionRunsMillis(
				NumberUtils.parseLong(redisConfigs.get("redis.pool.timeBetweenEvictionRunsMillis"), 30000));
		// 向调用者输出“链接”对象时，是否检测它的空闲超时
		poolConfig.setTestWhileIdle(NumberUtils.parseBoolean(redisConfigs.get("redis.pool.testWhileIdle"), true));
		// 对于“空闲链接”检测线程而言，每次检测的链接资源的个数。默认为3.
		poolConfig.setNumTestsPerEvictionRun(
				NumberUtils.parseInt(redisConfigs.get("redis.pool.numTestsPerEvictionRun"), 50));

		// 数据库Index
		int dbIndex = NumberUtils.parseInt(redisConfigs.get("redis.dbIndex"), 0);
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public String getAuthPassword()
	{
		return authPassword;
	}

	public void setAuthPassword(String authPassword)
	{
		this.authPassword = authPassword;
	}

	public JedisPoolConfig getPoolConfig()
	{
		return poolConfig;
	}

	public void setPoolConfig(JedisPoolConfig poolConfig)
	{
		this.poolConfig = poolConfig;
	}

	public int getConnectionTimeout()
	{
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout)
	{
		this.connectionTimeout = connectionTimeout;
	}
}
