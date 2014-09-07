package com.deve.timeschedule.jmx;

import java.util.Map;

public interface JobWrapperMBean {
	public long getStartExecutingTime();

	public long getExecutingTimes();

	public long getShorttestExecutingTime();

	public long getLongestExecutingTime();

	public String getName();

	public String getTriggerStr();

	public Map<String, String> getStatus();
	
	public void refreshTriggerStr(String str) throws Exception;
	
	public void refreshParams(Map<String, String> params) throws Exception;
	
	public void runNow() throws Exception;
}
