package com.opzoon.vdi.core.util;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;

/**
 * 与License Server通信的Socket函数类。
 * @author zhanglu
 *
 */
public class LicenseServerUtil {
	
    /*
     * 本地SocketIP
     */
    public static final String SOCKET_IP = "localhost";
    /*
     * 本地Socket PORT
     */
    public static final int SOCKET_PORT = 59001;
	/*
	 * client to server
	 */
	public static final int TYPE_0 = 0;
	
	/*
	 * server to client
	 */
	public static final int TYPE_1 = 1;
	
	/*
	 * queryPermit
	 */
	public static final int QUERY_PERMIT = 1;
	
	/*
	 * version
	 */
	private static final int version = 1;
	
	/*
     * CreateContent
     */
    private static final int CREATE_CONTEXT = 1;
    /*
     * DeleteContext
     */
    private static final int DELETE_CONTEXT = 2;
    /*
     * OpenDevice
     */
    private static final int OPEN_DEVICE = 3;
    /*
     * CloseDevice
     */
    private static final int CLOSE_DEVICE = 4;
    /*
     * CreateFile
     */
    private static final int CREATE_FILE = 5;
    /*
     * DeleteFile
     */
    private static final int DELETE_FILE = 6;
    /*
     * OpenFile
     */
    private static final int OPEN_FILE = 7;
    /*
     * CloseFile
     */
    private static final int CLOSE_FILE = 8;
    /*
     * Read
     */
    private static final int READ = 9;
    /*
     * Write
     */
    private static final int WRITE = 10;
    /*
     * Verify
     */
    private static final int VERIFY = 11;
	
	/*
	 * 认证成功
	 */
	public static final int CODE_SEC = 0;
	
	/*
	 * 构造数据格式失败
	 */
	public static final int CODE_ERR_1 = -1;
	
	/*
	 * 无法连接License服务器
	 */
	public static final int CODE_ERR_2 = -2;
	
	/*
	 * License过期
	 */
	public static final int CODE_ERR_3 = -3;
	
	/*
	 * 返回值类型转换失败
	 */
	public static final int CODE_ERR_4 = -4;
	
	/*
	 * 数据流关闭失败
	 */
	public static final int CODE_ERR_5 = -5;
	/*
     * license已经存在
     */
    public static final int CODE_ERR_6 = -6;
    /*
     * 通用失败
     */
    public static final int CODE_ERR_7 = -7;
    /*
     * License Server 返回值枚举列表
     */
	public enum ErrCode {
    	/* 成功 */
    	LICENSE_SEC("0", 0),
    	/* 参数错误 */
    	LICENSE_ERR_CODE_1("e0000001", -1),
    	/* 连接Server失败 */
    	LICENSE_ERR_CODE_2("e0000002", -2),
    	/* 找不到UKEY */
    	LICENSE_ERR_CODE_3("e0000003", -7),
    	/* 没有license */
    	LICENSE_ERR_CODE_4("e0000004", -1),
    	/* license已过期 */
    	LICENSE_ERR_CODE_5("e0000005", -3),
    	/* API版本错误 */
    	LICENSE_ERR_CODE_6("e0000101", -7),
    	/* 找不到指定的产品 */
    	LICENSE_ERR_CODE_7("e0000102", -7),
        /* 达到最大license数量  */
        LICENSE_ERR_CODE_8("e0000103", -7),
        /* license已经存在 */
        LICENSE_ERR_CODE_9("e0000201", -6),
        /* license错误，解密失败 */
        LICENSE_ERR_CODE_10("e0000202", -7);
        private String errorCode = null;
        private int code = 0;
        private ErrCode(String error, int code) {
            this.errorCode = error;
            this.code = code;
        }
        
        public static int getErrorCode(String code){
            for (ErrCode c : ErrCode.values()) {
                if (c.errorCode.equals(code)) {
                    return c.code;
                }
            }
            return 0;
        }
        
	}
	/*
	 * 产品名称
	 */
	public static final String PRO_NAME_VDI = "VDI";
	
	public static final int GUID = 0x200;
	
	public static final int PUBLIC_KEY = 0x300;
	
