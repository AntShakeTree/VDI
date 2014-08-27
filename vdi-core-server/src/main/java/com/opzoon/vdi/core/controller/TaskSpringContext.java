package com.opzoon.vdi.core.controller;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

//need add to the spring xml

@Component("taskSpringContext")
public class TaskSpringContext implements ApplicationContextAware{

    private static ApplicationContext s_appContext;  

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
        s_appContext = applicationContext;  
	}
	
    public static <T> T inject(Object instance) {
        // autowire dynamically loaded object
        assert(s_appContext != null);
        AutowireCapableBeanFactory  beanFactory = s_appContext.getAutowireCapableBeanFactory();
        beanFactory.autowireBean(instance);
        return (T)instance;
    }
}
