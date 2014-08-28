package com.vdi.lls.resource.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.springframework.util.FileCopyUtils;

public class AutoGeneralServicesFile {
	
	public static void main(String[] args) throws IOException {
		autoServiceImpl("Storage");
		autoServiceImpl("Template");
		autoServiceImpl("VmInstance");
		autoServiceImpl("VNetcard");
		autoServiceImpl("VmProc");
		autoServiceImpl("VDisk");
		
	}
	public static void auto(String name) throws UnsupportedEncodingException, IOException{
		String fileName="D:\\"+name+"Service.java";
		FileOutputStream out =new FileOutputStream(fileName);
		FileInputStream in=new FileInputStream("D:\\ComputePoolService.java");
		byte[] b=new byte[4096];
		while((in.read(b))!=-1){
			
			String temp= new String(b,"UTF-8");
			temp=temp.replace("TTT",name);
			out.write(temp.getBytes("UTF-8"));
		}
		out.close();
		in.close();
	}
	public static void autoServiceImpl(String name) throws UnsupportedEncodingException, IOException{
		String fileName="D:\\"+name+"ServiceImpl.java";
		FileOutputStream out =new FileOutputStream(fileName);
		FileInputStream in=new FileInputStream("D:\\ServcesImpl.txt");
		byte[] b=new byte[4096];
		while((in.read(b))!=-1){
			
			String temp= new String(b,"UTF-8");
			temp=temp.replace("000",name.toLowerCase());
			temp=temp.replace("111", name);
			temp=temp.replace("222", name.toUpperCase());
			out.write(temp.getBytes("UTF-8"));
		}
		out.close();
		in.close();
	}
}
