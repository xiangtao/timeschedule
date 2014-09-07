package com.deve.timeschedule.jmx;

public interface AppWrapperMBean {

	public String getName();

	public Integer getBootStyle();

	public void resume() throws Exception;
	
	public void pause() throws Exception;
}
