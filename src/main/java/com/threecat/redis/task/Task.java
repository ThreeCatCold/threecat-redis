package com.threecat.redis.task;

public abstract class Task implements Runnable
{
	public abstract void doTask();
}
