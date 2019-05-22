package com.threecat.redis.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommonUtils
{
	public static boolean isEmptyString(String str)
	{
		return str == null || str.isEmpty();
	}

	public static boolean isEmptyObject(Object object)
	{
		if (object == null)
		{
			return true;
		}

		if (object instanceof String)
		{
			return isEmptyString((String)object);
		}

		if (object instanceof String[])
		{
			return ((String[])object).length == 0;
		}

		if (object instanceof List)
		{
			List list = (List)object;
			return list.size() == 0;
		}

		if (object instanceof Map)
		{
			Map map = (Map)object;
			return map.size() == 0;
		}

		if (object instanceof Set)
		{
			Set set = (Set)object;
			return set.size() == 0;
		}

		throw new RuntimeException("Unknown type object:" + object);
	}

}
