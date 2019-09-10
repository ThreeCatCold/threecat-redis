package com.threecat.redis.service;

public interface ServiceProcessor<T> extends Runnable
{
	void process(T request);

	void shutdown();
}
