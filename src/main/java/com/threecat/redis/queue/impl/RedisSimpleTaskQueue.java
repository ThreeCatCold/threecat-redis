package com.threecat.redis.queue.impl;

import com.threecat.redis.queue.TaskQueue;
import redis.clients.jedis.Jedis;

/**
 * 主要使用lpush和brpop这种一进一出组合实现，具体怎么实现待定
 */
public class RedisSimpleTaskQueue implements TaskQueue
{
	private Jedis jedis;

	private String queueName;

	@Override public void add(String element)
	{

	}

	@Override public void poll()
	{

	}
}
