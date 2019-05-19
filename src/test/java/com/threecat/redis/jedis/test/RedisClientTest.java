package com.threecat.redis.jedis.test;

import com.threecat.redis.jedis.clients.RedisClient;
import com.threecat.redis.jedis.clients.RedisConfig;
import com.threecat.redis.lock.DistributedLock;
import com.threecat.redis.lock.impl.RedisDistLock;
import com.threecat.redis.util.ThreadUtils;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RedisClientTest
{
	@Test
	public void testRedisClient()
	{
		RedisClient redisClient = RedisClient.getInstance();
		Class[] paramTypes = {String.class, String.class, String.class, String.class, int.class};
		Object[] params = {"LOCK_KEY", "REQUEST_ID", "NX", "PX", 60000};
		redisClient.executeCommand("set", paramTypes, params);
		System.out.println("execute redis command success.");
	}

	@Test
	public void testDistLock()
	{
		String lockKey = "DIST_LOCK";
		String requestId = lockKey + System.currentTimeMillis();
		int expireTime = 1000;

		ExecutorService threadPool = Executors.newFixedThreadPool(5);

		for (int i = 0; i < 5; i++)
		{
			threadPool.submit(() -> {
				RedisClient redisClient = RedisClient.getInstance();
				DistributedLock lock = new RedisDistLock(lockKey, lockKey + ":" + System.currentTimeMillis(),
						expireTime, redisClient);
				try
				{
					while (true)
					{
						if (lock.tryLock())
						{
							System.out.println("Do method: " + Thread.currentThread().getName());
							// 假设每次执行任务需要耗费300ms
							TimeUnit.MILLISECONDS.sleep(300);
							lock.unlock();
						}
						else
						{
							TimeUnit.MILLISECONDS.sleep(50);
						}
					}
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			});
			ThreadUtils.sleep(300);
		}
		ThreadUtils.sleep(Integer.MAX_VALUE);
	}

}
