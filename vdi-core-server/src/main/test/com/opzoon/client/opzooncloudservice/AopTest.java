package com.opzoon.client.opzooncloudservice;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AopTest {
	public static void main(String[] args) {
		ApplicationContext context =new ClassPathXmlApplicationContext("spring-*.xml");
//		context.get
	}
}
