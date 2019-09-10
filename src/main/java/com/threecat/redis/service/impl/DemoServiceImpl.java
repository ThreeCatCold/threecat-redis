package com.threecat.redis.service.impl;

import com.threecat.redis.config.SpringBeanFactory;
import com.threecat.redis.dto.Request;
import com.threecat.redis.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl implements DemoService
{
	@Autowired
	private SpringBeanFactory beanFactory;

	@Override public void doService(Request request)
	{
		String requestParam = request.getRequestParam();
		// TODO do task
	}
}
