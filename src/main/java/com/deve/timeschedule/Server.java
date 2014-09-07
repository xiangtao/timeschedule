package com.deve.timeschedule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.deve.timeschedule.log.Log;
import com.deve.timeschedule.log.LogFactory;
import com.deve.timeschedule.util.ConfigUtil;

public class Server {
	private static final Log logger = LogFactory.getLog(CoreJob.class);

	public Server(int serverPort) throws IOException {
		final ServerSocket socket = new ServerSocket(serverPort);
		new Thread(new Runnable() {

			public void run() {
				try {
					Socket s = null;
					BufferedReader in = null;
					while (true) {
						s = socket.accept();
						in = new BufferedReader(
								new InputStreamReader(s.getInputStream(), "GBK"));
						String line = in.readLine();
						if (ConfigUtil.getShutDownCommand().equalsIgnoreCase(
								line)) {
							logger.info("shutting down");
							// stop scheduler container
							Main.stop();

							in.close();
							s.close();
							socket.close();
							logger.info("shutdown successfully");
							System.exit(1);
							break;
						}
						in.close();
						s.close();
					}
				} catch (IOException e) {
					logger.error("", e);
				}

			}
		}).start();

	}
}
