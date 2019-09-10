package com.threecat.redis.task.pool;

import com.threecat.redis.task.Task;
import com.threecat.redis.util.NumberUtils;
import com.threecat.redis.util.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;
import java.util.concurrent.*;

/**
 * TODO 实现步骤分发转换，step与handler之间的比较
 */
@Component
public class TaskThreadPool
{
	private Logger logger = LoggerFactory.getLogger(TaskThreadPool.class);

	private TaskThreadPoolConfig poolConfig = new TaskThreadPoolConfig();

	private ThreadPoolExecutor executor = initTaskPool();

	private class TaskThreadPoolConfig
	{
		/**
		 * 是否是IO密集型CPU
		 */
		private boolean isIOInsentiveCPU;

		private int corePoolSize;

		private int maxPoolSize;

		private long keepAliveTime;

		private int blockingQueueSize;

		public TaskThreadPoolConfig()
		{
			super();
			loadConfig();
		}

		private void loadConfig()
		{
			String configPath = System.getProperty("user.dir") + File.separator + "config" + File.separator
					+ "thread-pool-config.properties";
			Map<String, String> threadPoolConfigs = PropertiesUtils.loadMapProperties(configPath, false);
			isIOInsentiveCPU = NumberUtils.parseBoolean("thread.pool.isIOInsentiveCPU", false);
			corePoolSize = NumberUtils.parseInt("thread.pool.corePoolSize", calculateCorePoolSize());
			maxPoolSize = NumberUtils.parseInt("thread.pool.maxPoolSize", corePoolSize * 2);
			if (maxPoolSize < corePoolSize)
			{
				maxPoolSize = corePoolSize * 2;
			}
			keepAliveTime = NumberUtils.parseLong("thread.pool.keepAliveTime", 0l);
			blockingQueueSize = NumberUtils.parseInt("thread.pool.blockingQueueSize", 0);
		}
	}

	/**
	 * 初始化任务线程池
	 */
	private ThreadPoolExecutor initTaskPool()
	{
		int corePoolSize = poolConfig.corePoolSize;
		int maxPoolSize = poolConfig.maxPoolSize;
		int queueSize = poolConfig.blockingQueueSize;
		long keepAliveTime = poolConfig.keepAliveTime;
		return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<Runnable>(queueSize), new ThreadPoolExecutor.AbortPolicy());
	}

	/**
	 * 如果活跃线程数大于核心线程数，说明任务提交数已经超出了一次阻塞队列的长度，启用了最大线程，线程池比较繁忙。
	 * @return
	 */
	public boolean checkTaskPoolIsFree()
	{
		// TODO这里还是要仔细思考下
		return executor.getActiveCount() < executor.getCorePoolSize();
	}

	/**
	 * 计算线程池核心线程数，IO密集型cpu个数*2，否则就CPU个数个线程
	 *
	 * @return
	 */
	public int calculateCorePoolSize()
	{
		int cpuCounts = Runtime.getRuntime().availableProcessors();
		return poolConfig.isIOInsentiveCPU ? cpuCounts * 2 : cpuCounts;
	}

	public boolean addTask(Task task)
	{
		if (task == null)
		{
			logger.error("Null task!");
			return false;
		}
		try
		{
			executor.submit(task);
			return true;
		}
		catch (RejectedExecutionException e)
		{
			logger.error("Add task to task thread pool failed: " + e);
		}
		return false;
	}

}
