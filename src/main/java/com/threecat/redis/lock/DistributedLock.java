package com.threecat.redis.lock;

/**
 * 简单分布式互斥锁接口
 */
public interface DistributedLock
{
	/**
	 *
	 * @param lockKey 锁名称
	 * @param expireTime 过期时间
	 * @return
	 */
	boolean tryLock();

	/**
	 * 释放锁
	 */
	void unlock();

	// TODO, other undefined lock method
}
