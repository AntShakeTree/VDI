package com.opzoon.ohvc.service;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.opzoon.ohvc.domain.Driver;
import com.opzoon.vdi.core.cloud.CloudManager;
/**
 * 
 * @author maxiaochao and tanyunhua
 * @version: V04 
 * @since V04
 * 2012-10-12
 */
public class DriverManager{

	private static final String NODE_NAME_ROOT = "vdidrivers";
	private static final String NODE_NAME_DRIVERCLASS = "driverClass";
	private static final String NODE_NAME_VERSION = "version";
	private static final String NODE_NAME_NAME = "name";
	private static final String DEFAULT_NAME = "opzoon";

	public static List<Driver> findDriverName(String path) {
		List<Driver> driverList =new ArrayList<Driver>();
		if(path == null)
			return driverList;
		File file = new File(path);
		File[] files = file.listFiles(new ExtensionFileFilter(".xml"));
		if(files == null)
			return driverList;
		Driver defaultDriver = null;
        String name;
		for(File xmlFile: files)
		{
			try {
	            SAXReader reader = new SAXReader();
	            Document document;
				document = reader.read(xmlFile);
				
	            Element root = document.getRootElement();
	            if(root == null)
	            	continue;
	            if(!NODE_NAME_ROOT.equals(root.getName()))
	            	continue;
	            
	            List<Element> drivers = root.elements();
	            if(drivers == null)
	            	continue;

	            for(Element e: drivers)
	            {
		            Driver driver = new Driver();
		            
	            	Element ele = e.element(NODE_NAME_DRIVERCLASS);
	            	if(ele == null)
	            		continue;
	            	driver.setDriverClass(ele.getText());
	            	
	            	ele = e.element(NODE_NAME_VERSION);
	            	if(ele == null)
	            		continue;
	            	driver.setVersion(ele.getText());

	            	ele = e.element(NODE_NAME_NAME);
	            	if(ele == null)
	            		continue;
	            	name = ele.getText();
	            	driver.setDriverName(name);

	            	if(name != null && defaultDriver == null && name.toLowerCase().indexOf(DEFAULT_NAME) >= 0 )
	            	{
	            		defaultDriver = driver;
	            	}
	            	else
	            	{
	            		driverList.add(driver);
	            	}
	            }
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(defaultDriver != null)
			driverList.add(0, defaultDriver);
		return driverList;
	}

	public static CloudManager instanceByDriver(String driverClass) throws Exception {
		try {
			return (CloudManager)Class.forName(driverClass).newInstance();
		} catch (Exception e) {
			throw e;
		}
	}
	
	static class ExtensionFileFilter implements FileFilter {

	    private String extension;

	    public ExtensionFileFilter(String extension) {
	        this.extension = extension;
	    }

	    public boolean accept(File file) {
	        if(file.isDirectory( )) {
	            return false;
	        }

	        String name = file.getName( );
	        return name.endsWith(extension);
	    }
	} 
}