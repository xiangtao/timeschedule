package com.deve.timeschedule.monitor;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;

public interface IMonitor {
	public void sendException(String taskName, Exception e);

	public void heartBeatMonitor() throws HttpException, IOException;

	public void sendConfigInfo() throws IOException;
}
