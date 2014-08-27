// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IePass.java

package epas1kndjni;


// Referenced classes of package epas1kndjni:
//			WFileInfo, WDirInfo, WAccessInfo, WVersionInfo, 
//			WSysInfo
import epas1kndjni.*;
public interface IePass
{

	public abstract void CreateContext(int i, int j);

	public abstract void DeleteContext();

	public abstract void OpenDevice(int i, byte abyte0[]);

	public abstract void CloseDevice();

	public abstract void CreateFile(int i, WFileInfo wfileinfo);

	public abstract void DeleteFile(int i, int j);

	public abstract void OpenFile(int i, int j, WFileInfo wfileinfo);

	public abstract void CloseFile();

	public abstract void GetFileInfo(int i, int j, byte abyte0[], WFileInfo wfileinfo);

	public abstract void Read(int i, int j, byte abyte0[], int k, int ai[]);

	public abstract void Write(int i, int j, byte abyte0[], int k, int ai[]);

	public abstract void CreateDir(int i, byte abyte0[], byte abyte1[], WDirInfo wdirinfo);

	public abstract void DeleteDir(int i, int j, byte abyte0[]);

	public abstract void GetCurrentDir(int i, int ai[], int j, int ai1[]);

	public abstract void ChangeDir(int i, int j, byte abyte0[]);

	public abstract void Directory(int i, int j, WFileInfo wfileinfo);

	public abstract void GenRandom(int i, byte abyte0[], byte abyte1[], int j);

	public abstract void HashToken(int i, int j, byte abyte0[], int k, byte abyte1[], int l, int ai[]);

	public abstract void MD5HMAC(int i, int j, byte abyte0[], int k, byte abyte1[]);

	public abstract void Verify(int i, byte abyte0[], int j);

	public abstract void ChangeCode(int i, byte abyte0[], int j, byte abyte1[], int k);

	public abstract void ResetSecurityState(int i);

	public abstract void GetProperty(int i, byte abyte0[], byte abyte1[], int j);

	public abstract void SetProperty(int i, byte abyte0[], byte abyte1[], int j);

	public abstract void GetAccessInfo(int i, WAccessInfo waccessinfo);

	public abstract void SetAccessInfo(int i, WAccessInfo waccessinfo);

	public abstract void GetVersionInfo(WVersionInfo wversioninfo);

	public abstract void GetSysInfo(WSysInfo wsysinfo);

	public abstract void Cleanup(int i);

	public abstract int GetLibVersion();

	public abstract int GetDriverVersion();

	public abstract void Tea(int i, int j, byte abyte0[], int k, byte abyte1[], int ai[]);
}
