package com.deve.timeschedule.boot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.deve.timeschedule.log.Log;
import com.deve.timeschedule.log.LogFactory;
/**
 * start up timeschedule from container
 * @author taox
 */
public class Bootstrap {

	private static final Log log = LogFactory.getLog(Bootstrap.class);

	private static final String COMMON_JARS = "/common/";
	private static final String APP_JARS = "/app/";

	/**
	 * Daemon object used by main.
	 */
	private static Bootstrap daemon = null;

	private void init() throws MalformedURLException, SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		initWorkingHome();
		addJars();
	}

	private void initWorkingHome() {
		/*System
				.setProperty("work.home", System.getProperty("user.dir")
						+ "/../");*/
		
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
		if(log.isDebugEnabled()){
			log.debug("work.home is --->" + System.getProperty("work.home"));
		}
		
	}

	/**
	 * Add jar to system class loader
	 * @throws MalformedURLException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void addJars() throws MalformedURLException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		URLClassLoader load = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		String workHome = System.getProperty("work.home");
		String common = workHome + COMMON_JARS;
		String app = workHome + APP_JARS;

		/*
		 * Hack
		 */
		Method method = URLClassLoader.class.getDeclaredMethod("addURL",
				new Class[] { URL.class });
		method.setAccessible(true);

		URL[] urls = findJarURLs(common);
		URL[] appUrls = findJarURLs(app);

		if (urls == null)
			return;
		for (URL url : urls) {
			method.invoke(load, new Object[] { url });
		}

		if (appUrls == null)
			return;
		for (URL url : appUrls) {
			method.invoke(load, new Object[] { url });
		}
	}

	/**
	 * find all jars under specified folder
	 * @param path
	 * @return
	 * @throws MalformedURLException
	 */
	private URL[] findJarURLs(String path) throws MalformedURLException {
		File file = new File(path);
		File[] files = file.listFiles();
		if (files == null) {
			throw new RuntimeException("Invalid path: " + path);
		}
		
		//URL[] urls = new URL[files.length];
		List<URL> urls = new ArrayList<URL>();
		for (int i = 0, j = 0; i < files.length; i++) {
			if (files[i].getName().indexOf(".jar") != -1) {
				log.debug("find jar : " + path + files[i].getName());
				/*urls[j] = files[i].toURL();
				j++;*/
				urls.add(files[i].toURL());
			}
		}
		//return urls;
		return urls.toArray(new URL[0]);
	}

	/**
	 * invoke entrance method of framework
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws MalformedURLException
	 */
	public void start() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, InvocationTargetException,
			MalformedURLException {
		init();
		Class clazz = Class.forName("com.letv.taskplugin.Main");
		Object scheduler = clazz.newInstance();

		Method method = clazz.getMethod("start", new Class[] {});
		method.invoke(scheduler, new Object[] {});
	}

	public static void main(String[] args) {
		daemon = new Bootstrap();
		try {
			daemon.start();
		} catch (Exception e) {
			log.error("Failed to start timescheduler server ", e);
			System.exit(1);
		}
	}
}
