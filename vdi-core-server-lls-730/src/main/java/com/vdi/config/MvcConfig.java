package com.vdi.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableAsync
@EnableWebMvc
@EnableScheduling
@EnableAspectJAutoProxy
@Import({PersistenceContext.class})
@ImportResource("classpath:applicationContext-security.xml")
@ComponentScan("com.vdi")
//@EnableGlobalMethodSecurity
@EnableTransactionManagement 
public class MvcConfig   {

}