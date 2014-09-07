package com.deve.timeschedule.jmx;

public class JmxAuth {
	private String userName;
	private String password;
	
	public JmxAuth(String name, String pwd){
		this.userName = name;
		this.password = pwd;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
