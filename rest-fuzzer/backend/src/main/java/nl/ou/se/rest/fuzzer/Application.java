package nl.ou.se.rest.fuzzer;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableAutoConfiguration
public class Application {

	public static void main(String[] args) {
	    SpringApplication.run(Application.class, args);		
	}

	@Bean("TaskExecutor")
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(2);
		threadPoolTaskExecutor.setMaxPoolSize(10);
		threadPoolTaskExecutor.setQueueCapacity(20);
		threadPoolTaskExecutor.setThreadNamePrefix("task-executor-");
		threadPoolTaskExecutor.initialize();
		return threadPoolTaskExecutor;
	}
}