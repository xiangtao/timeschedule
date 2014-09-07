package com.deve.timeschedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.deve.timeschedule.log.Log;
import com.deve.timeschedule.log.LogFactory;
import com.deve.timeschedule.util.ConfigUtil;

public class ShutDown {

	private static final Log logger = LogFactory.getLog(ShutDown.class);

	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		initWorkingHome();
		new ShutDown(ConfigUtil.getShutDownPort());
	}

	public ShutDown(int serverPort) {
		try {
			socket = new Socket("127.0.0.1", serverPort);
			reader = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
			writer.println("shutdown");
			writer.flush();
			writer.close();
			reader.close();
			socket.close();
		} catch (Exception e) {
			logger.error("sending shutdown cmd error", e);
		}
	}
	
	private static void initWorkingHome() {
		if (System.getProperty("work.home") != null)
            return;
		File startupBat = 
            new File(System.getProperty("user.dir"), "startup.bat");
		if (startupBat.exists()) {
			try {
				System.setProperty("work.home", (new File(System.getProperty("user.dir"), "..")).getCanonicalPath());
			} catch (IOException e) {
				// Ignore
	            System.setProperty("work.home",System.getProperty("user.dir"));
			}
		}else{
			System.setProperty("work.home",
                    System.getProperty("user.dir"));
		}
		if(logger.isDebugEnabled()){
			logger.debug("work.home is --->" + System.getProperty("work.home"));
		}
	}
}
