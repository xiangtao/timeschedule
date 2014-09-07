package com.deve.timeschedule.jmx;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import com.deve.timeschedule.config.JobConfigParser;
import com.deve.timeschedule.quartz.QuartzScheduleWrapper;
import com.deve.timeschedule.quartz.RecoverTriggerListener;
import com.deve.timeschedule.trigger.TriggerFactory;

public class JobWrapper implements JobWrapperMBean {

	private static final Log log = LogFactory.getLog(JobWrapper.class);

	/*
	 * Job name
	 */
	private String name;

	/*
	 * Job class name which value shall be from config file
	 */
	private String clazz;

	/*
	 * Trigger info
	 */
	private String triggerStr;
	private String triggerType;

	/*
	 * parameters
	 */
	private Map map;

	/*
	 * ��ʼִ��ʱ�䣬ִ���������
	 */
	protected long startExecutingTime = -1;
	protected long executingTimes = 0;

	/*
	 * �����ִ��ʱ�䣬���ִ��ʱ�� ��λΪms
	 */
	protected long shorttestExecutingTime = 0;
	protected long longestExecutingTime = 0;

	/*
	 * ��map����ʽ��¼���е�jobWrapper��jobName vs jobWrapper����
	 */
	private static Map<String, JobWrapper> jobWrapperMap = new HashMap<String, JobWrapper>();
	
	private boolean stateful;

	/*
	 * ��map����ʽ��¼job����״̬
	 */
	private Map<String, String> status = new HashMap<String, String>();
	
	public Map<String, String> getStatus() {
		return status;
	}

	public void setStatus(Map<String, String> status) {
		this.status = status;
	}

	public static void setStatus(String name, Map<String, String> status) {
		JobWrapper wrapper = jobWrapperMap.get(name);
		wrapper.setStatus(status);
	}
	
	public boolean isStateful() {
		return stateful;
	}

	public void setStateful(boolean stateful) {
		this.stateful = stateful;
	}

	public JobWrapper(String name) {
		if (jobWrapperMap.containsKey(name)) {
			throw new IllegalArgumentException(
					"Job name could not be duplicated!!!");
		}
		this.name = name;
		jobWrapperMap.put(name, this);
	}

	public long getStartExecutingTime() {
		return startExecutingTime;
	}

	public void setStartExecutingTime(long startExecutingTime) {
		this.startExecutingTime = startExecutingTime;
	}

	public long getExecutingTimes() {
		return executingTimes;
	}

	public void setExecutingTimes(long executingTimes) {
		this.executingTimes = executingTimes;
	}

	public long getShorttestExecutingTime() {
		return shorttestExecutingTime;
	}

	public void setShorttestExecutingTime(long shorttestExecutingTime) {
		this.shorttestExecutingTime = shorttestExecutingTime;
	}

	public long getLongestExecutingTime() {
		return longestExecutingTime;
	}

	public void setLongestExecutingTime(long longestExecutingTime) {
		this.longestExecutingTime = longestExecutingTime;
	}

	public String getName() {
		return name;
	}

	public String getTriggerStr() {
		return triggerStr;
	}

	public void refreshTriggerStr(String str) throws Exception {
		log.debug("Refresh trigger string: " + this.triggerStr + "->" + str);
		this.triggerStr = str;
		try {
			QuartzScheduleWrapper.refreshTrigger(name, triggerStr);
			JobConfigParser.refreshTrigger(name, triggerStr);
		} catch (Exception e) {
			log.error("refreshTrigger error", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public void refreshParams(Map<String, String> params) throws Exception {
		log.debug("Refresh params: " + this.map.toString() + "->" + params.toString());
		this.map = params;
		try {
			QuartzScheduleWrapper.refreshParams(name, map);
			JobConfigParser.refreshParams(name, map);
		} catch (Exception e) {
			log.error("refreshParams error", e);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public void setTriggerStr(String triggerStr) {
		this.triggerStr = triggerStr;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public synchronized static void start(String jobName) {
		JobWrapper wrapper = jobWrapperMap.get(jobName);
		if (wrapper.getStartExecutingTime() == -1)
			wrapper.setStartExecutingTime(System.currentTimeMillis());
	}

	public synchronized static void end(String jobName, long startTime) {
		long time = startTime;

		JobWrapper wrapper = jobWrapperMap.get(jobName);
		wrapper.setExecutingTimes(wrapper.getExecutingTimes() + 1);
		long now = System.currentTimeMillis();

		long shorttestExecutingTime = (wrapper.getShorttestExecutingTime() == 0 || now
				- time < wrapper.getShorttestExecutingTime()) ? now - time
				: wrapper.getShorttestExecutingTime();
		long longestExecutingTime = (wrapper.getLongestExecutingTime() == 0 || now
				- time > wrapper.getLongestExecutingTime()) ? now - time
				: wrapper.getLongestExecutingTime();

		wrapper.setShorttestExecutingTime(shorttestExecutingTime);
		wrapper.setLongestExecutingTime(longestExecutingTime);
	}

	public synchronized void runNowOld() throws SchedulerException {
		try {
			log.debug("runNow from jmx remote");
			/*
			 * ��� ֮ǰ��trigger
			 */
			Scheduler sched = QuartzScheduleWrapper.getSched();
			Trigger oldTrigger = sched.getTrigger(name + "_trigger",
					Scheduler.DEFAULT_GROUP);

			/*
			 * ������ע��listener
			 */
			RecoverTriggerListener lis = new RecoverTriggerListener(oldTrigger);
			sched.addJobListener(lis);
			JobDetail detail = sched
					.getJobDetail(name, Scheduler.DEFAULT_GROUP);
			detail.addJobListener(RecoverTriggerListener.NAME);

			/*
			 * ����һ���µĵ���
			 */
			sched.deleteJob(name, Scheduler.DEFAULT_GROUP);
			Trigger newTrigger = TriggerFactory.createImmediateTrigger(name
					+ "_trigger");
			sched.scheduleJob(detail, newTrigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error("RMI runNow exception", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public synchronized void runNow() {
		try {
			log.debug("runNow from jmx remote");
			
			// ���ǰjob���Ѿ�ע���listener������ϴ�job��δִ�����
			Scheduler sched = QuartzScheduleWrapper.getSched();
			JobDetail detail = sched
					.getJobDetail(name, Scheduler.DEFAULT_GROUP);
			if (ArrayUtils.contains(detail.getJobListenerNames(), RecoverTriggerListener.NAME)) {
				throw new RuntimeException("Job still running");
			}

			/*
			 * ��� ֮ǰ��trigger
			 */
			Trigger oldTrigger = sched.getTrigger(name + "_trigger",
					Scheduler.DEFAULT_GROUP);

			/*
			 * ������ע��listener
			 */
			RecoverTriggerListener lis = new RecoverTriggerListener(oldTrigger);

			// �����ظ�ע��listener�쳣
			try {
				sched.addJobListener(lis);
				detail.addJobListener(RecoverTriggerListener.NAME);
			} catch (Exception e) {
				log.warn("failed to registered listener", e);
				throw new RuntimeException("Job still running");
			}
			/*
			 * ����һ���µĵ���
			 */
			sched.deleteJob(name, Scheduler.DEFAULT_GROUP);
			Trigger newTrigger = TriggerFactory.createImmediateTrigger(name
					+ "_trigger");
			sched.scheduleJob(detail, newTrigger);
		} catch (Exception e) {
			log.error("RMI runNow exception", e);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	
}
