package com.threecat.redis;

import com.threecat.redis.task.pool.RedisTaskQueueUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisPracticeApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(RedisPracticeApplication.class, args);
		RedisTaskQueueUtil.triggerTask();
	}
}
