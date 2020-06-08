package com.rafareyeslopez;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class UserRowMapper implements RowMapper<User> {

	@Override
	public User mapRow(final ResultSet rs, final int rowNum) throws SQLException {
		final User user = new User();
		user.setId(rs.getInt("id"));
		user.setName(rs.getString("name"));

		return user;
	}

}