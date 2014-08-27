package com.opzoon.vdi.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

/**
 * 
 * @author tanyunhua
 * 
 */
public class LicenseUtil {
	public static final String HARDWARE_FILE_PATH = "./hwinfo.dat";
	private static final String SPL_HW = "##--##";
	private static final String SPL_LI = "#-#-#-";
	private static final String PUBLICK_KEY_PATH = "./opzoon_vdi_public.key";

	public static Set<String> hardwareInfoSet = new HashSet<String>();
	public static Set<String> hardwareInfoInFileSet = new HashSet<String>();
	private static final Logger log = LoggerFactory.getLogger(LicenseUtil.class);
	private static final int MAX_ENCRYPT_BLOCK = 117;
	private static final int MAX_DECRYPT_BLOCK = 128;

	static 
	{
		hardwareInfoInFileSet = readHardwareInfo();
		
		try {
			String fingerPrint = getHardwareInfo();
			hardwareInfoSet.add(fingerPrint);
			hardwareInfoInFileSet.add(fingerPrint);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("get finger print error: ", e);
		}
		
		log.info("hardwareInfoInFileSet count is " + hardwareInfoInFileSet.size());
		for(String str: hardwareInfoInFileSet)
		{
			log.info("file hw is " + str);
		}
	}
	
//	// 算法名称
//	private static final String KEY_ALGORITHM = "DES";
//	// 算法名称/加密模式/填充方式
//	// DES共有四种工作模式-->>ECB：电子密码本模式、CBC：加密分组链接模式、CFB：加密反馈模式、OFB：输出反馈模式
//	private static final String CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";
//	private static final String KEY = "jksjgi58458#$%$&%*,,llkjwrkln42mndfiocv0932";
//	private static final String CIPER_START = "sss#%!werghfgr%we32fj,asj42^*;34j2"; // 不能有^|.等正则符号。
//	private static final String VDI_VERSION = "Version:opzoon vDesktop     v0.1";
//	
//	public static String getValueFromContent(String content) {
//		if (content == null || content.equals(""))
//			return null;
//		try {
//			String[] strs = content.split("###");
//			if (strs == null || strs.length < 4) {
//				return null;
//			}
//			if (!strs[1].startsWith(VDI_VERSION)) {
//				return null;
//			}
//
//			return decrypt(strs[2].replace(CIPER_START, ""), KEY);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//	}
//	// Evan
//	// @Test
//	public void testContent() throws Exception {
//		String str = "BEGIN PGP SIGNED MESSAGE###Version:opzoon vDesktop     v0.1.1017.1###"
//				+ CIPER_START + "rBTozFX/sjk=###END PGP SIGNATURE";
//		System.out.println(getValueFromContent(str));
//	}
//
//	/**
//	 * 生成密钥
//	 */
//	public static String initkey() throws NoSuchAlgorithmException {
//		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM); // 实例化密钥生成器
//		kg.init(56); // 初始化密钥生成器
//		SecretKey secretKey = kg.generateKey(); // 生成密钥
//		return Base64.encodeBase64String(secretKey.getEncoded()); // 获取二进制密钥编码形式
//	}
//
//	/**
//	 * 转换密钥
//	 */
//	private static Key toKey(byte[] key) throws Exception {
//		DESKeySpec dks = new DESKeySpec(key); // 实例化Des密钥
//		SecretKeyFactory keyFactory = SecretKeyFactory
//				.getInstance(KEY_ALGORITHM); // 实例化密钥工厂
//		SecretKey secretKey = keyFactory.generateSecret(dks); // 生成密钥
//		return secretKey;
//	}
//
//	/**
//	 * 加密数据
//	 * 
//	 * @param data
//	 *            待加密数据
//	 * @param key
//	 *            密钥
//	 * @return 加密后的数据
//	 */
//	public static String encrypt(String data, String key) throws Exception {
//		Key k = toKey(Base64.decodeBase64(key)); // 还原密钥
//		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM); // 实例化Cipher对象，它用于完成实际的加密操作
//		cipher.init(Cipher.ENCRYPT_MODE, k); // 初始化Cipher对象，设置为加密模式
//		return Base64.encodeBase64String(cipher.doFinal(data.getBytes())); // 执行加密操作。加密后的结果通常都会用Base64编码进行传输
//	}
//
//	/**
//	 * 解密数据
//	 * 
//	 * @param data
//	 *            待解密数据
//	 * @param key
//	 *            密钥
//	 * @return 解密后的数据
//	 */
//	public static String decrypt(String data, String key) throws Exception {
//		Key k = toKey(Base64.decodeBase64(key));
//		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
//		cipher.init(Cipher.DECRYPT_MODE, k); // 初始化Cipher对象，设置为解密模式
//		return new String(cipher.doFinal(Base64.decodeBase64(data))); // 执行解密操作
//	}

