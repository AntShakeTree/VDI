package com.vdi.config;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.jolbox.bonecp.BoneCPDataSource;

/**
 * @author Petri Kainulainen
 */
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:env.properties")
@EnableJpaRepositories(basePackages={ "com.vdi.dao"})
@Order(1)
public class PersistenceContext {
	
    private static final String PROPERTY_PACKAGES_TO_SCAN = "com.vdi.dao.desktop.domain";
    private static final String PROPERTY_PACKAGES_TO_SCAN_2 = "com.vdi.dao.user.domain";
    protected static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
    protected static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
    protected static final String PROPERTY_NAME_DATABASE_URL = "db.url";
    protected static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";
    protected static final String PROPERTY_NAME_DATABASE_IDLECONNECTIONTESTPERIOD = "db.idleConnectionTestPeriod";
    protected static final String PROPERTY_NAME_DATABASE_IDLEMAXAGE = "db.idleMaxAge";
    protected static final String PROPERTY_NAME_DATABASE_MAXCONNECTIONSPERPARTITION = "db.maxConnectionsPerPartition";
    protected static final String PROPERTY_NAME_DATABASE_PARTITIONCOUNT = "db.partitionCount";
    protected static final String PROPERTY_NAME_DATABASE_ACQUIREINCREMENT = "db.acquireIncrement";
	protected static final String PROPERTY_NAME_DATABASE_STATEMENTSCACHESIZE = "db.statementsCacheSize";
	protected static final String PROPERTY_NAME_DATABASE_RELEASEHELPERTHREADS = "db.releaseHelperThreads";

    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    private static final String PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";



    @Resource
    private Environment environment;

  
//	@SuppressWarnings("deprecation")
	@Bean
    public DataSource dataSource() {
        BoneCPDataSource dataSource = new BoneCPDataSource();
        dataSource.setDriverClass(environment.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
        dataSource.setJdbcUrl(environment.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
        dataSource.setUsername(environment.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
        dataSource.setPassword(environment.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));
        dataSource.setIdleConnectionTestPeriod(environment.getProperty(PROPERTY_NAME_DATABASE_IDLECONNECTIONTESTPERIOD, Integer.class), TimeUnit.SECONDS);
        dataSource.setIdleMaxAge(environment.getProperty(PROPERTY_NAME_DATABASE_IDLEMAXAGE, Integer.class), TimeUnit.SECONDS);
        dataSource.setMaxConnectionsPerPartition(environment.getProperty(PROPERTY_NAME_DATABASE_MAXCONNECTIONSPERPARTITION,  Integer.class));
        dataSource.setPartitionCount(environment.getProperty(PROPERTY_NAME_DATABASE_PARTITIONCOUNT, Integer.class));
        dataSource.setAcquireIncrement(environment.getProperty(PROPERTY_NAME_DATABASE_ACQUIREINCREMENT, Integer.class));
        dataSource.setStatementsCacheSize(environment.getProperty(PROPERTY_NAME_DATABASE_STATEMENTSCACHESIZE,Integer.class));
//        dataSource.setReleaseHelperThreads(environment.getProperty(PROPERTY_NAME_DATABASE_RELEASEHELPERTHREADS,Integer.class));

        return dataSource;
    }

    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();

        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

        return transactionManager;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
//        entityManagerFactoryBean.setPackagesToScan(PROPERTY_PACKAGES_TO_SCAN);
        entityManagerFactoryBean.setPackagesToScan(PROPERTY_PACKAGES_TO_SCAN,PROPERTY_PACKAGES_TO_SCAN_2);

        Properties jpaProperties = new Properties();
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_DIALECT, environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL, environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL));
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO, environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO));
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY, environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY));
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));

        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }
}
