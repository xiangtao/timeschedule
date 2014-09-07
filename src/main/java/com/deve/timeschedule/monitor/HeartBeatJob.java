package com.deve.timeschedule.monitor;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import com.deve.timeschedule.exception.InvalidTriggerException;
import com.deve.timeschedule.log.Log;
import com.deve.timeschedule.log.LogFactory;
import com.deve.timeschedule.quartz.QuartzScheduleWrapper;
import com.deve.timeschedule.trigger.TriggerFactory;
import com.deve.timeschedule.util.ConfigUtil;

public class HeartBeatJob implements Job{
	private static final Log log = LogFactory.getLog(HeartBeatJob.class);
	
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		IMonitor m = MonitorFactory.getHttpMonitor();
		try {
			m.heartBeatMonitor();
		} catch (Exception e) {
			log.warn("failed to send heart beat");
		}
		
	}
	
	public static void heartBeat() throws InvalidTriggerException, SchedulerException{
		Trigger trigger = TriggerFactory.createCronTrigger("heartBeat_trigger", ConfigUtil.getHeartBeatTrigger());
		JobDetail jd = new JobDetail("heartBeat", HeartBeatJob.class);
		QuartzScheduleWrapper.getHeartSched().scheduleJob(jd, trigger);
	}

}