	public static String getHardwareInfo() throws Exception
	{
		String serialNum = getSerialNumber();

		for(int i = 0 ; i < serialNum.getBytes().length; i++)
		{
			System.out.print(String.format("%02x", serialNum.getBytes()[i]));
		}
		System.out.println("  ffffffff");
		
		String cpuId = getProcessorID();

		for(int i = 0 ; i < cpuId.getBytes().length; i++)
		{
			System.out.print(String.format("%02x", cpuId.getBytes()[i]));
		}
		System.out.println("  ccccccccc");
		
		//add by zhanglu 2014-06-29 start
		String mac = getMinMac();
		System.out.println("MAC-----------" + mac);
		//add by zhanglu 2014-06-29 end
		//update by zhanglu 2014-06-29 start
//		String info = serialNum + "###" + cpuId;
		String info = serialNum + "###" + cpuId + "###" + mac;
		//update by zhanglu 2014-06-29 end
		return info;
	}
	private static String getSerialNumber()
	{
		String cmd = "dmidecode |grep 'Serial Number'";
		StringBuilder resultSB = new StringBuilder();
		RuntimeUtils.shell(resultSB, cmd);
		String result = resultSB.toString();
		if(result == null || result.isEmpty())
			return null;
		String[] reArr = result.split("\n");
		String sn = reArr[0];
		sn = sn.replaceAll("Serial Number: ", "");
		sn = sn.replaceAll("\t", "");
		return sn.replaceAll(" ", "");
	}
	//add by zhanglu 2014-06-28 start
	/**
	 * 获取系统最小网卡地址
	 * @param 
	 * @return
	 */
	private static String getMinMac()
	{
//		String cmd = "ifconfig | sed -n '/HWaddr/ s/^.*HWaddr *//pg'";
//		StringBuilder resultSB = new StringBuilder();
//		RuntimeUtils.shell(resultSB, cmd);
//		String result = resultSB.toString();
//		System.out.println("RES-------"+result+"----");
//		if(result == null || result.isEmpty())
//			return null;
//		String[] reArr = result.split("\n");
//		String mac = comporsitor(reArr,0);
//		mac = mac.replaceAll("\t", "");
//		return mac.replaceAll(" ", "");
		Enumeration<NetworkInterface> allNetInterfaces;
		List<String> macList = new ArrayList<String>();
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				byte[] macb = netInterface.getHardwareAddress();
				if (null != macb && macb.length > 0){
					String mac = displayMac(macb);
					macList.add(mac);
				}
			}
			return comporsitor(macList,0);
		} catch (SocketException e) {
			return null;
		}
	}
	/**
	 * 生成MAC地址
	 * @param mac
	 * @return
	 */
	private static String displayMac(byte[] mac){
		if (null == mac) return null;
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < mac.length; i++){
			String s = Integer.toHexString(mac[i] & 0xFF);
			sb.append(s.length() == 1 ? 0 + s : s);
		}
		return sb.toString().toLowerCase();
	}
	/**
	 * 比较系统上所有网卡地址
	 * @param reArr
	 * @param flag 0 : min | 1 : max
	 * @return
	 */
	private static String comporsitor(List<String> reArr, int flag)
	{
		if (null == reArr)
		{
			return null;
		}else
		{
			String max = null;
			String min = null;
			String mac = null;
			int len = reArr.size();
			int a = 0;
			for(int i = 0; i < len; i++)
			{
				mac = max = min = reArr.get(i);
				System.out.println("RESSSS++++++++" + mac);
				for(int j = i + 1; j < len; j++)
				{
					String mm = reArr.get(i);
					a = mm.compareTo(mac);
					if ( a > 0)
					{
						max = mm;
						min = mac;
					}else if(a < 0)
					{
						max = mac;
						min = mm;
					}else
					{
						max = min = mac;
					}
				}
			}
			return flag == 0 ? min : max;
		}
	}
	//add by zhanglu 2014-06-28 end

	private static String getProcessorID()
	{
		String cmd = "dmidecode -t processor|grep ID";
		StringBuilder resultSB = new StringBuilder();
		RuntimeUtils.shell(resultSB, cmd);
		String result = resultSB.toString();
		if(result == null || result.isEmpty())
			return null;
		String[] reArr = result.split("\n");
		String proId = reArr[0].replaceAll("ID: ", "");
		proId = proId.replaceAll("\t", "");
		return proId.replaceAll(" ", "");
	}
	
	//license cipher format: version:  splitor  licenseconent splitor
	//license de cipher format   hardwareinfo splitor-hw hardwareinfo splitor-hw hardwareinfo splitor-Li count;
	//update by zhanglu 2014-07-07 start
