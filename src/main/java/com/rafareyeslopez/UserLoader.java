package com.rafareyeslopez;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserLoader implements ApplicationListener<ContextRefreshedEvent> {

	private UserRepository userRepository;

	@Autowired
	public void setProductRepository(final UserRepository productRepository) {
		this.userRepository = productRepository;
	}

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		log.info("Loading data");
		final List<User> userList = new ArrayList();
		for (int i = 0; i < 5000000; i++) {

			userList.add(new User(RandomStringUtils.randomAlphabetic(20)));

			log.debug("Generated user n " + i);
		}

		userRepository.saveAll(userList);
		log.info("Finished loading data");
	}
}