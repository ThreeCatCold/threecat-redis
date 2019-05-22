package com.threecat.redis.sentinel;

import com.threecat.redis.util.CommonUtils;
import com.threecat.redis.util.NumberUtils;
import com.threecat.redis.util.PropertiesUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SentinelConfig
{
	private Set<String> clusterConfig;

	private String masterName;

	private String authPassword;

	private JedisPoolConfig poolConfig;

	private int connectionTimeout;

	public SentinelConfig(String configPath)
	{
		this.poolConfig = new JedisPoolConfig();
		Map<String, String> sentinelConfigs = PropertiesUtils.loadMapProperties(configPath, false);

		this.clusterConfig = parseClusterConfig(sentinelConfigs.get("sentinel.clusterConfig"));
		this.masterName = sentinelConfigs.get("sentinel.masterName");
		this.authPassword = sentinelConfigs.get("sentinel.password");
		// 默认3s会话超时时间
		this.connectionTimeout = NumberUtils.parseInt(sentinelConfigs.get("sentinel.connectionTimeout"), 3000);

		// 连接池最大活动对象数
		poolConfig.setMaxTotal(NumberUtils.parseInt(sentinelConfigs.get("sentinel.pool.maxTotal"), 100));
		// 连接池最大空闲连接数
		poolConfig.setMaxIdle(NumberUtils.parseInt(sentinelConfigs.get("sentinel.pool.maxIdle"), 10));
		// 连接池最小空闲连接数
		poolConfig.setMinIdle(NumberUtils.parseInt(sentinelConfigs.get("sentinel.pool.minIdle"), 10));
		// 当池内没有返回对象时，最大等待时间
		poolConfig.setMaxWaitMillis(NumberUtils.parseLong(sentinelConfigs.get("sentinel.pool.maxWaitMillis"), 10000));
		// 当调用borrow Object方法时，是否进行有效性检查
		poolConfig.setTestOnBorrow(NumberUtils.parseBoolean(sentinelConfigs.get("sentinel.pool.testOnBorrow"), true));
		// 调用return Object方法时，是否进行有效性检查
		poolConfig.setTestOnReturn(NumberUtils.parseBoolean(sentinelConfigs.get("sentinel.pool.testOnReturn"), true));
		// 空闲链接”检测线程，检测的周期，毫秒数。如果为负值，表示不运行“检测线程”。默认为-1.
		poolConfig.setTimeBetweenEvictionRunsMillis(
				NumberUtils.parseLong(sentinelConfigs.get("sentinel.pool.timeBetweenEvictionRunsMillis"), 30000));
		// 向调用者输出“链接”对象时，是否检测它的空闲超时
		poolConfig.setTestWhileIdle(NumberUtils.parseBoolean(sentinelConfigs.get("sentinel.pool.testWhileIdle"), true));
		// 对于“空闲链接”检测线程而言，每次检测的链接资源的个数。默认为3.
		poolConfig.setNumTestsPerEvictionRun(
				NumberUtils.parseInt(sentinelConfigs.get("sentinel.pool.numTestsPerEvictionRun"), 50));

	}

	private Set<String> parseClusterConfig(String clusterConfigStr)
	{
		if (CommonUtils.isEmptyString(clusterConfigStr))
		{
			return new HashSet<>(Arrays.asList("127.0.0.1:26379"));
		}
		return new HashSet<>(Arrays.asList(clusterConfigStr.split(",")));
	}

	public Set<String> getClusterConfig()
	{
		return clusterConfig;
	}

	public void setClusterConfig(Set<String> clusterConfig)
	{
		this.clusterConfig = clusterConfig;
	}

	public String getMasterName()
	{
		return masterName;
	}

	public void setMasterName(String masterName)
	{
		this.masterName = masterName;
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
