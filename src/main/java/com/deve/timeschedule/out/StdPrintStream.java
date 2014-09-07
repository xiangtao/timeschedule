package com.deve.timeschedule.out;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import com.deve.timeschedule.log.Log;
import com.deve.timeschedule.log.LogFactory;

public class StdPrintStream extends PrintStream {
	private static final Log log = LogFactory.getLog(StdPrintStream.class);
	public StdPrintStream(File file) throws FileNotFoundException {
		super(file);
		redirectSystemOut();
	}

	public void redirectSystemOut() {
		System.setOut(this);
	}

	public void println(boolean x) {
		log.debug(Boolean.valueOf(x)+"");
	}

	public void println(char x) {
		log.debug(Character.valueOf(x)+"");
	}

	public void println(char[] x) {
		log.debug(x == null ? null : new String(x));
	}

	public void println(double x) {
		log.debug(Double.valueOf(x)+"");
	}

	public void println(float x) {
		log.debug(Float.valueOf(x)+"");
	}

	public void println(int x) {
		log.debug(Integer.valueOf(x)+"");
	}

	public void println(long x) {
		log.debug(x+"");
	}

	public void println(Object x) {
		log.debug(x+"");
	}

	public void println(String x) {
		log.debug(x);
	}
}
