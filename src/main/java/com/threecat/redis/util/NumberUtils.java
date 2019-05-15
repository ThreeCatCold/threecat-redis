package com.threecat.redis.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberUtils
{
	private static Logger logger = LoggerFactory.getLogger(NumberUtils.class);

	public static int parseInt(String str, int defaultValue)
	{
		int result = 0;
		try
		{
			result = Integer.parseInt(str);
		}
		catch (NumberFormatException e)
		{
			logger.error("Parse {} to int error.", str);
			result = defaultValue;
		}
		return result;
	}

	public static long parseLong(String str, long defaultValue)
	{
		long result = 0l;
		try
		{
			result = Long.parseLong(str);
		}
		catch (NumberFormatException e)
		{
			logger.error("Parse {} to long error.", str);
			result = defaultValue;
		}
		return result;
	}

	public static boolean parseBoolean(String str, boolean defaultValue)
	{
		boolean result = defaultValue;
		try
		{
			result = Boolean.parseBoolean(str);
		}
		catch (NumberFormatException e)
		{
			logger.error("Parse {} to boolean error.", str);
		}
		return result;
	}

}
