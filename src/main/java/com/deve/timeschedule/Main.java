package com.deve.timeschedule;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.deve.timeschedule.config.JobConfigParser;
import com.deve.timeschedule.exception.InvalidTriggerException;
import com.deve.timeschedule.jmx.AppWrapper;
import com.deve.timeschedule.jmx.JobWrapper;
import com.deve.timeschedule.listener.ContextListener;
import com.deve.timeschedule.log.Log;
import com.deve.timeschedule.log.LogFactory;
import com.deve.timeschedule.monitor.HeartBeatJob;
import com.deve.timeschedule.monitor.MonitorFactory;
import com.deve.timeschedule.quartz.QuartzScheduleWrapper;
import com.deve.timeschedule.util.ConfigUtil;

public class Main {

	private static final Log logger = LogFactory.getLog(Main.class);

	/**
	 * @throws IOException
	 */
	private static void init() throws IOException {
		// if(!"Main".equalsIgnoreCase(System.getProperty("start.from"))){
		// File file = new
		// File(System.getProperty("work.home")+"/logs/std.log");
		// if(!file.exists()){
		// file.createNewFile();
		// }
		// //PrintStream ps = new PrintStream(file);
		// StdPrintStream ps = new StdPrintStream(file);
		// System.setOut(ps);
		// System.setErr(ps);
		// logger.info("Redirect System.out to std.log");
		// }
	}

	/**
	 * @throws MalformedObjectNameException
	 * @throws NullPointerException
	 * @throws InstanceAlreadyExistsException
	 * @throws MBeanRegistrationException
	 * @throws NotCompliantMBeanException
	 * @throws ClassNotFoundException
	 */
	public static void start() throws MalformedObjectNameException,
			NullPointerException, InstanceAlreadyExistsException,
			MBeanRegistrationException, NotCompliantMBeanException,
			ClassNotFoundException {
		try {
			System.getProperties().list(System.out);
			
			init();
		} catch (IOException e) {
			logger.error("fatal error - init failed", e);
			System.exit(1);
		}

		List<JobWrapper> jobs = null;
		try {
			jobs = JobConfigParser.getJobsFromConfig();
		} catch (Exception e) {
			logger.error("fatal error", e);
			System.exit(1);
		}
		if (jobs == null || jobs.size() == 0) {
			logger.warn("no any jobs found in config file.");
			System.exit(1);
		}
		
		try {
			boolean result = fireStartupListener();
			if(!result){
				logger.error("The result for the initializtion is false, exit");
				System.exit(1);
			}
				
		} catch (Exception e) {
			logger.error("Failed to fire startup listener", e);
			System.exit(1);
		}
		// get a reference to a quartz scheduler
		Scheduler sched = QuartzScheduleWrapper.getSched();
		// mbaen agent
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		
		//register app
		try{
			AppWrapper app = AppWrapper.getInstance();
			app.setName(JobConfigParser.getJobs().getAppName());
			app.setBootStyle(JobConfigParser.getJobs().getBootStyle());
			ObjectName mbeanAppName = new ObjectName("ec_scheduler:appname="
					+ JobConfigParser.getJobs().getAppName());
			server.registerMBean(app, mbeanAppName);
		} catch (Exception e){
			logger.error("failed to register app", e);
			System.exit(1);
		}
		
		// add config job into schedule
		for (JobWrapper job : jobs) {
			try {
				QuartzScheduleWrapper.schedule(job);

				ObjectName mbeanName = new ObjectName("ec_scheduler:name="
						+ job.getName());
				server.registerMBean(job, mbeanName);

			} catch (InvalidTriggerException e) {
				logger.error("invalid trigger expression", e);
				System.exit(1);
			} catch (SchedulerException e) {
				logger.error("failed to shedule a job", e);
				System.exit(1);
			}
		}
		
		/*// Create an JMXMP connector server
		logger.debug("\nCreate an JMXMP connector server");
        JMXServiceURL url;
		try {
			String host ="127.0.0.1";
			int port = 9999;
			url = new JMXServiceURL("service:jmx:jmxmp://"+host+":"+port+"/server");
			JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
	        // Start the JMXMP connector server
	        logger.debug("\nStart the JMXMP connector server");
	        cs.start();
	        logger.debug("\nJMXMP connector server successfully started");
	        logger.debug("\nWaiting for incoming connections in host:"+ host +":" + port);
		} catch (Exception e1) {
			logger.error("fail to start the JMXMP connector server",e1);
			System.exit(1);
		}*/
        
        
        
		// �����������ȷ���Ƿ�ʼ�������
		if(JobConfigParser.getJobs().getBootStyle() == 1 ){
			// start execution
			try {
				sched.start();
			} catch (SchedulerException e) {
				logger.error("failed to start container", e);
				System.exit(1);
			}
		}

		// ����shutdown�˿�
		try {
			logger.info("add shutdown hook.");
			new Server(ConfigUtil.getShutDownPort());
		} catch (IOException e) {
			logger.error("cannot listener shutdown port", e);
			System.exit(1);
		}
		logger.info("scheduler container started.");

		// ��������Ϣ��������ط�����
		try {
			MonitorFactory.getHttpMonitor().sendConfigInfo();
		} catch (Exception e) {
			logger.warn("failed to send config info" + e);
			// System.exit(1);
		}

		//logger.debug(QuartzScheduleWrapper.getHeartSched() == QuartzScheduleWrapper.getSched());
		// ��ʼ���������
		try {
			HeartBeatJob.heartBeat();
			QuartzScheduleWrapper.getHeartSched().start();
		} catch (Exception e) {
			logger.error("failed to start heart beat job", e);
			// System.exit(1);
		}
	}

	private static boolean fireStartupListener() throws Exception {
		// figure out listener
		String listenerClass = JobConfigParser.getJobs().getListener();
		if (listenerClass == null)
			return true;

		ContextListener listener = (ContextListener) Class.forName(
				listenerClass).newInstance();
		try{
			return listener.onStartup();
		}catch(Exception e){
			throw e;
		}
	}

	private static void fireShutdownListener() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		// figure out listener
		String listenerClass = JobConfigParser.getJobs().getListener();
		if (listenerClass == null)
			return;

		ContextListener listener = (ContextListener) Class.forName(
				listenerClass).newInstance();
		listener.onShutdown();
	}

	public static void stop() {
		logger.info("shutdown scheduler container");
		try {
			//shutdown job sched
			QuartzScheduleWrapper.getSched().shutdown(true);
			//shutdown heartSched
			QuartzScheduleWrapper.getHeartSched().shutdown(true);
		} catch (SchedulerException e) {
			logger.error("failed to shut down scheduler", e);
		}
		try {
			fireShutdownListener();
		} catch (Exception e) {
			logger.error("Failed to fire startup listener", e);
		}
	}

	public static void restart() throws MalformedObjectNameException,
			InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, NullPointerException,
			ClassNotFoundException {
		stop();
		start();
	}

	public static void main(String[] args) throws MalformedObjectNameException,
			InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, NullPointerException,
			ClassNotFoundException, IOException {
		System.setProperty("work.home", System.getProperty("user.dir"));
		System.setProperty("start.from", "Main");
		if(logger.isTraceEnabled()){
			logger.info("work.home is " + System.getProperty("user.dir"));
			logger.info("start.from is Main");
		}
		Main.start();
	}

}
