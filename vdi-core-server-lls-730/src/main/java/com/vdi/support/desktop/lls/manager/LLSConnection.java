package com.vdi.support.desktop.lls.manager;

import org.springframework.stereotype.Service;
@Service
public interface LLSConnection {
	public  void connection(String address,int port) ;
	public void close();
	public void reconnection() ;
	public boolean isConnection();
}
