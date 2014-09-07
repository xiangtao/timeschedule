package com.deve.timeschedule;

import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.deve.timeschedule.jmx.JobWrapper;
import com.deve.timeschedule.log.Log;
import com.deve.timeschedule.log.LogFactory;
import com.deve.timeschedule.monitor.MonitorFactory;
import com.deve.timeschedule.quartz.QuartzScheduleWrapper;

public abstract class CoreJob implements Job {
	private static final Log log = LogFactory.getLog(CoreJob.class);
	private JobExecutionContext ctx;
	
	
	public abstract void run() throws Exception;
	
	
	
	public final void execute(JobExecutionContext ctx)
			throws JobExecutionException {
		String jobName = ctx.getJobDetail().getName();
		this.ctx = ctx;
		JobWrapper.start(jobName);

		long time = System.currentTimeMillis();
		try {
			run();
		} catch (Exception e) {
			log.error("Exception when execute " + jobName, e);
			MonitorFactory.getHttpMonitor().sendException(jobName, e);
		}
		JobWrapper.end(jobName, time);
	}
	public String getParamByName(String name) {
		if (ctx == null)
			return null;
		JobDataMap map = ctx.getJobDetail().getJobDataMap();
		if (map == null)
			return null;
		return map.getString(name);
	}

	public String getJobName() {
		if (ctx == null)
			return null;
		return ctx.getJobDetail().getName();
	}
	public void unschedulerJob() throws SchedulerException{
		QuartzScheduleWrapper.getSched().unscheduleJob(getJobName()+"_trigger", Scheduler.DEFAULT_GROUP);
	}
	public void setStatus(Map<String, String> status){
		JobWrapper.setStatus(getJobName(), status);
	}
	
}
