package com.rafareyeslopez;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.ScheduledMethodRunnable;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableBatchProcessing
@EnableScheduling
@Slf4j
public class SpringBatchScheduler {

	private final AtomicBoolean enabled = new AtomicBoolean(true);

	private final AtomicInteger batchRunCounter = new AtomicInteger(0);

	private final Map<Object, ScheduledFuture<?>> scheduledTasks = new IdentityHashMap<>();

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	DataSource dataSource;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Scheduled(cron = "0 45 12 * * *")
	public void launchJob() throws Exception {
		final Date date = new Date();
		log.info("scheduler starts at "
				+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		if (enabled.get()) {
			final JobExecution jobExecution = jobLauncher().run(job(),
					new JobParametersBuilder().addDate("launchDate", date).toJobParameters());
			batchRunCounter.incrementAndGet();
			log.debug("Batch job ends with status as " + jobExecution.getStatus());
		}
		log.info("scheduler ends at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	}

	public void stop() {
		enabled.set(false);
	}

	public void start() {
		enabled.set(true);
	}

	@Bean
	public TaskScheduler poolScheduler() {
		return new CustomTaskScheduler();
	}

	private class CustomTaskScheduler extends ThreadPoolTaskScheduler {

		private static final long serialVersionUID = -7142624085505040603L;

		@Override
		public ScheduledFuture<?> scheduleAtFixedRate(final Runnable task, final long period) {
			final ScheduledFuture<?> future = super.scheduleAtFixedRate(task, period);

			final ScheduledMethodRunnable runnable = (ScheduledMethodRunnable) task;
			scheduledTasks.put(runnable.getTarget(), future);

			return future;
		}

	}

	public void cancelFutureSchedulerTasks() {
		scheduledTasks.forEach((k, v) -> {
			if (k instanceof SpringBatchScheduler) {
				v.cancel(false);
			}
		});
	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job").start(readUsers()).build();
	}

	@Bean
	public JobLauncher jobLauncher() throws Exception {
		final SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}

	@Bean
	public JobRepository jobRepository() throws Exception {
		final MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean();
		factory.setTransactionManager(new ResourcelessTransactionManager());
		return factory.getObject();
	}

	@Bean
	protected Step readUsers() {
		return stepBuilderFactory.get("readUsers").<User, User>chunk(1000).reader(reader()).writer(writer()).build();
	}

	@Bean
	public JdbcCursorItemReader<User> reader() {
		final JdbcCursorItemReader<User> reader = new JdbcCursorItemReader<>();
		reader.setDataSource(dataSource);
		reader.setSql("SELECT id,name FROM user");
		reader.setRowMapper(new UserRowMapper());

		return reader;
	}

	@Bean
	public ItemWriter<User> writer() {
		return items -> {
			log.debug("writer..." + items.size());
			for (final User item : items) {
				log.debug(item.toString());
				rabbitTemplate.convertAndSend("batchQueue", item.toString());
			}
			log.debug("Finished to publish all items");
		};

	}

	public AtomicInteger getBatchRunCounter() {
		return batchRunCounter;
	}

}