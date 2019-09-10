package com.threecat.redis.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanFactory
{
	@Autowired
	private BeanFactory beanFactory;

	public <T> T getBean(String beanName)
	{
		return (T)beanFactory.getBean(beanName);
	}

	public <T> T getBean(Class<T> requiredType)
	{
		return beanFactory.getBean(requiredType);
	}

	public <T> T getBean(String beanName, Class<T> requiredType)
	{
		return beanFactory.getBean(beanName, requiredType);
	}

}