//	public static String getValueFromContent(String content) throws Exception {
	public static String[] getValueFromContent(String content) throws Exception {
	//update by zhanglu 2014-07-07 end
		if (content == null || content.equals(""))
		{
			log.info("content is null");
			return null;
		}
		PublicKey publicKey = null;
		boolean isUkey = false;
		try {
			isUkey = LicenseServerUtil.isUkey();
			String publicKeyStr = null;
			if (isUkey){
				publicKeyStr = LicenseServerUtil.readUkey(LicenseServerUtil.PUBLIC_KEY);
			}else{
				publicKeyStr = readTxtFile(PUBLICK_KEY_PATH);
			}
			if (publicKeyStr == null || publicKeyStr.equals(""))
			{
				log.info("not found the public key");
				return null;
			}
			publicKey = getPublicKey(publicKeyStr);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			log.info("public key error ", e1);
			return null;
		}
		try {
			String licenseStr = LicenseUtil.decryptByPublicKey(content, publicKey);
			String[] strs = licenseStr.split(SPL_LI);
			//update by zhanglu 2014-07-07 start
//				if(strs == null || strs.length != 2)
			if(strs == null || strs.length != 5)
			//update by zhanglu 2014-07-07 end
			{
				log.info("license content error " + licenseStr);
				return null;
			}
			//update by zhanglu 2014-08-15 bugId: start
//			if(!isUkey && !checkHardwareInfo(strs[0]))
			if(!isUkey && !"2".equals(strs[3]) && !checkHardwareInfo(strs[0]))
			//update by zhanglu 2014-08-15 bugId: end
			{
				log.info("checkHardwareInfo error " + licenseStr);
				throw new Exception("finger print error");
			}
			//update by zhanglu 2014-07-07 start
//				return strs[1];
			return strs;
			//update by zhanglu 2014-07-07 end
			
		} catch (InvalidKeyException e1) {
			// TODO Auto-generated catch block
			log.info("license invalide key ", e1);
		} catch (IllegalBlockSizeException e1) {
			// TODO Auto-generated catch block
			log.info("license IllegalBlock ", e1);
		} catch (BadPaddingException e1) {
			// TODO Auto-generated catch block
			log.info("license BadPadding ", e1);
		}
		return null;
	}
	//在获取到硬件信息和导入授权之间，如果有新机器加入或者旧机器退出（包括未开机），是否合法？如果不合法，需要修改下面的方法
	private static boolean checkHardwareInfo(String hwInfos)
	{
		String[] hws = hwInfos.split(SPL_HW);
		if(hws == null || hws.length <= 0)
		{
			log.info("checkHardwareInfo error hws is null");
			return false;
		}
		for(String str: hardwareInfoInFileSet)
		{
			log.info("checkHardwareInfo hw " + str);
		}
		
		for(String hw: hws)
		{
			log.info("license hw " + hw);
			if(LicenseUtil.hardwareInfoInFileSet.contains(hw))
				return true;
		}
		return false;
	}
	
	public static String encryptByKey(String data, Key pKey) 
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, pKey);
			//update by zhanglu 2014-07-21 start
//			int len = data.length();
//			int offset = 0;
//			byte[] result = null;
//			int i = 0;
//			while (len - offset > 0) {
//				byte[] cache = null;
//				if (len - offset > MAX_ENCRYPT_BLOCK) {
//					cache = cipher.doFinal(data.getBytes(),offset,MAX_ENCRYPT_BLOCK);
//				} else {
//					cache = cipher.doFinal(data.getBytes(),offset,len - offset);
//				}
//				result = concat(result,cache);
//				i++;
//				offset = i * MAX_ENCRYPT_BLOCK;
//			}
			byte[] raw = cipher.doFinal(data.getBytes());
			return Base64.encodeBase64String(raw);
