package com.threecat.redis.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * 读取properties工具类
 */
public class PropertiesUtils
{
	private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

	/**
	 * 加载配置文件
	 * @param path 路径
	 * @param isResourcePath 是否是resource路径
	 * @return
	 */
	public static Map<String, String> loadMapProperties(String path, boolean isResourcePath)
	{
		Map<String, String> configs = new HashMap<String, String>();
		Properties properties = loadProperties(path, isResourcePath);

		for (Iterator<String> element = properties.stringPropertyNames().iterator(); element.hasNext();)
		{
			String key = element.next();
			String value = properties.getProperty(key);
			configs.put(key, value);
		}
		return configs;
	}

	/**
	 * 加载配置文件
	 * @param path 路径
	 * @param isResourcePath 是否是resource路径
	 * @return
	 */
	public static Properties loadProperties(String path, boolean isResourcePath)
	{
		return isResourcePath ? loadResourceProperties(path) : loadSystemProperties(path);
	}


	/**
	 * 读取resource目录下的配置文件
	 * @param resourcePath
	 * @return
	 */
	private static Properties loadResourceProperties(String resourcePath)
	{
		Properties properties = new Properties();
		InputStream inputStream = null;
		try
		{
			inputStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(resourcePath);
			properties.load(inputStream);
		}
		catch (IOException e)
		{
			logger.error("Load resource properties error: " + e.getMessage());
		}
		finally
		{
			IOUtils.closeStream(inputStream);
		}
		return properties;
	}

	/**
	 * 读取系统路径下的配置文件
	 * @param systemPath
	 * @return
	 */
	private static Properties loadSystemProperties(String systemPath)
	{
		Properties properties = new Properties();
		InputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(systemPath);
			properties.load(inputStream);
		}
		catch (IOException e)
		{
			logger.error("Load system properties error: " + e.getMessage());
		}
		finally
		{
			IOUtils.closeStream(inputStream);
		}
		return properties;
	}


}
