package com.deve.timeschedule.quartz;

import java.text.ParseException;
import java.util.Map;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.simpl.RAMJobStore;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.JobStore;

import com.deve.timeschedule.exception.InvalidTriggerException;
import com.deve.timeschedule.jmx.JobWrapper;
import com.deve.timeschedule.log.Log;
import com.deve.timeschedule.log.LogFactory;
import com.deve.timeschedule.trigger.TriggerFactory;
import com.deve.timeschedule.util.EnhancerStatefulUtil;

public class QuartzScheduleWrapper {
	private static final Log log = LogFactory.getLog(QuartzScheduleWrapper.class);
	private static Scheduler sched = null;
	private static Scheduler heartSched = null;    
	

	public static void initSched(int threadCount) {

		try {
			SimpleThreadPool threadPool = new SimpleThreadPool(threadCount,
					Thread.NORM_PRIORITY);
			threadPool.initialize();
			// create the job store
			JobStore jobStore = new RAMJobStore();
			DirectSchedulerFactory.getInstance().createScheduler(threadPool,
					jobStore);

			sched = DirectSchedulerFactory.getInstance().getScheduler();
			
			
			//init heart sched
			SimpleThreadPool threadPoolHeart = new SimpleThreadPool(1,
					Thread.NORM_PRIORITY);
			threadPoolHeart.initialize();
			JobStore jobStoreHeart = new RAMJobStore();
			DirectSchedulerFactory.getInstance().createScheduler("HeartScheduler", "SIMPLE_NON_CLUSTERED", threadPoolHeart,
					jobStoreHeart);

			heartSched = DirectSchedulerFactory.getInstance().getScheduler("HeartScheduler");
		} catch (Exception e) {
			log.error("error init sched", e);
		}
	}

	public static Scheduler getSched() {
		return sched;
	}

	public static Scheduler getHeartSched() {
		return heartSched;
	}
	
	public static void schedule(JobWrapper job) throws InvalidTriggerException,
			SchedulerException, ClassNotFoundException {
		getSched();

		Trigger t = TriggerFactory.createTrigger(job.getName(), job
				.getTriggerType(), job.getTriggerStr());
		t.setName(job.getName() + "_trigger");
		
		Class clazz = Class.forName(job.getClazz());
		// if it is not allowed to create concurrent job
		if(job.isStateful()){
			clazz = EnhancerStatefulUtil.enhancer(clazz);
		}
		
		JobDetail jd = new JobDetail(job.getName(), clazz);
		if (job.getMap() != null)
			jd.setJobDataMap(new JobDataMap(job.getMap()));

		sched.scheduleJob(jd, t);
	}

	public static void refreshTrigger(String name, String triggerStr)
			throws ParseException, SchedulerException {
		CronTrigger trigger = (CronTrigger) sched.getTrigger(name + "_trigger",
				Scheduler.DEFAULT_GROUP);
		trigger.setCronExpression(triggerStr);
		sched
				.rescheduleJob(name + "_trigger", Scheduler.DEFAULT_GROUP,
						trigger);
	}

	public static void refreshTrigger(String name, Trigger trigger)
			throws SchedulerException {
		trigger.setJobGroup(Scheduler.DEFAULT_GROUP);
		trigger.setJobName(name);
		sched
				.rescheduleJob(name + "_trigger", Scheduler.DEFAULT_GROUP,
						trigger);
		
	}
	
	public static void refreshParams(String name, Map<String, String> params)
	        throws ParseException, SchedulerException {
        JobDetail jd = sched.getJobDetail(name, Scheduler.DEFAULT_GROUP);
        jd.setJobDataMap(new JobDataMap(params));
        
	}
}
