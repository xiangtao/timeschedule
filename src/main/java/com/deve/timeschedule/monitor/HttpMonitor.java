package com.deve.timeschedule.monitor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import com.deve.timeschedule.config.JobConfigParser;
import com.deve.timeschedule.jmx.AppWrapper;
import com.deve.timeschedule.jmx.JmxAuth;
import com.deve.timeschedule.log.Log;
import com.deve.timeschedule.log.LogFactory;
import com.deve.timeschedule.out.StdPrintStream;
import com.deve.timeschedule.util.ConfigUtil;

public class HttpMonitor implements IMonitor {
	private static final Log log = LogFactory.getLog(HttpMonitor.class);
	private static HttpMonitor instance = new HttpMonitor();

	private HttpClient client;

	public static HttpMonitor getInstance() {
		return instance;
	}

	private HttpMonitor() {
		client = new HttpClient();
		client.setTimeout(10000);
	}

	public void sendException(String taskName, Exception e) {
		PostMethod method = new PostMethod(ConfigUtil.getSendExceptionUrl());
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();		
		PrintStream ps = new PrintStream(bos);
		try {
			method.addParameter("appName", JobConfigParser.getAppName());
			method.addParameter("taskName", taskName);
			method.addParameter("exceptionName",e.getClass().getName());
			e.printStackTrace(ps);
			method.addParameter("exceptionMessage", new String(bos.toByteArray()));
			client.executeMethod(method);			
		} catch (Exception e1) {
			log.warn("failed to send exception");
		} finally {
			method.releaseConnection();
			ps.close();
		}
	}

	public void heartBeatMonitor() throws HttpException, IOException {
		PostMethod method = new PostMethod(ConfigUtil.getMonitorHeartBeatAddr());
		try {
			method.addParameter("appName", JobConfigParser.getAppName());
			method.addParameter("bootStyle", AppWrapper.getInstance().getBootStyle().toString());
			client.executeMethod(method);
			log.debug("send heat beat done");
		} catch (Exception e) {
			log.warn("failed to send heart beat");
		} finally {
			method.releaseConnection();
		}
	}

	public void sendConfigInfo() throws IOException {
		PostMethod method = new PostMethod(ConfigUtil
				.getMonitorSendConfigAddr());
		try {
			method.addParameter("appConfXml", JobConfigParser
					.getConfigFileString());
			
			JmxAuth jmxAuth = ConfigUtil.getJmxAuth();
			if(jmxAuth != null){
				log.debug("Security mode startup, authentification is required from remote accessing");
				method.addParameter("userName", jmxAuth.getUserName());
				method.addParameter("password", jmxAuth.getPassword());
			}
			
			int status = client.executeMethod(method);
			if (status == 200) {
				log.debug("successfully send config info");
			}else{
				log.warn("fialed to send config,return status code["+status+"]");
			}
		} catch (Exception e) {
			log.warn("failed to send config info"+e);
		} finally {
			method.releaseConnection();
		}
	}

}
