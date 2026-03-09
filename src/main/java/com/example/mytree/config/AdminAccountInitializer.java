package com.example.mytree.config;

import java.time.Clock;
import java.time.LocalDateTime;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.mytree.domain.User;
import com.example.mytree.repository.UserRepository;

@Configuration
public class AdminAccountInitializer {

	private static final String ADMIN_USER_ID = "admin";
	private static final String ADMIN_PASSWORD = "963963";
	private static final String ADMIN_NAME = "관리자";
	private static final String ADMIN_IP_ADDRESS = "127.0.0.1";

	@Bean
	ApplicationRunner adminAccountRunner(UserRepository userRepository, Clock clock) {
		return args -> {
			User adminUser = userRepository.findByUserId(ADMIN_USER_ID);

			if (adminUser == null) {
				userRepository.insert(
					new User(
						ADMIN_USER_ID,
						ADMIN_PASSWORD,
						ADMIN_NAME,
						LocalDateTime.now(clock),
						ADMIN_IP_ADDRESS
					)
				);
				return;
			}

			adminUser.setPassword(ADMIN_PASSWORD);
			adminUser.setName(ADMIN_NAME);
			userRepository.update(adminUser);
		};
	}
}
