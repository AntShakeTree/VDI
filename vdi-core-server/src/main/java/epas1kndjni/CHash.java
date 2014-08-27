// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CHash.java

package epas1kndjni;


// Referenced classes of package epas1kndjni:
//			IHash, ePassAPI
import epas1kndjni.*;
public class CHash
	implements IHash
{

	private ePassAPI ePass;

	public void MD5_Hash(byte abyte0[], int i, byte abyte1[])
	{
		ePass.MD5_Hash(abyte0, i, abyte1);
	}

	public void MD5_HMAC(byte abyte0[], int i, byte abyte1[], int j, byte abyte2[], byte abyte3[], byte abyte4[])
	{
		ePass.MD5_HMAC(abyte0, i, abyte1, j, abyte2, abyte3, abyte4);
	}

	public CHash()
	{
		ePass = new ePassAPI();
	}
}
