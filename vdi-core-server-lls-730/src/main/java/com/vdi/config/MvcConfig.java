package com.vdi.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
//@EnableWebMvc
@EnableAsync
@EnableScheduling
@EnableAspectJAutoProxy
@Import({PersistenceContext.class})
@ImportResource("classpath:applicationContext-security.xml")
@ComponentScan("com.vdi")
@EnableTransactionManagement 
public class MvcConfig   {

}