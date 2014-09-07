package com.deve.timeschedule.quartz;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import com.deve.timeschedule.log.Log;
import com.deve.timeschedule.log.LogFactory;

public class RecoverTriggerListener implements JobListener {
	public static final String NAME = "RecoverTriggerListener";
	
	private static final Log log = LogFactory.getLog(RecoverTriggerListener.class);
	
	private Trigger olderTrigger;

	public RecoverTriggerListener(Trigger trigger) {
		this.olderTrigger = trigger;
	}

	public String getName() {
		return NAME;
	}

	public void jobExecutionVetoed(JobExecutionContext context) {
		// TODO Auto-generated method stub

	}

	public void jobToBeExecuted(JobExecutionContext context) {
		// TODO Auto-generated method stub

	}

	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		log.debug("job done in RecoverTriggerListener");
		JobDetail detail = context.getJobDetail();
		
		try {
			Scheduler sched = QuartzScheduleWrapper.getSched();
			detail.removeJobListener(NAME);
			sched.removeJobListener(NAME);
			sched.deleteJob(context.getJobDetail().getName(), Scheduler.DEFAULT_GROUP);
			sched.scheduleJob(detail, olderTrigger);
			
		} catch (Exception e) {
			log.error("Recover trigger failed",e);
		}
	}

	public Trigger getOlderTrigger() {
		return olderTrigger;
	}

	public void setOlderTrigger(Trigger olderTrigger) {
		this.olderTrigger = olderTrigger;
	}

}
