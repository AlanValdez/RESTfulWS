package me.restfulws.utm.config;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Controller;
import org.springframework.util.ErrorHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
@EnableAsync
@EnableScheduling
@ComponentScan(
		basePackageClasses = {
			   		me.restfulws.utm.web.ComponentPackageMaker.class,
			   		me.restfulws.utm.repository.ComponentPackageMaker.class,
			   		me.restfulws.utm.service.ComponentPackageMaker.class,
			   		me.restfulws.utm.component.ComponentPackageMaker.class }, 
		excludeFilters = @ComponentScan.Filter({
			Controller.class, 
			ControllerAdvice.class}))
public class RootContextConfig implements AsyncConfigurer, SchedulingConfigurer {
	private static final Logger logger = LogManager.getLogger(); 
	private static final Logger schedulingLogger = LogManager.getLogger(String.format("%s-schedue", logger.getName()));
	
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
		return mapper;
	}

	@Bean
	public Jaxb2Marshaller jaxb2Marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setPackagesToScan(new String[] { "me.restfulws.utm.model" });
		return marshaller;
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		TaskScheduler taskScheduler = this.taskScheduler();
		taskRegistrar.setTaskScheduler(taskScheduler);
	}
	
	@Override
	public Executor getAsyncExecutor() {
		Executor executor = this.taskScheduler();
		return executor;
	}
	
	@Bean
	public ThreadPoolTaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(20);
		taskScheduler.setThreadNamePrefix("job-");
		taskScheduler.setAwaitTerminationSeconds(60);
		taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
		taskScheduler.setErrorHandler(new ErrorHandler() {
			@Override
			public void handleError(Throwable t) {
				schedulingLogger.error("Unknown handling job {}", t);
			}
		});
		taskScheduler.setRejectedExecutionHandler(new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				schedulingLogger.error("Job rejected {}", r);
			}
		});
		return taskScheduler;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return null;
	}
	
	@Bean
	public JavaMailSender javaMailSender() {
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    mailSender.setHost("smtp.gmail.com");
	    mailSender.setPort(587);
	    mailSender.setUsername("me@jose-manuel.me");
	    mailSender.setPassword("hhthlspdqidujhrj");
	    Properties mailProperties = mailSender.getJavaMailProperties();
	    mailProperties.put("mail.transport.protocol", "smtp");
	    mailProperties.put("mail.smtp.auth", "true");
	    mailProperties.put("mail.smtp.starttls.enable", "true");
	    mailProperties.put("mail.debug", "false");
	    return mailSender;
	}
}