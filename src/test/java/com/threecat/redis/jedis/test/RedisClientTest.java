package com.threecat.redis.jedis.test;

import com.threecat.redis.jedis.clients.RedisClient;
import com.threecat.redis.jedis.clients.RedisConfig;
import com.threecat.redis.lock.DistributedLock;
import com.threecat.redis.lock.impl.RedisDistLock;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RedisClientTest
{
	public static void main(String[] args)
	{
		String lockKey = "DIST_LOCK";
		String requestId = lockKey + System.currentTimeMillis();
		int expireTime = 1000;

		ExecutorService threadPool = Executors.newFixedThreadPool(3);

		for (int i = 0; i < 5; i++)
		{
			threadPool.submit(() -> {
				Jedis jedis = RedisClient.getInstance().getJedis();
				DistributedLock lock = new RedisDistLock(lockKey, lockKey + ":" + System.currentTimeMillis(),
						expireTime, jedis);
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
		}
	}
}
