package com.opzoon.vdi.core.util;

import org.hibernate.Hibernate;
import org.hibernate.dialect.MySQLDialect;

public class CharToStringMySQLDialect extends MySQLDialect {

	public CharToStringMySQLDialect() {
		registerHibernateType(1, Hibernate.STRING.getName());
	}

}
