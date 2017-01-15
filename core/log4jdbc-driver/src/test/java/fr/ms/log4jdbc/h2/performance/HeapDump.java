package fr.ms.log4jdbc.h2.performance;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

import javax.management.MBeanServer;

public class HeapDump {
	// This is the name of the HotSpot Diagnostic MBean
	private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";

	private HeapDump() {
	}

	/**
	 * Call this method from your application whenever you want to dump the heap
	 * snapshot into a file.
	 *
	 * @param fileName
	 *            name of the heap dump file
	 * @param live
	 *            flag that tells whether to dump only the live objects
	 */
	public static void dumpHeap(String fileName, boolean live) {

		try {
			Class<?> clazz = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
			Method m = clazz.getMethod("dumpHeap", String.class, boolean.class);
			m.invoke(Singleton.hotspotMBean, fileName, live);
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception exp) {
			throw new RuntimeException(exp);
		}
	}

	private static class Singleton {
		private static final Object hotspotMBean = getHotspotMBean();
	}

	// get the hotspot diagnostic MBean from the
	// platform MBean server
	private static Object getHotspotMBean() {
		try {
			Class<?> clazz = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			Object bean = ManagementFactory.newPlatformMXBeanProxy(server, HOTSPOT_BEAN_NAME, clazz);
			return bean;
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception exp) {
			throw new RuntimeException(exp);
		}
	}
}
