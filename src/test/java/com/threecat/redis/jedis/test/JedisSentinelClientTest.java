package com.threecat.redis.jedis.test;

import com.threecat.redis.sentinel.SentinelClient;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class JedisSentinelClientTest
{
	public static void main(String[] args)
	{
		testSentinelClient();
	}

	private static void testSentinelClient()
	{
		SentinelClient sentinelClient = SentinelClient.getInstance();
		Jedis jedis = sentinelClient.getJedis();
		Map<String, String> params = new HashMap<>(1);
		params.put("key", "value");
		jedis.hmset("sentinel-key", params);
		sentinelClient.closeJedis(jedis);
	}
}
