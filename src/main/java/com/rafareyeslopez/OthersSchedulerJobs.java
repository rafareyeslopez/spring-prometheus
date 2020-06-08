/*
 * Copyright (C) IDB Mobile Technology S.L. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.rafareyeslopez;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * @project spring-batch-mysql
 * @package com.rafareyeslopez
 * @date 2020-06-04
 * @author Rafael Reyes Lopez
 *
 */
@Component
@EnableScheduling
@Slf4j
public class OthersSchedulerJobs {
	@Autowired
	UserRepository userRepository;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Scheduled(cron = "0 30 12 * * *")
	@Transactional
	public void launchJobJpa() throws Exception {
		log.info(
				"Jpa Job started at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		userRepository.findAll().forEach(u -> rabbitTemplate.convertAndSend("jpaQueue", u.toString()));
		log.info("Jpa Job end at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	}

	@Scheduled(cron = "0 35 12 * * *")
	@Transactional
	public void launchJobJpaStream() throws Exception {
		log.info("Jpa Job Stream started at "
				+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		userRepository.findAllUsersForBillingWith()
				.forEach(u -> rabbitTemplate.convertAndSend("jpastreamQueue", u.toString()));
		log.info("Jpa Stream Job end at "
				+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	}

	@Scheduled(cron = "0 40 12 * * *")
	@Transactional
	public void launchJobJdbc() throws Exception {
		log.info("Jdbc Job started at "
				+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		jdbcTemplate.query("SELECT id,name FROM user", new UserRowMapper())
				.forEach(u -> rabbitTemplate.convertAndSend("jdbcQueue", u.toString()));
		log.info("Jdbc Job end at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

	}

}
