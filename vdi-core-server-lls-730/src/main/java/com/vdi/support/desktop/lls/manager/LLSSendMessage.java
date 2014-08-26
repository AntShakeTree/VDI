package com.vdi.support.desktop.lls.manager;

import org.springframework.stereotype.Service;

import com.vdi.support.desktop.lls.domain.BasicDomain;

@Service
public interface LLSSendMessage {
	public <T extends BasicDomain> T sendMessage(Object message, Class<T> clazz) ;
}

