package com.threecat.redis.queue;

/**
 * 简单任务队列接口，没有考虑是否阻塞
 */
public interface TaskQueue
{
	void add(String element);

	void poll();
}
