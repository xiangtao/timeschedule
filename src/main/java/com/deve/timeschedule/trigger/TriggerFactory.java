package com.deve.timeschedule.trigger;

import java.text.ParseException;

import org.quartz.CronTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;

import com.deve.timeschedule.exception.InvalidTriggerException;
import com.deve.timeschedule.log.Log;
import com.deve.timeschedule.log.LogFactory;
public class TriggerFactory {
	private static final Log log = LogFactory.getLog(TriggerFactory.class);
	
	protected static final String SIMPLE_TRIGGER = "simple";
	protected static final String CRON_TRIGGER = "cron";

	/**
	 * create trigger via type and value
	 * @param type
	 * @param value
	 * @return
	 * @throws Exception 
	 */
	public static Trigger createTrigger(String name, String type, String value)
			throws InvalidTriggerException {
		if(SIMPLE_TRIGGER.equalsIgnoreCase(type)){
			//TODO û��ʵ��create simple trigger
			return createSimpleTrigger(value);
		}
		
		if(CRON_TRIGGER.equalsIgnoreCase(type)){
			return createCronTrigger(name, value);
		}
		
		throw new InvalidTriggerException("invalid trigger type '" + type
				+ "', it should be [" + SIMPLE_TRIGGER + ", "
				+ CRON_TRIGGER + "]");
	}
	
	public static Trigger createSimpleTrigger(String value){
		throw new RuntimeException("NOT implemented");
	}
	
	/**
	 * Make a cron trigger via the cron expression
	 * @param value
	 * @return
	 * @throws InvalidTriggerException
	 */
	public static Trigger createCronTrigger(String name, String value) throws InvalidTriggerException{
		CronTrigger ct = new CronTrigger();
		try {
			ct.setName(name);
			ct.setCronExpression(value);
		} catch (ParseException e) {
			log.error("",e);
			throw new InvalidTriggerException("invalid cron expression '"+value+"'");
		}
		return ct;
	}
	
	public static Trigger createImmediateTrigger(String name){
		return TriggerUtils.makeImmediateTrigger(name, 0, 1);
	}
}
