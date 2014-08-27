// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IHash.java

package epas1kndjni;

public interface IHash
{

	public abstract void MD5_Hash(byte abyte0[], int i, byte abyte1[]);

	public abstract void MD5_HMAC(byte abyte0[], int i, byte abyte1[], int j, byte abyte2[], byte abyte3[], byte abyte4[]);
}
