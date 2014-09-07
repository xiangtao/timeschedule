package com.deve.timeschedule.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.deve.timeschedule.monitor.HeartBeatJob;



public class JobListener implements ContextListener {
	private static final Log log = LogFactory.getLog(JobListener.class);

	public void onShutdown() {
		log.info("listener shutdown");
	}

	public boolean onStartup() throws Exception {
		log.info("listener begins");
		return true;
	}
}
