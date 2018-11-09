package fr.ms.log4jdbc;

import java.lang.instrument.Instrumentation;

import fr.ms.log4jdbc.javassist.JavassistMethodTransformer;
import fr.ms.log4jdbc.transformer.ConnectionPoolDataSourceTransformer;
import fr.ms.log4jdbc.transformer.DataSourceTransformer;
import fr.ms.log4jdbc.transformer.XADataSourceTransformer;

public class Agent {

	public static void premain(final String agentArgs, final Instrumentation instrumentation) throws Exception {

		final JavassistMethodTransformer javassistMethodTransformer = new JavassistMethodTransformer();

		// javassistMethodTransformer.addTransformer(new DriverTransformer());

		javassistMethodTransformer.addTransformer(new DataSourceTransformer());

		javassistMethodTransformer.addTransformer(new ConnectionPoolDataSourceTransformer());

		javassistMethodTransformer.addTransformer(new XADataSourceTransformer());

		instrumentation.addTransformer(javassistMethodTransformer);

		System.out.println("**************************** Log4Jdbc Agent Enabled ****************************");
	}
}
