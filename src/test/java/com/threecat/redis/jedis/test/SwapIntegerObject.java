package com.threecat.redis.jedis.test;

import java.lang.reflect.Field;

public class SwapIntegerObject
{
	public static void main(String[] args)
	{
		int i = -1;
		System.out.println(i++ + ++i);
//		Integer i1 = 1, i2 = 2;
//		System.out.println("before: i1 = " + i1 + ", i2 = " + i2);
//		swap(i1, i2);
//		System.out.println("before: i1 = " + i1 + ", i2 = " + i2);
//		System.out.println(Integer.valueOf(1));
	}

	/**
	 * Java只有值传递，只能用反射解决
	 * 这种解法会毁掉底层IntegerCache中原来的顺序，因为调用反射终究会改掉IntegerCache底层数组的值。
	 * @param i1
	 * @param i2
	 */
	public static void swap(Integer i1, Integer i2)
	{
		try
		{
			// 获取实际存储值得字段
			Field field = Integer.class.getDeclaredField("value");
			// 设置访问权限
			field.setAccessible(true);
			// 中间缓存，此处一定不要使用int，使用int类型在自动装箱时，数字若在-128-127之间，会直接取
			// IntegerCache数组中的值（通过计算数组下标索引获得），会使结果不正确。
			Integer temp = new Integer(i1.intValue());
			field.set(i1, i2.intValue());
			field.set(i2, temp);
		}
		catch (Exception e)
		{
		}
	}
}
