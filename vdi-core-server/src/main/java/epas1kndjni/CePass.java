// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CePass.java

package epas1kndjni;


// Referenced classes of package epas1kndjni:
//			IePass, ePassAPI, WFileInfo, WDirInfo, 
//			WAccessInfo, WVersionInfo, WSysInfo
import epas1kndjni.*;
public class CePass
	implements IePass
{

	private ePassAPI ePass;

	public void CreateContext(int i, int j)
	{
		ePass.CreateContext(i, j);
	}

	public void DeleteContext()
	{
		ePass.DeleteContext();
	}

	public void OpenDevice(int i, byte abyte0[])
	{
		ePass.OpenDevice(i, abyte0);
	}

	public void CloseDevice()
	{
		ePass.CloseDevice();
	}

	public void CreateFile(int i, WFileInfo wfileinfo)
	{
		ePass.CreateFile(i, wfileinfo);
	}

	public void DeleteFile(int i, int j)
	{
		ePass.DeleteFile(i, j);
	}

	public void OpenFile(int i, int j, WFileInfo wfileinfo)
	{
		ePass.OpenFile(i, j, wfileinfo);
	}

	public void CloseFile()
	{
		ePass.CloseFile();
	}

	public void GetFileInfo(int i, int j, byte abyte0[], WFileInfo wfileinfo)
	{
		ePass.GetFileInfo(i, j, abyte0, wfileinfo);
	}

	public void Read(int i, int j, byte abyte0[], int k, int ai[])
	{
		ePass.Read(i, j, abyte0, k, ai);
	}

	public void Write(int i, int j, byte abyte0[], int k, int ai[])
	{
		ePass.Write(i, j, abyte0, k, ai);
	}

	public void CreateDir(int i, byte abyte0[], byte abyte1[], WDirInfo wdirinfo)
	{
		ePass.CreateDir(i, abyte0, abyte1, wdirinfo);
	}

	public void DeleteDir(int i, int j, byte abyte0[])
	{
		ePass.DeleteDir(i, j, abyte0);
	}

	public void GetCurrentDir(int i, int ai[], int j, int ai1[])
	{
		ePass.GetCurrentDir(i, ai, j, ai1);
	}

	public void ChangeDir(int i, int j, byte abyte0[])
	{
		ePass.ChangeDir(i, j, abyte0);
	}

	public void Directory(int i, int j, WFileInfo wfileinfo)
	{
//		ePass.Directory(i, j, wfileinfo);
	}

	public void GenRandom(int i, byte abyte0[], byte abyte1[], int j)
	{
		ePass.GenRandom(i, abyte0, abyte1, j);
	}

	public void HashToken(int i, int j, byte abyte0[], int k, byte abyte1[], int l, int ai[])
	{
		ePass.HashToken(i, j, abyte0, k, abyte1, l, ai);
	}

	public void MD5HMAC(int i, int j, byte abyte0[], int k, byte abyte1[])
	{
		ePass.MD5HMAC(i, j, abyte0, k, abyte1);
	}

	public void Verify(int i, byte abyte0[], int j)
	{
		ePass.Verify(i, abyte0, j);
	}

	public void ChangeCode(int i, byte abyte0[], int j, byte abyte1[], int k)
	{
		ePass.ChangeCode(i, abyte0, j, abyte1, k);
	}

	public void ResetSecurityState(int i)
	{
		ePass.ResetSecurityState(i);
	}

	public void GetProperty(int i, byte abyte0[], byte abyte1[], int j)
	{
		ePass.GetProperty(i, abyte0, abyte1, j);
	}

	public void SetProperty(int i, byte abyte0[], byte abyte1[], int j)
	{
		ePass.SetProperty(i, abyte0, abyte1, j);
	}

	public void GetAccessInfo(int i, WAccessInfo waccessinfo)
	{
		ePass.GetAccessInfo(i, waccessinfo);
	}

	public void SetAccessInfo(int i, WAccessInfo waccessinfo)
	{
		ePass.SetAccessInfo(i, waccessinfo);
	}

	public void GetVersionInfo(WVersionInfo wversioninfo)
	{
		ePass.GetVersionInfo(wversioninfo);
	}

	public void GetSysInfo(WSysInfo wsysinfo)
	{
		ePass.GetSysInfo(wsysinfo);
	}

	public void Cleanup(int i)
	{
		ePass.Cleanup(i);
	}

	public int GetLibVersion()
	{
		return ePass.GetLibVersion();
	}

	public int GetDriverVersion()
	{
		return ePass.GetDriverVersion();
	}

	public void Tea(int i, int j, byte abyte0[], int k, byte abyte1[], int ai[])
	{
		ePass.Tea(i, j, abyte0, k, abyte1, ai);
	}

	public CePass()
	{
		ePass = new ePassAPI();
	}
}