	public static final int LICENSE_FILE = 0x400;
	/**
	 * License Server通信接口实现。
	 * @param ip License Server的IP
	 * @param port License Server的端口
	 * @param name 申请授权的产品名称
	 * @param id 申请授权的产品实例ID
	 * @return
	 */
	public static int queryPermit(String ip, int port, String name, String id)
	{
		//构造通信消息
		byte[] data = null;
		try
		{
		      //构造主体的json格式
	        JSONObject jo = new JSONObject();
	        //设置版本
	        jo.put("version", version);
	        //设置产品名称
	        jo.put("producttype", name);
	        //设置SN
	        jo.put("id", id);
	        //构造data主体
	        String body = jo.toString();
			//构造通信协议主体
			data = makeData(TYPE_0,QUERY_PERMIT,body);
		}
		catch(JSONException e){
			return CODE_ERR_1;
		}
        String res = ePasSocket(ip,port,data,true);
        if (null == res || "".equals(res)){
        	return CODE_ERR_2;
        }
        //转换成JSON格式
        Gson json = new Gson();
        Map map = json.fromJson(res, Map.class);
        Double b = (Double) map.get("error");
        String error = Integer.toHexString(b.intValue());
        return ErrCode.getErrorCode(error);
	}
	
	/**
	 * 构造通信协议报文
	 * @param type
	 * @param index
	 * @param ver
	 * @param name
	 * @param id
	 * @return
	 * @throws JSONException
	 */
	private static byte[] makeData(int type, int index, String body)
	{
        byte[] bType = toByteArray(type, 2);
        byte[] bIndex = toByteArray(index, 2);
        // 获取data部分长度
        if (null == body) {
            byte[] bLen = toByteArray(0, 4);
            return byteMerger2(int2ToHH(bType), int2ToHH(bIndex),int4ToHH(bLen));
        }
        int len = body.length();
        byte[] bLen = toByteArray(len, 4);
        byte[] bData = body.getBytes();
        return byteMerger2(int2ToHH(bType), int2ToHH(bIndex), int4ToHH(bLen),
                bData);
	}
    
	/**
	 * java 合并两个byte数组  
	 * @param bytes
	 * @return
	 */
    private static byte[] byteMerger2(byte[]... bytes){
        int len = bytes.length;
        int bytes_len = 0;
        for (int i = 0; i < len; i++) {
            bytes_len += bytes[i].length;
        }
        
        byte[] res = new byte[bytes_len];
        int lens = 0;
        for (int i = 0; i < len; i++) {
            lens += i == 0 ? 0 : bytes[i - 1].length;
            int blen = bytes[i].length;
            System.arraycopy(bytes[i], 0, res, lens, blen);
        }
        return res;  
    }
    
    /**
     * 
     * @param b
     * @return
     */
    private static int getLen(byte[] b){
        byte bType[] = new byte[2];
        bType[0] = b[0];
        bType[1] = b[1];
        byte bIndex[] = new byte[2];
        bIndex[0] = b[2];
        bIndex[1] = b[3];
        byte bLen[] = new byte[4];
        bLen[0] = b[4];
        bLen[1] = b[5];
        bLen[2] = b[6];
        bLen[3] = b[7];
        return hBytesToInt(bLen);
    }
    
    /**
     * 将高字节数组转换为int
     * 
     * @param byte[]
     * @return int
     */
    public static int hBytesToInt(byte[] b) {
        int s = 0;
        for (int i = 0; i < 3; i++) {
            if (b[i] >= 0) {
                s = s + b[i];
            } else {
                s = s + 256 + b[i];
            }
            s = s * 256;
        }
        if (b[3] >= 0) {
            s = s + b[3];
        } else {
            s = s + 256 + b[3];
        }
        return s;
    }
	
//	private static char Hex(int bin)
//	{
//		char retval;
//		
//		if(bin >= 0 && bin <= 9)
//			retval = (char)('0' + bin);
//		else if (bin >= 10 && bin <= 15)
//			retval = (char)('A' + bin - 10);
//		else
//			retval = '0';
//		return retval;
//	}
	
