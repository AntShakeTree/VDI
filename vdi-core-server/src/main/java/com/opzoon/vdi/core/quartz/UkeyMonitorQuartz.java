package com.opzoon.vdi.core.quartz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.opzoon.appstatus.facade.AppStatusService;
import com.opzoon.vdi.core.domain.License;
import com.opzoon.vdi.core.facade.CommonException;
import com.opzoon.vdi.core.facade.LicenseFacade;
import com.opzoon.vdi.core.util.LicenseServerUtil;
import com.opzoon.vdi.core.ws.admin.LicenseMangement;


public class UkeyMonitorQuartz {

	private LicenseFacade licenseFacade;
	
	private AppStatusService appStatusService;
	
	private static final SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static final String FILE_PATH = "./timer.dat";
	
	//计数开关 -1==关闭 0==开启 1==过期
	public static int isExpired = 0;
	
	private static final int TIMER_DAY = 7;
	
	//每15秒查一次UKEY是否插入。如果插入读取license内容
	public void ukeyTask(){
		//如果插入Ukey
		if(!appStatusService.isStandalone() && LicenseServerUtil.isUkey()){
			//获取当前是否有license
			List<License> list = licenseFacade.listLicense(0, null);
			try {
				if (null != list && !"".equals(list) && list.size() > 0){
					//先尝试读取license文件
					String res = LicenseServerUtil.readUkey(LicenseServerUtil.LICENSE_FILE);
					if (null != res){
						JSONObject obj =  (JSONObject) JSONObject.wrap(res);
						String content = obj.getString("content");
						String connectcount = obj.getString("connectcount");
						//update by zhanglu 2014-08-14 bugId:3338 start
						for(License lis : list){
							if(!lis.getContent().equals(content)){
								licenseFacade.deleteLicense(lis.getIdlicense());
							}
						}
						int hours = Integer.parseInt(obj.getString("hours")) - 1;
						License license = new License();
						license.setConnectCount(connectcount);
						license.setCreatetime(dateFormate.format(new Date()));
						license.setContent(content);
						license.setIdlicense(0);
						license.setExpire(hours);
						license.setType(0);
						license.setMode(0);
						licenseFacade.createLicense(license);
						LicenseMangement.LICENSE_HOURS = hours;
//						if (!isExist(content)){
//							licenseFacade.deleteLicense(0);
//							int hours = Integer.parseInt(obj.getString("hours"))-1;
//							License license = new License();
//							license.setConnectCount(connectcount);
//							license.setCreatetime(dateFormate.format(new Date()));
//							license.setContent(content);
//							license.setIdlicense(0);
//							license.setExpire(hours);
//							license.setType(0);
//							license.setMode(0);
//							licenseFacade.createLicense(license);
//							LicenseMangement.LICENSE_HOURS = hours;
//						}
						//update by zhanglu 2014-08-14 bugId:3338 end
					}
				}else{
					//先尝试读取license文件
					String res = LicenseServerUtil.readUkey(LicenseServerUtil.LICENSE_FILE);
					if (null != res){
						JSONObject obj =  (JSONObject) JSONObject.wrap(res);
						String content = obj.getString("content");
						String connectcount = obj.getString("connectcount");
						int hours = Integer.parseInt(obj.getString("hours"))-1;
						License license = new License();
						license.setConnectCount(connectcount);
						license.setCreatetime(dateFormate.format(new Date()));
						license.setContent(content);
						license.setIdlicense(0);
						license.setExpire(hours);
						license.setType(0);
						license.setMode(0);
						licenseFacade.createLicense(license);
						LicenseMangement.LICENSE_HOURS = hours;
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CommonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//不是Ukey的时候需要删除关于Ukey的license
		}else{
			//获取当前是否有license
			List<License> list = licenseFacade.listLicense(0, null);
			if (null != list && !"".equals(list) && list.size() > 0){
				for(License lis : list){
					if(0 == lis.getMode()){
						licenseFacade.deleteLicense(lis.getIdlicense());
					}
				}
			}else{
				File file = new File(FILE_PATH);
				if (!file.exists()){
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					LicenseMangement.LICENSE_HOURS = 24 * TIMER_DAY;
				}
			}
		}
	}
	
	//每小时更新一下系统时间，扣除有限时间
	public void updateSystemTime(){
		System.out.println("update system time start");
		//获取当前是否有license
		List<License> list =  licenseFacade.listLicense(0, null);
		if (null != list && !"".equals(list) && list.size() > 0){
			System.out.println("license is " + list.size());
			for (License license : list){
				//获取在有效期的license
				if (license.getExpire() > 0){
					int hours = license.getExpire() - 1;
					license.setExpire(hours);
					try {
						licenseFacade.updateLicense(license);
					} catch (CommonException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//当是UKEY版的license的
					if(license.getMode() == 0){
						//尝试读取Ukey
						if(!appStatusService.isStandalone() && LicenseServerUtil.isUkey()){
							//先尝试读取license文件
							String res = LicenseServerUtil.readUkey(LicenseServerUtil.LICENSE_FILE);
							if (null != res){
								JSONObject obj =  (JSONObject) JSONObject.wrap(res);
								//把license写入Ukey
								obj.remove("hours");
								try {
									obj.put("hours", String.valueOf(hours));
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								LicenseServerUtil.writeUkey(LicenseServerUtil.LICENSE_FILE,obj.toString());
								LicenseMangement.LICENSE_HOURS = hours;
							}
						}
					}
					break;
				}
			}
		}else{
			System.out.println("license is 7 day" );
			//获取计数文件
			int timer = getSystemTimer();
			//当开关是关闭或过期时退出计数
			if (1 == isExpired) {
				LicenseMangement.LICENSE_HOURS = 0;
				return;
			}else if(-1 == isExpired){
				return;
			}
			else 
			{
				//正常计数
				if (0 < timer )
				{
					int times = timer - 1;
					setSystemTimer(String.valueOf(times));
					LicenseMangement.LICENSE_HOURS = times;
				}else
				{
					isExpired = 1;
					//add by zhanglu 2014-08-16 bugId:3341 start
					LicenseMangement.LICENSE_HOURS = 0;
					//add by zhanglu 2014-08-16 bugId:3341 end
				}
			}
		}
		System.out.println("update system time end");
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private static int getSystemTimer(){
		File file = new File(FILE_PATH);
		BufferedReader in = null;
		String content = "";
		try{
			if (file.exists()){
				in = new BufferedReader(new FileReader(file));
				String line = null;
				while(null != (line = in.readLine())) {
					content += line;
				}
			}else{
				file.createNewFile();
				content = "0";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if (null != in){
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if("".equals(content) || "0".equals(content)){
			return 24 * TIMER_DAY;
		}else{
			return Integer.parseInt(content);
		}
	}
	/**
	 * 
	 * @param data
	 * @throws IOException
	 */
	public static void setSystemTimer(String data){
		File file = new File(FILE_PATH);
		BufferedWriter wr = null;
		try {
			wr = new BufferedWriter(new FileWriter(file));
			wr.write(data);
			wr.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				wr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private boolean isExist(String content){
		return licenseFacade.haveLicense(content);
	}
	
	public void setLicenseFacade(LicenseFacade licenseFacade) {
		this.licenseFacade = licenseFacade;
	}
	public void setAppStatusService(AppStatusService appStatusService)
	{
		this.appStatusService = appStatusService;
	}
	public static void main(String[] args){
//		updateSystemTime();
	}
}
