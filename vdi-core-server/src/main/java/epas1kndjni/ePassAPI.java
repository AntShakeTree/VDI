// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ePassAPI.java

package epas1kndjni;


// Referenced classes of package epas1kndjni:
//			RTException, WFileInfo, WDirInfo, WAccessInfo, 
//			WVersionInfo, WSysInfo
import epas1kndjni.*;
public class ePassAPI
{

	private int hCtx;
	private int bInit;

	public void finalize()
	{
		if (bInit == 1)
		{
			bInit = 0;
//			FreeLibrary();
		}
	}

	public void Thrown1(int i, String s)
	{
		throw new RTException(i, s);
	}

	public void Thrown2(int i)
	{
		throw new RTException(i);
	}

	private final native void LoadLibrary();

	private final native void FreeLibrary();

	public native void CreateContext(int i, int j);

	public native void DeleteContext();

	public native void OpenDevice(int i, byte abyte0[]);

	public native void CloseDevice();

	public native void CreateFile(int i, WFileInfo wfileinfo);

	public native void DeleteFile(int i, int j);

	public native void OpenFile(int i, int j, WFileInfo wfileinfo);

	public native void CloseFile();

	public native void GetFileInfo(int i, int j, byte abyte0[], WFileInfo wfileinfo);

	public native void Read(int i, int j, byte abyte0[], int k, int ai[]);

	public native void Write(int i, int j, byte abyte0[], int k, int ai[]);

	public native void CreateDir(int i, byte abyte0[], byte abyte1[], WDirInfo wdirinfo);

	public native void DeleteDir(int i, int j, byte abyte0[]);

	public native void GetCurrentDir(int i, int ai[], int j, int ai1[]);

	public native void ChangeDir(int i, int j, byte abyte0[]);

//	public native void Directory(int i, int j, WFileInfo wfileinfo);

	public native void GenRandom(int i, byte abyte0[], byte abyte1[], int j);

	public native void HashToken(int i, int j, byte abyte0[], int k, byte abyte1[], int l, int ai[]);

	public native void MD5HMAC(int i, int j, byte abyte0[], int k, byte abyte1[]);

	public native void Verify(int i, byte abyte0[], int j);

	public native void ChangeCode(int i, byte abyte0[], int j, byte abyte1[], int k);

	public native void ResetSecurityState(int i);

	public native void GetProperty(int i, byte abyte0[], byte abyte1[], int j);

	public native void SetProperty(int i, byte abyte0[], byte abyte1[], int j);

	public native void GetAccessInfo(int i, WAccessInfo waccessinfo);

	public native void SetAccessInfo(int i, WAccessInfo waccessinfo);

	public native void GetVersionInfo(WVersionInfo wversioninfo);

	public native void GetSysInfo(WSysInfo wsysinfo);

	public native void Cleanup(int i);

	public native int GetLibVersion();

	public native int GetDriverVersion();

	public native void MD5_Hash(byte abyte0[], int i, byte abyte1[]);

	public native void MD5_HMAC(byte abyte0[], int i, byte abyte1[], int j, byte abyte2[], byte abyte3[], byte abyte4[]);

	public native void Tea(int i, int j, byte abyte0[], int k, byte abyte1[], int ai[]);

	public ePassAPI()
	{
		bInit = 0;
//		LoadLibrary();
	}

	static 
	{
//		System.out.println("stststststs");
//		System.setProperty("java.library.path", "/root/lib/jni");
//		System.out.println("asasasasasas");
		System.load("/root/lib/jni/libJePs1knd.so");
//		System.out.println("qwqwqwqwqwq");
	}
}
