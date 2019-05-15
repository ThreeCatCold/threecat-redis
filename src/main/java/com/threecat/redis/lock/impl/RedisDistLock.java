package com.threecat.redis.lock.impl;

import com.threecat.redis.jedis.clients.RedisClient;
import com.threecat.redis.lock.DistributedLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Collections;

/**
 * redis分布式锁实现
 * 参考博客：https://www.cnblogs.com/linjiqin/p/8003838.html
 *
 */
public class RedisDistLock implements DistributedLock
{
	private Logger logger = LoggerFactory.getLogger(RedisDistLock.class);

	/**
	 * 锁成功返回内容
	 */
	public static final String LOCK_SUCCESS = "OK";

	/**
	 * set命令模式，只有当该key不存在时，才能set成功，否则不允许set key。
	 */
	public static final String SET_IF_NOT_EXIST = "NX";

	/**
	 * 为该key设置过期时间的模式
	 */
	public static final String SET_WITH_EXPIRE_TIME = "PX";

	/**
	 * 释放锁（即删除该key）的lua脚本，在redis侧执行为原子操作
	 */
	public static final String LUA_UNLOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

	/**
	 * 释放成功返回值
	 */
	public static final Long UNLOCK_SUCCESS = 1L;
	/**
	 * 锁名称
	 */
	private String lockKey;

	/**
	 * 锁请求id
	 */
	private String requestId;

	/**
	 * 过期时间，单位（ms）
	 */
	private int expireTime;

	private Jedis jedis;

	public RedisDistLock(String lockKey, int expireTime, Jedis jedis)
	{
		this.lockKey = lockKey;
		this.requestId = lockKey;
		this.expireTime = expireTime;
		this.jedis = jedis;
	}

	public RedisDistLock(String lockKey, String requestId, int expireTime, Jedis jedis)
	{
		this.lockKey = lockKey;
		this.requestId = requestId;
		this.expireTime = expireTime;
		this.jedis = jedis;
	}

	/**
	 * 尝试获取锁，实现思路：
	 * 使用带NX模式的set命令设置一个字符串key作为分布式锁，set成功说明该客户端抢到了这把锁（即抢到了执行权），
	 * 其他客户端设置不成功，说明该key已存在，已经有客户端拿到了这把锁（抢到了执行权），只能轮询等待再次tryLock尝试获取锁。
	 * @return
	 */
	@Override public boolean tryLock()
	{
		try
		{
			String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
			if (LOCK_SUCCESS.equals(result))
			{
				logger.info("RedisDistLock lock \"{}\" success.", lockKey);
				return true;
			}
		}
		catch (Exception e)
		{
			logger.error("Unexpected error in redisDistLock tryLock: " + e);
		}
		return false;
	}

	/**
	 * 释放锁：
	 * 释放锁的时候只需要判断请求的id（这个id最好是全局唯一id）与lockKey对应的value是否相等，相等则证明是上这把锁的客户端
	 * 要释放这把锁，否则其他客户端也可以执行unlock而把这把锁释放掉，这就存在了不安全性。
	 *
	 * 同时需要非常注意的一点是，判断id相同和删除key，是两个操作，如果直接用exists和del命令组合，或者在判断该锁是否为该客户端后，
	 * 突然这把锁不是属于该客户端了，这时这边执行del操作也会导致误释放锁。
	 *
	 * 故判断锁是否为该客户端上的锁以及删除这个key必须为原子操作，这样才能保证较高的安全性.
	 * LUA脚本命令是原子操作的，故使用lua脚本释放锁。
	 */
	@Override public void unlock()
	{
		try
		{
			Object result = jedis
					.eval(LUA_UNLOCK_SCRIPT, Collections.singletonList(lockKey), Collections.singletonList(requestId));
			if (UNLOCK_SUCCESS.equals(result))
			{
				logger.info("RedisDistLock unlock \"{}\" success.", lockKey);
			}
		}
		catch (Exception e)
		{
			logger.error("Unexpected error in redisDistLock unlock: " + e.getMessage());
		}
	}
}
