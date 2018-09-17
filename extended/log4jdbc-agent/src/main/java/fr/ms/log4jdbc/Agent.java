package fr.ms.log4jdbc;

import java.lang.instrument.Instrumentation;

import fr.ms.log4jdbc.transformer.ConnectionPoolDataSourceTransformer;
import fr.ms.log4jdbc.transformer.DataSourceTransformer;
import fr.ms.log4jdbc.transformer.DriverTransformer;
import fr.ms.log4jdbc.transformer.XADataSourceTransformer;

public class Agent {

	public static void premain(final String agentArgs, final Instrumentation instrumentation) throws Exception {
		instrumentation.addTransformer(new DriverTransformer());

		instrumentation.addTransformer(new DataSourceTransformer());

		instrumentation.addTransformer(new ConnectionPoolDataSourceTransformer());

		instrumentation.addTransformer(new XADataSourceTransformer());

		System.out.println("**************************** Log4Jdbc Agent Enabled ****************************");
	}
}
