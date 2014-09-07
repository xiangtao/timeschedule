package com.deve.timeschedule.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import com.deve.timeschedule.config.Jobs.Job;
import com.deve.timeschedule.config.Jobs.Job.Params.Param;
import com.deve.timeschedule.config.Jobs.Job.Trigger;
import com.deve.timeschedule.exception.InvalidJobClassException;
import com.deve.timeschedule.exception.InvalidTriggerException;
import com.deve.timeschedule.jmx.JobWrapper;
import com.deve.timeschedule.quartz.QuartzScheduleWrapper;
import com.deve.timeschedule.util.XMLUtil;

public class JobConfigParser {
	private final static String CONFIG_NAME = "scheduler.xml";
	private final static String PATH = System.getProperty("work.home")
			+ "/conf/" + CONFIG_NAME;
	private final static String PKG = "com.deve.timeschedule.config";
	private static Jobs jobs = null;

	/**
	 * Parser xml via jaxb
	 * 
	 * @return
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Jobs loadJobsFromConfig() throws JAXBException, SAXException,
			IOException {
		jobs = (Jobs) XMLUtil.unmarshall(PKG, PATH);
		if(jobs.getBootStyle() == null){
			jobs.setBootStyle(1);
		}
		return jobs;
	}
	
	
	/**
	 * Parser xml via jaxb
	 * @return
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Jobs loadJobsFromClassPath() throws JAXBException, SAXException,
			IOException {
		jobs = (Jobs) XMLUtil.unmarshall(PKG, JobConfigParser.class.getResourceAsStream(CONFIG_NAME));
		if(jobs.getBootStyle() == null){
			jobs.setBootStyle(1);
		}
		return jobs;
	}
	

	/**
	 * Turn the jaxb jobs to MonitoredJob
	 * 
	 * @return
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvalidJobClassException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws InvalidTriggerException
	 */
	public static List<JobWrapper> getJobsFromConfig() throws JAXBException,
			SAXException, IOException, InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			InvalidJobClassException, InvocationTargetException,
			InvalidTriggerException {
		ArrayList<JobWrapper> jobs = new ArrayList<JobWrapper>();

		/*
		 * Parser xml
		 */
		Jobs configJobs = loadJobsFromConfig();
		List<Job> jobList = configJobs.getJob();
		if (jobList == null)
			return null;

		QuartzScheduleWrapper.initSched(configJobs.getThreadCount());

		// Iterate the jobList one by one, and transfer it to MonitoredJob
		for (Job job : jobList) {
			JobWrapper jw = new JobWrapper(job.getName());
			jw.setStateful(job.isStateful() == null?false:job.isStateful());
			
			/*
			 * set job class name and job name
			 */
			jw.setClazz(job.getClazz());

			// extract parameters from xml config to mjob
			if (job.getParams() != null) {
				List<Param> params = job.getParams().getParam();
				if (params == null || params.size() == 0) {
					continue;
				}
				Map map = new HashMap();
				for (Param p : params) {
					String name = p.getName();
					String value = p.getValue();
					map.put(name, value);
				}
				jw.setMap(map);
			}
			// set the trigger
			Trigger t = job.getTrigger();
			if (t == null) {
				throw new InvalidTriggerException("no trigger node");
			}
			String triggerType = t.getType();
			String triggerValue = t.getValue();
			if (triggerType == null || triggerValue == null) {
				throw new InvalidTriggerException(
						"invalid trigger type or trigger value");
			}
			jw.setTriggerStr(triggerValue);
			jw.setTriggerType(triggerType);
			
			jobs.add(jw);
		}
		return jobs;
	}

	public static void refreshTrigger(String jobName, String triggerStr)
			throws FileNotFoundException, JAXBException, SAXException,
			IOException {
		if (jobName == null)
			return;

		List<Job> jobList = jobs.getJob();
		if (jobList == null)
			return;

		for (Job job : jobList) {
			if (jobName.equals(job.getName())) {
				job.getTrigger().setValue(triggerStr);
				break;
			}
		}
		FileOutputStream os = new FileOutputStream(PATH);
		XMLUtil.marshall(PKG, jobs, os);
		os.close();
	}


	public static void refreshParams(String jobName, Map<String, String> params)
		    throws FileNotFoundException, JAXBException, SAXException,
		    IOException {
		if (jobName == null)
			return;

		List<Job> jobList = jobs.getJob();
		if (jobList == null)
			return;

		for (Job job : jobList) {
			if (jobName.equals(job.getName())) {
				job.getParams().getParam().clear();
				Set<Entry<String, String>> entrys = params.entrySet();
				Iterator<Entry<String, String>> it = entrys.iterator();				
				while(it.hasNext()){
					Param p = new Param();
					Entry<String, String> et = it.next();
					p.setName(et.getKey());
					p.setValue(et.getValue());
					job.getParams().getParam().add(p);
				}
				break;
			}
		}
		FileOutputStream os = new FileOutputStream(PATH);
		XMLUtil.marshall(PKG, jobs, os);
		os.close();

    }

	
	/**
	 * ���ַ����ʽ���ص�ǰ�����ļ�
	 * @return
	 * @throws IOException
	 */
	public static String getConfigFileString() throws IOException {
		StringBuffer buffer = new StringBuffer();
		FileInputStream fis = new FileInputStream(PATH);
		InputStreamReader isr = new InputStreamReader(fis, "UTF8");
		Reader in = new BufferedReader(isr);
		int ch;
		while ((ch = in.read()) > -1) {
			buffer.append((char) ch);
		}
		in.close();
		return buffer.toString();
	}
	
	public static String getAppName(){
		return jobs.getAppName();
	}

	public static Jobs getJobs() {
		return jobs;
	}
}
