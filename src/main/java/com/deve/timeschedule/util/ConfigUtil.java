package com.deve.timeschedule.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import com.deve.timeschedule.jmx.JmxAuth;
import com.deve.timeschedule.log.Log;
import com.deve.timeschedule.log.LogFactory;
public class ConfigUtil {
	private static final Log log = LogFactory.getLog(ConfigUtil.class);
	private static final String PROPERTIES_FILE = System
			.getProperty("work.home")
			+ "/conf/scheduler.properties";
	private static Properties prop;
	static {
		try {
			prop = new Properties();
			prop.load(new FileInputStream(PROPERTIES_FILE));
		} catch (Exception e) {
			log.error("fail to load " + PROPERTIES_FILE, e);
			System.exit(1);
		}

	}

	public static String getMonitorAddr() {
		return prop.getProperty("monitor.server");
	}

	public static String getMonitorSendConfigAddr() {
		return getMonitorAddr() + prop.getProperty("monitor.send.config");
	}

	public static String getMonitorHeartBeatAddr() {
		return getMonitorAddr() + prop.getProperty("monitor.heartBeat");
	}

	public static int getHeartBeatInterval() {
		try {
			int ret = Integer.parseInt(prop
					.getProperty("monitor.heartBeat.interval"));
			return ret;
		} catch (Exception e) {
			log.error("fail to get heart beat interval", e);
			System.exit(1);
		}
		return 30 * 1000;
	}
	
	public static String getHeartBeatTrigger() {
		try {
			String ret = prop
					.getProperty("monitor.heartBeat.hearttrigger");
			return ret;
		} catch (Exception e) {
			log.error("fail to get heart beat trigger", e);
			System.exit(1);
		}
		return "0/30 * * ? * *";
	}
	
	public static String getShutDownCommand(){
		return prop.getProperty("scheduler.shutdown.command");
	}
	
	public static int getShutDownPort(){
		try {
			int ret = Integer.parseInt(prop
					.getProperty("scheduler.shutdown.port"));
			return ret;
		} catch (Exception e) {
			log.error("fail to get shutdown port", e);
			System.exit(1);
		}
		return 8888;
	}
	
	public static String getSendExceptionUrl(){
		return getMonitorAddr() + prop.getProperty("monitor.send.exception");
	}
	
	/**
	 * Return JMX authentication object which reads from jmx password file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static JmxAuth getJmxAuth() throws FileNotFoundException, IOException{
		String auth = System.getProperty("com.sun.management.jmxremote.authenticate");
		String fileName = System.getProperty("com.sun.management.jmxremote.password.file");

		if("false".equalsIgnoreCase(auth) || fileName == null){
			return null;
		}
				
		Properties tmp = new Properties();
		tmp.load(new FileInputStream(fileName));
		Iterator it = tmp.keySet().iterator();
		while(it.hasNext()){
			String name = (String) it.next();
			String pwd = tmp.getProperty(name);
			JmxAuth jmxauth = new JmxAuth(name, pwd);
			return jmxauth;
		}
		return null;
	}
}
