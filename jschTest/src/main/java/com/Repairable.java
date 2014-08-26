package com;

public interface Repairable {
	boolean repair(String host,String user,String password,String execCommand);
}
