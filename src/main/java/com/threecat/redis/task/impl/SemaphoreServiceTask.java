package com.threecat.redis.task.impl;

import com.threecat.redis.task.abs.SemaphoreTask;
import com.threecat.redis.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

public class SemaphoreServiceTask extends SemaphoreTask
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public SemaphoreServiceTask(Semaphore semaphore)
	{
		super(semaphore);
	}

	@Override 
	public void doTask()
	{
		logger.info("[SimpleServiceTask]: start do task.");
		ThreadUtils.sleep(5000);
		logger.info("[SimpleServiceTask]: do task finished.");
	}
}
