package sv.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = { "classpath:network.properties" })
public class MainConfiguration {
	
	@Value("${predictor.host}")
	public static String PREDICTOR_HOST;
	@Value("${predictor.host}")
	public static int PREDICTOR_PORT;
}
