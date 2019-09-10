package com.threecat.redis.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 本地内存缓存工具类，本应该用concurrentHashMap，这里练习下读写锁就用hashMap，还有软引用避免内存溢出。
 */
public class MemoryCacheUtils
{
	private static Map<String, SoftReference> cache = new HashMap<>();
	private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private static Lock readLock = readWriteLock.readLock();
	private static Lock writeLock = readWriteLock.writeLock();

	public static <T> void set(String key, T value)
	{
		writeLock.lock();
		try
		{
			cache.put(key, new SoftReference<T>(value));
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public static <T> T get(String key)
	{
		readLock.lock();
		try
		{
			SoftReference<T> result = cache.get(key);
			return result == null? null : result.get();
		}
		finally
		{
			readLock.unlock();
		}
	}

	public static void delete(String key)
	{
		writeLock.lock();
		try
		{
			cache.remove(key);
		}
		finally
		{
			writeLock.unlock();
		}
	}
}
