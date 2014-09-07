package com.deve.timeschedule.util;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import org.quartz.StatefulJob;

public class EnhancerStatefulUtil {
	public static Class enhancer(Class clazz) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallbackType(MethodInterceptor.class);
		enhancer.setInterfaces(new Class[] { StatefulJob.class });
		return enhancer.createClass();
	}
}
