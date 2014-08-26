package com.spring;

public class User {
String realname;
String username;
String password;

public User() {}

public User(String realname, String username, String password) {
	super();
	this.realname = realname;
	this.username = username;
	this.password = password;
}

public String getRealname() {
	return realname;
}
public void setRealname(String realname) {
	this.realname = realname;
}
public String getUsername() {
	return username;
}
public void setUsername(String username) {
	this.username = username;
}
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
}
