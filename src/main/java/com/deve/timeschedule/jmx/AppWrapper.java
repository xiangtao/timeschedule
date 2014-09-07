package com.deve.timeschedule.jmx;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;

import com.deve.timeschedule.quartz.QuartzScheduleWrapper;

public class AppWrapper implements AppWrapperMBean {

	private static final Log log = LogFactory.getLog(AppWrapper.class);
	
    private static AppWrapper instance = new AppWrapper();
    
	private AppWrapper(){
		
	}
	
	public static AppWrapper getInstance(){
		return instance;
	}
	
	/*
	 * App name
	 */
	private String name;

    private Integer bootStyle;
	
	/*
	 * ��map����ʽ��¼app����״̬
	 */
	private Map<String, String> status = new HashMap<String, String>();
	
	public Map<String, String> getStatus() {
		return status;
	}

	public void setStatus(Map<String, String> status) {
		this.status = status;
	}

//	public AppWrapper(String name, Integer bootStyle) {
//		this.name = name;
//		this.bootStyle = bootStyle;
//	}
//	
//	public AppWrapper(String name) {
//		this.name = name;
//		this.bootStyle = 1;
//	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getBootStyle() {
		return bootStyle;
	}

	public void setBootStyle(Integer bootStyle) {
		this.bootStyle = bootStyle;
	}

	public synchronized void resume() throws Exception{
		try {
			log.debug("resume schedule from jmx remote");
			Scheduler sched = QuartzScheduleWrapper.getSched();
			
			try {
				sched.resumeAll();
				setBootStyle(1);
			} catch (Exception e) {
				log.warn("failed to resume schedule", e);
				throw new RuntimeException("failed to resume schedule" + e.getMessage());
			}
			
		} catch (Exception e) {
			log.error("RMI runNow exception", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public synchronized void pause() throws Exception{
		try {
			log.debug("pause schedule from jmx remote");
			Scheduler sched = QuartzScheduleWrapper.getSched();

			try {
				sched.pauseAll();
				setBootStyle(2);
			} catch (Exception e) {
				log.warn("failed to pause schedule", e);
				throw new RuntimeException("failed to pause schedule" + e.getMessage());
			}
			
		} catch (Exception e) {
			log.error("RMI runNow exception", e);
			throw new RuntimeException(e.getMessage());
		}
	}

}
