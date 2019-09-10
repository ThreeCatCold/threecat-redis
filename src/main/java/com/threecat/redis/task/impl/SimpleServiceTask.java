package com.threecat.redis.task.impl;

import com.threecat.redis.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SimpleServiceTask extends Task
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String taskId;

	private int taskStep;

	private Object target;

	private String methodName;

	private Class[] paramTypes;

	private Object[] params;

	public SimpleServiceTask(Object target, String methodName, Class[] paramTypes, Object[] params)
	{
		this.target = target;
		this.methodName = methodName;
		this.paramTypes = paramTypes;
		this.params = params;
	}

	@Override public void doTask()
	{
		try
		{
			Method serviceMethod = target.getClass().getMethod(methodName, paramTypes);
			serviceMethod.invoke(target, params);
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
		{
			logger.error("[ServiceTask]: execute method failed, service class: {} , method: {}", target.getClass(),
					methodName);
		}
	}

	@Override public void run()
	{
		doTask();
	}
}
