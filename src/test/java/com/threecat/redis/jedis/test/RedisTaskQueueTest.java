package com.threecat.redis.jedis.test;

import com.threecat.redis.task.pool.RedisTaskQueueUtil;
import com.threecat.redis.util.ThreadUtils;
import org.junit.Test;

public class RedisTaskQueueTest
{
	/**
	 * 提交任务的范例
	 */
	@Test public void testSubmitTaskSync()
	{
		int taskNum = 100;

		for (int i = 0; i < taskNum; i++)
		{
			String task = "task-msg:" + (i + 1);
			RedisTaskQueueUtil.submitTask(task);
			ThreadUtils.sleep(1000);
		}

		ThreadUtils.sleep(Integer.MAX_VALUE);
	}
}
