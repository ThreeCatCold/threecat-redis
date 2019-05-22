package com.threecat.redis.task.pool;

import com.threecat.redis.jedis.clients.RedisClient;
import com.threecat.redis.lock.DistributedLock;
import com.threecat.redis.lock.impl.RedisDistLock;
import com.threecat.redis.util.CommonUtils;
import com.threecat.redis.util.ThreadUtils;
import com.threecat.redis.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RedisTaskQueueUtil
{
	private static Logger logger = LoggerFactory.getLogger(RedisTaskQueueUtil.class);

	/**
	 * 定时任务线程池
	 */
	private static ScheduledExecutorService timerTaskPool;

	/**
	 * 抓取任务的锁名
	 */
	private static final String FETCH_TASK_LOCK = "FETCH_LOCK";

	/**
	 * 提交任务锁名
	 */
	private static final String SUBMIT_TASK_LOCK = "SUBMIT_LOCK";

	/**
	 * 任务队列的名称
	 */
	private static final String TASK_QUEUE_NAME = "SERVICE_TASK_QUEUE";

	/**
	 * 过期时间（ms）
	 */
	private static final int LOCK_EXPIRE_TIME_MS = 10 * 1000;

	/**
	 * 周期任务休息时间
	 */
	private static final int SCHEDULED_TASK_SLEEP = 50;

	/**
	 * pop list过期时间
	 */
	private static final int POP_TASK_TIMEOUT = 1000;

	/**
	 * 队列最大不超过100个
	 */
	private static final int TASK_QUEUE_MAX_SIZE = 10;

	/**
	 * redis客户端
	 */
	private static RedisClient redisClient = RedisClient.getInstance();

	/**
	 * 触发任务
	 */
	public static void triggerTask()
	{
		logger.info("Prepare fetch service task.");
		timerTaskPool = Executors.newSingleThreadScheduledExecutor();
		TimerTask timerTask = new TimerTask()
		{
			@Override public void run()
			{
				fetchTask();
			}
		};
		// 延迟一秒后执行，每3秒执行一次
		timerTaskPool.scheduleAtFixedRate(timerTask, 1000, 1000, TimeUnit.MILLISECONDS);
	}

	/**
	 * 获取任务
	 */
	private static void fetchTask()
	{
		// 请求ID使用uuid保持唯一性
		String requestId = UUIDUtil.uuid();
		DistributedLock taskLock = new RedisDistLock(FETCH_TASK_LOCK, requestId, LOCK_EXPIRE_TIME_MS, redisClient);

		// 抢锁成功，获取抓取任务许可
		if (taskLock.tryLock())
		{
			try
			{
				// 如果任务线程池尚有空余，则抓取任务
				if (TaskUtil.checkTaskPoolIsFree())
				{
					Class[] paramTypes = { int.class, String.class };
					Object[] params = { POP_TASK_TIMEOUT, TASK_QUEUE_NAME };
					// 将list最右侧任务弹出
					List<String> taskList = redisClient.executeCommand("brpop", paramTypes, params);
					// 已经取出任务就可以释放锁了
					taskLock.unlock();
					String task = parseSimpleTask(taskList);

					// TODO 将任务提交到任务队列线程池，或者队列
					if (!CommonUtils.isEmptyObject(task))
					{
						// 休息0.5s，假装提交任务至任务线程池
						ThreadUtils.sleep(500);
						logger.info("Pop and execute task {} success.", task);
					}

					// 休息500ms
					ThreadUtils.sleep(50);
				}
			}
			finally
			{
				taskLock.unlock();
			}
		}
	}

	/**
	 * 解析出单个task
	 * @param taskList
	 * @return
	 */
	private static String parseSimpleTask(List<String> taskList)
	{
		if (!CommonUtils.isEmptyObject(taskList))
		{
			List<String> tasks = (List<String>)taskList;
			if (tasks.size() >= 2)
			{
				// 第一个元素是队列名称，第二个元素是pop的list的第一个元素
				return tasks.get(1);
			}
		}
		return null;
	}

	public static boolean submitTask(String taskJsonStr)
	{
		logger.debug("start submit task");
		boolean success = false;

		// 请求ID使用uuid保持唯一性
		String requestId = UUIDUtil.uuid();
		DistributedLock taskLock = new RedisDistLock(SUBMIT_TASK_LOCK, requestId, LOCK_EXPIRE_TIME_MS, redisClient);

		if (taskLock.tryLock())
		{
			try
			{
				// 查看任务队列是否已达上限
				Class[] paramTypes = {String.class};
				Object[] params = {TASK_QUEUE_NAME};
				if (isTaskQueueFree(redisClient.executeCommand("llen", paramTypes, params)))
				{
					paramTypes = new Class[]{String.class, String[].class};
					params = new Object[]{TASK_QUEUE_NAME, new String[]{taskJsonStr}};

					long result = redisClient.executeCommand("lpush", paramTypes, params);
					// 提交成功
					if (result >= 1l)
					{
						logger.info("submit task success: " + taskJsonStr);
						success = true;
					}
					else
					{
						logger.info("submit task failed, result: " + result);
						success = false;
					}
				}
				else
				{
					logger.info("submit task failed: task queue is full. Please wait for a while to submit.");
					success = false;
				}
			}
			finally
			{
				taskLock.unlock();
			}
		}

		return success;
	}

	/**
	 * 判断任务队列排队是否达到上限个数
	 * @param queueSize
	 * @return
	 */
	public static boolean isTaskQueueFree(long queueSize)
	{
		return queueSize < TASK_QUEUE_MAX_SIZE;
	}
}
