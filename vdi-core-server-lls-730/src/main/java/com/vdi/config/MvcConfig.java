package com.vdi.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
//@EnableWebMvc
@EnableAsync
@EnableScheduling
@EnableAspectJAutoProxy
//@Import({PersistenceContext.class,SecurtyConfig.class})
@ImportResource("classpath:applicationContext-security.xml")
@ComponentScan("com.vdi")
@EnableTransactionManagement 
public class MvcConfig   {

}