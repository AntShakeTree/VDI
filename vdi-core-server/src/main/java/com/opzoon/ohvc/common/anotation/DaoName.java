package com.opzoon.ohvc.common.anotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface DaoName {

	public String name();

}