	private static byte[] toByteArray(int iSource, int iArrayLen){
		byte[] bLocalArr = new byte[iArrayLen];
		for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
		}
		return bLocalArr;
	}
	
	private static byte[] int2ToHH(byte[] b) {
        byte[] res = new byte[2];
        res[1] = b[0];
        res[0] = b[1];
        return res;
    }
    
	private static byte[] int4ToHH(byte[] b) {
        byte[] res = new byte[4];
        res[3] = b[0];
        res[2] = b[1];       
        res[1] = b[2];
        res[0] = b[3];
        return res;
    }
	
	/**
	 * 判断是否插入Ukey
	 * @return
	 */
	public static boolean isUkey(){
		try {
			createContext();
			openDevice();
			verify();
			closeDevice();
			deleteContext();
			return true;
		} catch (Exception e) {
			return false;
		}
//		IePass ePass = null;
//		try{
//			ePass = new CePass();
//			ePassDef flag = new ePassDef();
//			//初始化
//			ePass.CreateContext(0, flag.EPAS_API_VERSION);
//			//打开Device
//			ePass.OpenDevice(flag.EPAS_OPEN_FIRST,null);
//			//验证
//			ePass.Verify(flag.EPAS_VERIFY_SO_PIN, "rockey".getBytes(), "rockey".length());
//			ePass.CloseDevice();
//			ePass.DeleteContext();
//		}catch(RTException e){
//			return false;
//		}catch(Exception e){
//			return false;
//		}
//		return true;
	}
	
	/**
	 * 读文件
	 * @param fid
	 */
	public static String readUkey (int fid){
		System.out.println("read ukey start");
		try {
			createContext();
			openDevice();
			verify();
			openFile(fid);
			String len = read(0, 2);
			if (null == len){
				return null;
			}
			String res = read(2, Integer.parseInt(len));
			closeFile();
			closeDevice();
			deleteContext();
			System.out.println("read ukey end " + res);
			return res;
		} catch (Exception e) {
			return null;
		}
//		IePass ePass = null;
//		int Low = 0;
//		int Hiw = 0;
//		int Len1 = 0;
//		byte[] tText1 = new byte[16];
//		int[] size1 = new int[1];
//		int[] size2 = new int[1];
//		String oText2 = "";
//		try{
//			ePass = new CePass();
//			ePassDef flag = new ePassDef();
//			WFileInfo fi = new WFileInfo();
//			//初始化
//			ePass.CreateContext(0, flag.EPAS_API_VERSION);
//			//打开Device
//			ePass.OpenDevice(flag.EPAS_OPEN_FIRST,null);
//			//验证
//			ePass.Verify(flag.EPAS_VERIFY_SO_PIN, "rockey".getBytes(), "rockey".length());
//			//打开文件
//			ePass.OpenFile(flag.EPAS_FILE_READ, fid, fi);
//			//读文件
//			ePass.Read(0, 0, tText1, 2, size1);
//			for(int i = 0; i < size1[0]; i++){
//				String oText = "";
//				Hiw = Low = tText1[i];
//				System.out.println("read tText1" + tText1[i]);
//				Low &= 0xF;
//				Hiw >>= 4;
//				Hiw &= 0xF;
//				//System.out.println("read Hiw" + Hex(Hiw));
//				//System.out.println("read Low" + Hex(Low));
//				oText = oText + Hex(Hiw) + Hex(Low);
//				System.out.println("read len" + oText);
//				Len1 += Integer.parseInt(oText, 16) << (i * 8);
//			}
//			System.out.println("ePass.OpenFile()"+Len1);
//			byte[] tText2 = new byte[Len1];
//			ePass.Read(0, 2, tText2, Len1, size2);
//			oText2 = new String(tText2,0,Len1);
//			System.out.println("ePass.OpenFile()"+oText2);
//			ePass.CloseFile();
//			ePass.CloseDevice();
//			ePass.DeleteContext();
//		}catch(RTException e){
//			System.out.println(e.getMessage());
//			return e.getMessage();
//		}catch(Exception e){
//			System.out.println(e);
//		}
//		return oText2;
	}
	
	public static void writeUkey(int fid, String content){
		System.out.println("write ukey start" + content);
		try {
			createContext();
			openDevice();
			verify();
			openFile(fid);
			String s = Base64.encodeBase64String(content.getBytes("UTF-8")).replaceAll("/r/n", "").replaceAll("/n", "");
			int len = s.length();
			write(0, String.valueOf(len), 2);
			write(2, s, len);
			closeFile();
			closeDevice();
			deleteContext();
			System.out.println("write ukey end");
		} catch (Exception e) {
			System.out.print("write ukey error!");
		}
//		IePass ePass = null;
//		byte[] tText = null;
//		Integer Len = content.length();
//		try {
//			tText = content.getBytes("UTF-8");
//		} catch (UnsupportedEncodingException e1) {
//			System.out.println(e1);
//			return;
//		}
//		int[] tSize = new int[1];
//		
//		try{
//			ePass = new CePass();
//			ePassDef flag = new ePassDef();
//			WFileInfo fi = new WFileInfo();
//			//初始化
//			ePass.CreateContext(0, flag.EPAS_API_VERSION);
//			//打开Device
//			ePass.OpenDevice(flag.EPAS_OPEN_FIRST,null);
//			//验证
//			ePass.Verify(flag.EPAS_VERIFY_SO_PIN, "rockey".getBytes(), "rockey".length());
//			//打开文件
//			ePass.OpenFile(flag.EPAS_ACCESS_WRITE, fid, fi);
//			//写文件大小
//			ePass.Write(0, 0, toByteArray(Len,Len.SIZE), 2, tSize);
//			//写文件
//			ePass.Write(0, 2, tText, Len, tSize);
//			System.out.println("ePass.Write()"+tSize[0]);
//			ePass.CloseFile();
//			ePass.CloseDevice();
//			ePass.DeleteContext();
//		}catch(RTException e){
//			System.out.println(e.getMessage());
//		}catch(Exception e){
//			System.out.println(e);
//		}
	}
	
	public static String toHexString(String s) 
	{ 
	String str=""; 
	for (int i=0;i<s.length();i++) 
	{ 
	int ch = (int)s.charAt(i); 
	String s4 = Integer.toHexString(ch); 
	str = str + s4; 
	} 
	return str; 
	} 
	
	public static void main(String[] args) {

//	    createContext();
//	    openDevice();
//	    verify();
//	    openFile(0x400);
//	    String s = read(0,2);
//        String res = read(2,Integer.parseInt(s));
//        closeFile();
//        closeDevice();
//        deleteContext();
//        System.out.print(res);
//	       createContext();
//	        openDevice();
//	        verify();
//	        openFile(0x400);
////	        String s = read(0,2);
////	        String res = read(2,Integer.parseInt(s));         0a41b8c0-2389-4500-9725-1b5025b01f6f
//	        String data = "{\"connectcount\":\"21\",\"content\":\"0a41b8c0-2389-4500-9725-1b5025b01f6f\",\"hours\":\"246\"}";
//            String s = Base64.encode(data.getBytes());
//            int len = s.length();
////            byte[] bb = toByteArray(len,2);
//	        write(0,String.valueOf(len), 2);
//	        write(2,s,len);
//	        closeFile();
//	        closeDevice();
//	        deleteContext();
	}
	
	private static int getError(String res) throws Exception{
		if (null == res || "".equals(res)){
			throw new Exception();
		}else{
	        //转换成JSON格式
	        Gson json = new Gson();
	        Map map = json.fromJson(res, Map.class);
	        Double b = (Double) map.get("status");
	        String error = Integer.toHexString(b.intValue());
	        int code = Integer.parseInt(error);
	        if (0 != code) {
	        	throw new Exception();
	        }
	        return code;
		}
	}
	
	private static int createContext() throws Exception{
       //构造通信消息
       byte[] data = null;
        try
        {
              //构造主体的json格式
            JSONObject jo = new JSONObject();
            jo.put("flags", 0);
            jo.put("apiversion", 256);
            //构造data主体
            String body = jo.toString();
            //构造通信协议主体
            data = makeData(TYPE_0,CREATE_CONTEXT,body);
        }
        catch(JSONException e){
            throw e;
        }
        String res = ePasSocket(SOCKET_IP,SOCKET_PORT,data,false);
        return getError(res);
	}
	
    private static int deleteContext() throws Exception{
        // 构造通信消息
        byte[] data = makeData(TYPE_0, DELETE_CONTEXT, null);
        String res = ePasSocket(SOCKET_IP, SOCKET_PORT, data, false);
        return getError(res);
    }
	
   private static int openDevice() throws Exception{
       //构造通信消息
       byte[] data = null;
        try
        {
              //构造主体的json格式
            JSONObject jo = new JSONObject();
            jo.put("flags", 1);
            jo.put("appid", "");
            //构造data主体
            String body = jo.toString();
            //构造通信协议主体
            data = makeData(TYPE_0,OPEN_DEVICE,body);
        }
        catch(JSONException e){
            return CODE_ERR_1;
        }
        String res = ePasSocket(SOCKET_IP,SOCKET_PORT,data,false);
        return getError(res);
   }
   
   private static int closeDevice() throws Exception{
       // 构造通信消息
       byte[] data = makeData(TYPE_0, CLOSE_DEVICE, null);
       String res = ePasSocket(SOCKET_IP, SOCKET_PORT, data, false);
       return getError(res);
   }
        
   private static int verify() throws Exception{
       //构造通信消息
       byte[] data = null;
        try
        {
              //构造主体的json格式
            JSONObject jo = new JSONObject();
            jo.put("flags", 1);
            jo.put("data", "rockey");
            jo.put("size", "rockey".length());
            //构造data主体
            String body = jo.toString();
            //构造通信协议主体
            data = makeData(TYPE_0,VERIFY,body);
        }
        catch(JSONException e){
            return CODE_ERR_1;
        }
        String res = ePasSocket(SOCKET_IP,SOCKET_PORT,data,false);
        return getError(res);
    }
   
   private static int openFile(int fid) throws Exception{
       //构造通信消息
       byte[] data = null;
        try
        {
              //构造主体的json格式
            JSONObject jo = new JSONObject();
            jo.put("flags", 16);
            jo.put("fileid", fid);
            //构造data主体
            String body = jo.toString();
            //构造通信协议主体
            data = makeData(TYPE_0,OPEN_FILE,body);
        }
        catch(JSONException e){
            return CODE_ERR_1;
        }
        String res = ePasSocket(SOCKET_IP,SOCKET_PORT,data,false);
        return getError(res);
    }
   
   private static int closeFile() throws Exception{
       // 构造通信消息
       byte[] data = makeData(TYPE_0, CLOSE_FILE, null);
       String res = ePasSocket(SOCKET_IP, SOCKET_PORT, data, false);
       return getError(res);
   }
   
   private static String read(int offset, int bytestoread){
       //构造通信消息
       byte[] data = null;
        try
        {
              //构造主体的json格式
            JSONObject jo = new JSONObject();
            jo.put("flags", 0);
            jo.put("offset", offset);
            jo.put("bytestoread", bytestoread);
            //构造data主体
            String body = jo.toString();
            //构造通信协议主体
            data = makeData(TYPE_0,READ,body);
        }
        catch(JSONException e){
            return null;
        }
        String res = ePasSocket(SOCKET_IP,SOCKET_PORT,data,false);
        if (null == res || "".equals(res)){
        	return null;
        }
        //转换成JSON格式
        Gson json = new Gson();
        Map map = json.fromJson(res, Map.class);
        Double b = (Double) map.get("status");
        String error = Integer.toHexString(b.intValue());
        if ("0".equals(error)){
            byte[] buffer = Base64.decodeBase64(map.get("buffer").toString());
            return new String(buffer);
        }else{
            return null;
        }
    }
   
   private static int write(int offset, String buffer, int len) throws Exception{
       //构造通信消息
       byte[] data = null;
        try
        {
              //构造主体的json格式
            JSONObject jo = new JSONObject();
            jo.put("flags", 0);
            jo.put("offset", offset);
            jo.put("buffer", buffer);
            jo.put("bytestowrite", len);
            //构造data主体
            String body = jo.toString();
            //构造通信协议主体
            data = makeData(TYPE_0,WRITE,body);
        }
        catch(JSONException e){
            return CODE_ERR_1;
        }
        String res = ePasSocket(SOCKET_IP,SOCKET_PORT,data,false);
        return getError(res);
    }
	
	private static String ePasSocket(String ip, int port, byte[] data, boolean flg){
        Socket client = null;
        try
        {
            //初始化socket对象,建立连接
            client = new Socket(ip,port);
            //向licnese Server发出客户请求
            client.getOutputStream().write(data);
            //获取服务器返回
            byte head[] = new byte[8];
            client.getInputStream().read(head);
            int len = 0;
            if (flg){
                len = getLen(head) - 1;
            }else{
                len = getLen(head);
            }
            
            byte body[] = new byte[len];
            client.getInputStream().read(body);
            return new String(body);
        }
        catch(UnknownHostException e){
            return null;
        }
        catch(IOException e){
            return null;
        }finally{
            try {
                if (null != client){
                    client.close();
                }
            } catch (IOException e) {
                return null;
            }
        }
	}
}
