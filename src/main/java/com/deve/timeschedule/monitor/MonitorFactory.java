package com.deve.timeschedule.monitor;

public class MonitorFactory {
	public static IMonitor getHttpMonitor(){
		IMonitor m = HttpMonitor.getInstance();
		return m;
	}
}
