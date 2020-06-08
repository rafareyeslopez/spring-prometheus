package com.rafareyeslopez;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

	@Query("SELECT u FROM User u")
	Stream<User> findAllUsersForBillingWith();

}