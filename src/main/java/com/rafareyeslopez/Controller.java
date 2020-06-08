/*
 * Copyright (C) IDB Mobile Technology S.L. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.rafareyeslopez;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @project spring-batch-mysql
 * @package com.rafareyeslopez
 * @date 2020-06-04
 * @author Rafael Reyes Lopez
 *
 */
@RestController
public class Controller {

	@Autowired
	OthersSchedulerJobs othersSchedulerJobs;

	@Autowired
	SpringBatchScheduler springBatchScheduler;

	@GetMapping("/jpa")
	public String sendItemsJpa() throws Exception {

		othersSchedulerJobs.launchJobJpa();
		return "ok";
	}

	@GetMapping("/jpastream")
	public String sendItemsJpaStream() throws Exception {

		othersSchedulerJobs.launchJobJpaStream();
		return "ok";
	}

	@GetMapping("/jdbc")
	public String sendItemsJdbc() throws Exception {

		othersSchedulerJobs.launchJobJdbc();
		return "ok";
	}

	@GetMapping("/batch")
	public String sendItemsBatch() throws Exception {

		springBatchScheduler.launchJob();
		return "ok";
	}

}
