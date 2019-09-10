package com.threecat.redis.service.impl;

import com.threecat.redis.service.ServiceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 异步化处理业务链
 * @param <T>
 */
public class ChainServiceProcessor<T> implements ServiceProcessor<T>
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 请求队列
	 */
	private LinkedBlockingQueue<T> requestQueue = new LinkedBlockingQueue<>();

	/**
	 * 下一个processor
	 */
	private ServiceProcessor nextProcessor;

	/**
	 * 任务标识符
	 */
	private volatile boolean isFinished = false;

	public ChainServiceProcessor(ServiceProcessor nextProcessor)
	{
		this.nextProcessor = nextProcessor;
	}

	/**
	 * 提交请求至队列
	 * @param request
	 */
	@Override public void process(T request)
	{
		requestQueue.add(request);
	}

	@Override public void run()
	{
		while(!isFinished)
		{
			try
			{
				T request = requestQueue.take();
				// TODO 处理业务逻辑


				// 下一个processor处理，实质也是提交到阻塞队列中
				nextProcessor.process(request);
			}
			catch (InterruptedException e)
			{
				logger.error("Process request error:" + e);
			}

		}
	}

	@Override
	public void shutdown()
	{
		this.isFinished = true;
	}

}
