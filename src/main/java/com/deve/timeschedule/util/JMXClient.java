package com.deve.timeschedule.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JMXClient {
	public static void main(String[] args) throws Exception {
		JMXServiceURL url = new JMXServiceURL(
				"service:jmx:rmi:///jndi/rmi://localhost:1090/jmxrmi");
		JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
		ObjectName mbeanName = new ObjectName("ec_scheduler:name=job1");

		// ������Domain����ӡ����
		System.out.println("Domains:---------------");
		String domains[] = mbsc.getDomains();
		for (int i = 0; i < domains.length; i++) {
			System.out.println("\tDomain[" + i + "] = " + domains[i]);
		}

		// MBean������
		System.out.println("MBean count = " + mbsc.getMBeanCount());
		MBeanInfo info = mbsc.getMBeanInfo(mbeanName);
		MBeanAttributeInfo[] ainfo = info.getAttributes();
		for (MBeanAttributeInfo a : ainfo) {
			System.out.println(a.getName() + "="
					+ mbsc.getAttribute(mbeanName, a.getName()));
		}
		//
		// //�õ�proxy�����ֱ�ӵ��õķ�ʽ
		// HelloMBean proxy = (HelloMBean)
		// MBeanServerInvocationHandler.newProxyInstance(mbsc, mbeanName,
		// HelloMBean.class, false);
		// proxy.printHello();
		// proxy.printHello("�¸�");
		//
		// //Զ�̵��õķ�ʽ
		// mbsc.invoke(mbeanName, "printHello", null, null);
		// mbsc.invoke(mbeanName, "printHello", new Object[] { "���ڴ���Ի" }, new
		// String[] { String.class.getName() });
		//
		// //��mbean����Ϣ
		// MBeanInfo info = mbsc.getMBeanInfo(mbeanName);
		// System.out.println("Hello Class: " + info.getClassName());
		// System.out.println("Hello Attriber��" +
		// info.getAttributes()[0].getName());
		// System.out.println("Hello Operation��" +
		// info.getOperations()[0].getName());
		//
		// //�õ����е�MBean��ObjectName
		// System.out.println("all ObjectName��---------------");
		// Set set = mbsc.queryMBeans(null, null);
		// for (Iterator it = set.iterator(); it.hasNext();) {
		// ObjectInstance oi = (ObjectInstance) it.next();
		// System.out.println("\t" + oi.getObjectName());
		// }

		// Connect to a running JVM (or itself) and get MBeanServerConnection
		// that has the JVM MXBeans registered in it

		try {
			// Assuming the RuntimeMXBean has been registered in mbs

			MemoryMXBean mmbean = ManagementFactory.newPlatformMXBeanProxy(
					mbsc, ManagementFactory.MEMORY_MXBEAN_NAME,
					MemoryMXBean.class);

			System.out.println("Heap memory usage max: "
					+ mmbean.getHeapMemoryUsage().getMax());
			System.out.println("NonHeap memory usage max: "
					+ mmbean.getNonHeapMemoryUsage().getMax());

			System.out.println("avaliable processors: "
					+ ManagementFactory.getOperatingSystemMXBean()
							.getAvailableProcessors());
			
			
			ThreadMXBean tmbean = ManagementFactory.newPlatformMXBeanProxy(
					mbsc, ManagementFactory.THREAD_MXBEAN_NAME,
					ThreadMXBean.class);
			System.out.println("Peak thread count: "
					+ tmbean.getPeakThreadCount());

		} catch (Exception e) {
			// Catch the exceptions thrown by ObjectName constructor
			// and MBeanServer.getAttribute method
			e.printStackTrace();
		}

		// ע��
		// mbsc.unregisterMBean(mbeanName);
		// �ر�MBeanServer����
		jmxc.close();
	}
}