//			return Base64.encodeBase64String(result);
			//update by zhanglu 2014-07-21 end
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String decryptByPublicKey(String data, PublicKey pKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		Cipher cipherDecrypt;
		try {
			cipherDecrypt = Cipher.getInstance("RSA");
			cipherDecrypt.init(Cipher.DECRYPT_MODE, pKey);
			//update by zhanglu 2014-07-21 start
//			byte[] data64 = Base64.decodeBase64(data);
//			int len = data64.length;
//			int offset = 0;
//			byte[] result = null;
//			int i = 0;
//			while (len - offset > 0) {
//				byte[] cache = null;
//				if (len - offset > MAX_DECRYPT_BLOCK) {
//					cache = cipherDecrypt.doFinal(data64,offset,MAX_DECRYPT_BLOCK);
//				} else {
//					cache = cipherDecrypt.doFinal(data64,offset,len - offset);
//				}
//				result = concat(result,cache);
//				i++;
//				offset = i * MAX_DECRYPT_BLOCK;
//			}
			byte[] ram = cipherDecrypt.doFinal(Base64.decodeBase64(data));
			return new String(ram);
//			return new String(result);
			//update by zhanglu 2014-07-21 end
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static byte[] concat(byte[] first, byte[] second){
		if (first == null && second == null) {
			return new byte[0];
		}
		if (null == first) {
			return second;
		}
		if (null == second) {
			return first;
		}
		byte[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
	
	public static PrivateKey getPrivateKey(String key) throws InvalidKeySpecException 
	{
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey pKey = keyFactory.generatePrivate(keySpec);
			return pKey;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static PublicKey getPublicKey(String key) throws InvalidKeySpecException 
	{
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        PublicKey pKey = keyFactory.generatePublic(keySpec);
			return pKey;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
	@Test
	public void testGenPriKeyPair() throws NoSuchAlgorithmException
	{
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(1024);
		KeyPair kp = kpg.genKeyPair();
		PublicKey publicKey = kp.getPublic();
		PrivateKey privateKey = kp.getPrivate();
		String publicKeyStr = Base64.encodeBase64String(publicKey.getEncoded());
		String privateKeyStr = Base64.encodeBase64String(privateKey.getEncoded());
		System.out.println(publicKeyStr);
		System.out.println(privateKeyStr);
	}
	
	public static String getAllHardwareInfo()
	{
		return getAllHardwareInfo(hardwareInfoSet);
	}

	public static String getAllFileHardwareInfo()
	{
		return getAllHardwareInfo(hardwareInfoInFileSet);
	}
	
	public static String getAllHardwareInfo(Set<String> hwInfoSet)
	{
		String hwinfo = "";
		for(String str: hwInfoSet)
		{
			hwinfo += str + SPL_HW;
		}
		return hwinfo;
	}
	
	public static Set<String> toHardwareSet(String hwStr)
	{
		Set<String> hwInfoSet = new HashSet<String>();
		String[] hws = hwStr.split(SPL_HW);
		for(String hw: hws)
		{
			hwInfoSet.add(hw);
		}
		return hwInfoSet;
	}
	
	public static Set<String> readHardwareInfo()
	{
		String hwStr;
		try {
			hwStr = readTxtFile(HARDWARE_FILE_PATH);
			return toHardwareSet(hwStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new HashSet<String>();
	}
	
	public static String readTxtFile(String path) throws IOException
	{
		File file = new File(path);
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line = null;
		String content = "";
		while(null != (line = in.readLine())) {
			content += "\r\n" + line;
		}
		content = content.replaceFirst("\r\n", "");
		System.out.println(content);
		return content;
	}

//	// Evan
//	@Test
//	public void testDES() throws Exception {
//		String source = "50";
//		System.out.println("原文: " + source);
//
//		// String key = initkey();
//		System.out.println("密钥: " + KEY);
//
//		String encryptData = encrypt(source, KEY);
//		// BEGIN PGP SIGNED MESSAGE###Version:opzoon vDesktop
//		// v0.1.1017.1###" + CIPER_START + "rBTozFX/sjk=###END PGP SIGNATURE
//		System.out.println("加密: ");
//		String ciper = "BEGIN PGP SIGNED MESSAGE###" + VDI_VERSION + "###"
//				+ CIPER_START + encryptData + "###END PGP SIGNATURE";
//		System.out.println(ciper);
//
//		String decryptData = getValueFromContent(ciper);
//		System.out.println("解密: " + decryptData);
//	}

	@Test
	public void testRsa() throws Exception {
		Date start = new Date();
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		kpg.initialize(2048, random); // 指定密匙长度（取值范围：512～2048）
		KeyPair kp = kpg.genKeyPair(); // 生成‘密匙对’，其中包含着一个公匙和一个私匙的信息
		PublicKey public_key = kp.getPublic(); // 获得公匙
		PrivateKey private_key = kp.getPrivate(); // 获得私匙
		
		Date genD = new Date();
		System.out.println("gen key time is : " + (genD.getTime() - start.getTime()));
		
		String publicKeyStr = Base64.encodeBase64String(public_key.getEncoded());
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream("./public.key"));
			FileCopyUtils.copy(publicKeyStr, out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String privateKeyStr = Base64.encodeBase64String(private_key.getEncoded());
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream("./private.key"));
			FileCopyUtils.copy(privateKeyStr, out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("write file is : " + (new Date().getTime() - genD.getTime()));
		
		String pubKeyStr = readTxtFile("./public.key");
		String priKeyStr = readTxtFile("./private.key");

		System.out.println("write and read file is : " + (new Date().getTime() - genD.getTime())); 
		
		String str = "sdkfjaslkdjf kejtoew oite9ti340i5p4i ejf lsjf 03wuiw rej lkjaesfk jsalf";

		genD = new Date();
        PublicKey pubKey = LicenseUtil.getPublicKey(pubKeyStr);
		PrivateKey priKey = LicenseUtil.getPrivateKey(priKeyStr);
		Date d1 = new Date();
		System.out.println("init key time is : " + (d1.getTime() - genD.getTime()));
		//加密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, priKey);
		byte[] raw = cipher.doFinal(str.getBytes());

		Date d2 = new Date();
		System.out.println("encrypt time is : " + (d2.getTime() - d1.getTime()));
		System.out.println(Base64.encodeBase64String(raw));
        
		Cipher cipherDecrypt = Cipher.getInstance("RSA");
		cipherDecrypt.init(Cipher.DECRYPT_MODE, pubKey);
		System.out.println(str);
		System.out.println(new String(cipherDecrypt.doFinal(raw)));
		Date d3 = new Date();
		System.out.println("decrypt time is : " + (d3.getTime() - d2.getTime()));
	}
	
	public static PublicKey getPublicKey()
	{
		try {
			String content = readTxtFile(PUBLICK_KEY_PATH);
			return getPublicKey(content);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
//	@Test
//	public void testLicense()throws Exception
//	{
//		String fp = " VMware-564dced6e6708359-15d0384600924c4c+++     D7060200FFFBEB1F";
//		hardwareInfoInFileSet.add(fp);
//		
//		String cpFP = testCipherFingerPrint(fp, 200, "./private.key");
////		String publicKeyStr = readTxtFile("./public.key");
//		String count = getValueFromContent(cpFP);
//		System.out.println(count);
//	}
	
	//for cipher fingerprint and count, build license content
//	public String testCipherFingerPrint(String fp, int count, String keyPath) throws Exception
//	{
//		String license = fp + SPL_LI + count;
//		
//		String priKeyStr = readTxtFile(keyPath);
//		PrivateKey priKey = LicenseUtil.getPrivateKey(priKeyStr);
//		//加密
//		Cipher cipher = Cipher.getInstance("RSA");
//		cipher.init(Cipher.ENCRYPT_MODE, priKey);
//		byte[] raw = cipher.doFinal(license.getBytes());
//		String licenseCipher = Base64.encodeBase64String(raw);
//		System.out.println(licenseCipher);
//		return licenseCipher;
//	}
	//key version:md5;content
	//license version:keyuuid;
	//hardware version:md5;
	
	// dmidecode -t processor|grep ID
	// ID: 13 0F 04 00 FF FB 8B 17
	// ID: 12 0F 04 00 FF FB 8B 17
	// ID: 00 00 00 00 00 00 00 00
	// ID: 00 00 00 00 00 00 00 00
	
	// dmidecode |grep 'Serial Number'
	// Serial Number: CNG029THXG
	// Serial Number: CNG029THXG
	// Serial Number: Not Specified
	// Serial Number: Not Specified
	// Serial Number: Not Specified
	// Serial Number: Not Specified

	public static String getGuid(){
		return LicenseServerUtil.readUkey(LicenseServerUtil.GUID);
	}
}
