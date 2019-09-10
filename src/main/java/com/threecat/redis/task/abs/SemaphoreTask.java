package com.threecat.redis.task.abs;

import com.threecat.redis.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

/**
 * 带有信号量许可的任务
 */
public abstract class SemaphoreTask extends Task
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Semaphore semaphore;

	public SemaphoreTask(Semaphore semaphore)
	{
		this.semaphore = semaphore;
	}

	@Override
	public void run()
	{
		try
		{
			semaphore.acquire();
			doTask();
			semaphore.release();
		}
		catch (InterruptedException e)
		{
			logger.error("Semaphore Task execute error:" + e.getMessage());
		}
	}
}
