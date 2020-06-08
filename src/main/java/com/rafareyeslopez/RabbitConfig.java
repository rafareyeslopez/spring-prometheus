
package com.rafareyeslopez;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @project spring-batch-mysql
 * @package com.rafareyeslopez
 * @date 2020-06-04
 * @author Rafael Reyes Lopez
 *
 */
@Configuration
public class RabbitConfig {
	@Bean
	public Queue jpaQueue() {
		return new Queue("jpaQueue", false);
	}

	@Bean
	public Queue jpastreamQueue() {
		return new Queue("jpastreamQueue", false);
	}

	@Bean
	public Queue jdbcQueue() {
		return new Queue("jdbcQueue", false);
	}

	@Bean
	public Queue batchQueue() {
		return new Queue("batchQueue", false);
	}
}
