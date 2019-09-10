package com.threecat.redis.task.pool;

import com.threecat.redis.task.Task;
import com.threecat.redis.util.NumberUtils;
import com.threecat.redis.util.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务管理类
 */
@Component
public class TaskManager
{
	private static final String CONFIG_PATH =
			System.getProperty("user.dir") + File.separator + "config" + File.separator
					+ "thread-pool-config.properties";

	private Logger logger = LoggerFactory.getLogger(TaskManager.class);

	private TaskThreadPoolConfig poolConfig;

	private ThreadPoolExecutor executor;

	public TaskManager()
	{
		super();
		poolConfig = new TaskThreadPoolConfig();
		initTaskPool();
	}

	/**
	 * 线程池配置类，方便的话可以单独写个类出来
	 */
	private class TaskThreadPoolConfig
	{
		/**
		 * 核心线程计算参数，具体解释请参考配置文件
		 */
		private int corePoolSizeParam;

		/**
		 * 核心线程数
		 */
		private int corePoolSize;

		/**
		 * 最大线程数
		 */
		private int maxPoolSize;

		/**
		 * 超过核心线程的多余线程的最大保留时间，单位ms
		 */
		private long keepAliveTime;

		/**
		 * 阻塞队列大小
		 */
		private int blockingQueueSize;

		/**
		 * 任务名称
		 */
		private String taskName;

		public TaskThreadPoolConfig()
		{
			super();
			loadConfig();
		}

		private void loadConfig()
		{
			// 导入配置完全可用springboot的yaml文件或者properties导入，这里偷懒用自己的propertyUtil工具
			Map<String, String> threadPoolConfigs = PropertiesUtils.loadMapProperties(CONFIG_PATH, false);
			corePoolSizeParam = NumberUtils.parseInt(threadPoolConfigs.get("thread.pool.corePoolSizeParam"), 0);
			corePoolSize = NumberUtils
					.parseInt(threadPoolConfigs.get("thread.pool.corePoolSize"), calCorePoolSize(corePoolSizeParam));
			// 校验一下核心线程池数据合法性
			corePoolSize = corePoolSize <= 0 ? calCorePoolSize(corePoolSizeParam) : corePoolSize;
			maxPoolSize = NumberUtils.parseInt(threadPoolConfigs.get("thread.pool.maxPoolSize"), corePoolSize);
			// 校验一下最大线程池数据合法性
			maxPoolSize = maxPoolSize < corePoolSize ? corePoolSize : maxPoolSize;
			keepAliveTime = NumberUtils.parseLong(threadPoolConfigs.get("thread.pool.keepAliveTime"), 0l);
			blockingQueueSize = NumberUtils.parseInt(threadPoolConfigs.get("thread.pool.blockingQueueSize"), 10);
			taskName = threadPoolConfigs.get("thread.pool.taskName");
		}

		/**
		 * 计算线程池核心线程数，IO密集型cpu个数*2，否则就CPU个数个线程就行
		 * 一般来说公式为：（线程等待时间/线程CPU计算时间+1)*CPU核数
		 *
		 * @return
		 */
		private int calCorePoolSize(int corePoolSizeParam)
		{
			int cpuCounts = Runtime.getRuntime().availableProcessors();
			return cpuCounts * (1 + corePoolSizeParam);
		}

	}

	/**
	 * 初始化任务线程池
	 */
	private void initTaskPool()
	{
		int corePoolSize = poolConfig.corePoolSize;
		int maxPoolSize = poolConfig.maxPoolSize;
		int queueSize = poolConfig.blockingQueueSize;
		long keepAliveTime = poolConfig.keepAliveTime;
		executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<>(queueSize), new CustomThreadFactory(),
				new BlockingTaskRejectedExecutionHandler());
	}

	/**
	 * 自定义线程工厂，没啥用可以删求
	 */
	private class CustomThreadFactory implements ThreadFactory
	{
		private AtomicInteger count = new AtomicInteger(0);

		@Override
		public Thread newThread(Runnable r)
		{
			Thread t = new Thread(r);
			t.setName(poolConfig.taskName + count.addAndGet(1));
			t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
			{
				@Override
				public void uncaughtException(Thread t, Throwable e)
				{
					logger.error("Unknown error in task thread {}: {}", t.getName(), e);
				}
			});
			return t;
		}
	}

	/**
	 * 自定义线程池拒绝策略，当提交的任务数超过最大线程数+阻塞队列长度时，使用阻塞提交线程，避免任务丢失
	 */
	private class BlockingTaskRejectedExecutionHandler implements RejectedExecutionHandler
	{
		@Override public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
		{
			try
			{
				executor.getQueue().put(r);
			}
			catch (InterruptedException e)
			{
				logger.error("Submit blocking task error:" + e);
			}
		}
	}

	/**
	 * 添加任务
	 *
	 * @param task
	 * @return
	 */
	public boolean submitTask(final String task)
	{
		if (task == null)
		{
			logger.error("Null task!");
			return false;
		}
		executor.submit(() -> {
			// 具体执行线程的方法
			System.out.println("to execute task:" + task);
			try
			{
				Thread.sleep(2000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		});
		return true;
	}

	public void submitTask(List<String> tasks)
	{
		for (String task : tasks)
		{
			submitTask(task);
		}
	}

	/**
	 * 判断阻塞队列线程个数是否已经达到了阻塞队列上限，是则说明线程池繁忙
	 *
	 * @return
	 */
	public boolean isTaskPoolBusy()
	{
		return executor.getQueue().size() == poolConfig.blockingQueueSize;
	}

	/**
	 * 测试类
	 * @param args
	 */
	public static void main(String[] args)
	{
		TaskManager taskManager = new TaskManager();
		// 测试线程池是否繁忙的方法
		new Thread(()->{
			while (true)
			{
				System.out.println("taskManager is busy? " + taskManager.isTaskPoolBusy());
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}).start();
		for (int i = 0; i < 30; i++)
		{
			String taskName = "task" + (i + 1);
			taskManager.submitTask(taskName);
			System.out.println("submit task success:" + taskName);
		}
	}
}
