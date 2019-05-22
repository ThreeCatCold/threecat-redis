package com.threecat.redis.jedis.test;

import com.threecat.redis.util.ThreadUtils;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * Java 强、软、弱、虚4个引用的学习范例
 */
public class JavaReference
{
	public void testSoftReference()
	{
		Object softRefObject = new Object();
		// 传入软引用对象
		SoftReference<Object> softReference = new SoftReference<>(softRefObject);
		softRefObject = null;
		System.gc();
		// 获取软引用对象，结果不为空
		System.out.println(softReference.get());
	}

	public void testWeakReference()
	{
		Object weakRefObject = new Object();
		// 传入虚引用对象
		WeakReference<Object> weakReference = new WeakReference<>(weakRefObject);
		weakRefObject = null;
		System.gc();
		// 获取虚引用对象，结果为空
		System.out.println(weakReference.get());
	}

	public void testPhantomReference()
	{
		ReferenceQueue<Object> queue = new ReferenceQueue<>();
		Object phantomRefObject = new Object();
		PhantomReference<Object> phantomReference = new PhantomReference<>(phantomRefObject, queue);
		// 永远为null
		System.out.println(phantomReference.get());

		phantomRefObject = null;
		System.gc();
		// null
		System.out.println(phantomReference.get());
		ThreadUtils.sleep(200);
		// java.lang.ref.PhantomReference对象存在
		System.out.println(queue.poll());
	}

	public static void main(String[] args)
	{
		JavaReference reference = new JavaReference();
		reference.testSoftReference();
		reference.testWeakReference();
		reference.testPhantomReference();
	}
}
