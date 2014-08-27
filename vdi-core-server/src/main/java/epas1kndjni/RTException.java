// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RTException.java

package epas1kndjni;


public class RTException extends RuntimeException
{

	private int retval;

	public int HResult()
	{
		if (retval == 0)
		{
			int i = getMessage().lastIndexOf("0x");
			if (i != -1)
			{
				String s = getMessage().substring(i + 6);
				retval = Integer.parseInt(s, 16);
			}
		}
		return retval;
	}

	public RTException()
	{
		retval = 0;
	}

	public RTException(int i)
	{
		retval = i;
	}

	public RTException(int i, String s)
	{
		super(s);
		retval = i;
	}

	public RTException(String s)
	{
		super(s);
		retval = 0;
	}
}
