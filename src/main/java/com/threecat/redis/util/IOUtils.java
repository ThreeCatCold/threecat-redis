package com.threecat.redis.util;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils
{
	public static void closeStream(Closeable stream)
	{
		if (stream != null)
		{
			try
			{
				stream.close();
			}
			catch (IOException e)
			{
				// do nothing
			}
		}
	}

}
