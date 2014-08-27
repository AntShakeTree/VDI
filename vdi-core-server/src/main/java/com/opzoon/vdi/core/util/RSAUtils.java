package com.opzoon.vdi.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

import com.dc.core.DCSHCoreAPI;
import com.dc.core.DCSHPDATA;

public abstract class RSAUtils {
	
	private RSAUtils() {
	}

	public static String parse(String pdataFileDataInBase64) throws IOException {
		DCSHCoreAPI dcsh = new DCSHCoreAPI();
		File file = File.createTempFile("rsa-pdata-", ".bin");
		FileOutputStream os = new FileOutputStream(file);
		os.write(Base64.decodeBase64(pdataFileDataInBase64));
		os.close();
		DCSHPDATA pdata = new DCSHPDATA();
		dcsh.DCSHSAInitBINRead(file.getAbsolutePath(), dcsh.iRecords,
				dcsh.cType);
		for (int i = 0; i < 1; i++) {
			dcsh.DCSHSAReadBINRec("", pdata, i, dcsh.cType);
		}
		dcsh.DCSHSAExitBINRead();
		dcsh.DCSHSAEnableToken(pdata, "888888");
		file.delete();
		return pdata.toString();
	}

	public static boolean authenticate(String pdataString, String dynamicCode) throws Exception {
		DCSHCoreAPI dcsh = new DCSHCoreAPI();
		long tokenTime = dcsh.DCSHSAGetPasscodeTime();
		DCSHPDATA pdata = new DCSHPDATA(pdataString);
		int ret = dcsh.DCSHSACheckPasscode(tokenTime, pdata, dynamicCode, "888888");
		return ret == 1;
	}

}
