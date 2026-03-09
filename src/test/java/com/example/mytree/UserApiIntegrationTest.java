package com.example.mytree;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.mytree.domain.User;
import com.example.mytree.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class UserApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		userRepository.findAll().forEach(user -> userRepository.deleteByUserId(user.getUserId()));
	}

	@Test
	void signUpStoresUserWithIpv4() throws Exception {
		String requestBody = """
			{
			  "userId": "hong01",
			  "password": "pw1234",
			  "name": "홍길동"
			}
			""";

		mockMvc.perform(post("/api/users/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
				.with(request -> {
					request.setRemoteAddr("127.0.0.1");
					return request;
				}))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.userId").value("hong01"))
			.andExpect(jsonPath("$.name").value("홍길동"))
			.andExpect(jsonPath("$.ipAddress").value("127.0.0.1"));

		User savedUser = userRepository.findByUserId("hong01");
		assertThat(savedUser).isNotNull();
		assertThat(savedUser.getCreatedAt()).isNotNull();
		assertThat(savedUser.getIpAddress()).isEqualTo("127.0.0.1");
	}

	@Test
	void loginSucceedsWithUserIdAndPassword() throws Exception {
		userRepository.insert(new User("login01", "pw1234", "login-user", LocalDateTime.now(), "127.0.0.1"));

		String requestBody = """
			{
			  "userId": "login01",
			  "password": "pw1234"
			}
			""";

		mockMvc.perform(post("/api/users/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").value("login01"))
			.andExpect(jsonPath("$.name").value("login-user"));
	}

	@Test
	void userCrudLifecycleWorks() throws Exception {
		mockMvc.perform(post("/api/users/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "userId": "crud01",
					  "password": "pw1234",
					  "name": "초기이름"
					}
					""")
				.with(request -> {
					request.setRemoteAddr("127.0.0.1");
					return request;
				}))
			.andExpect(status().isCreated());

		mockMvc.perform(get("/api/users/crud01"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").value("crud01"))
			.andExpect(jsonPath("$.name").value("초기이름"));

		mockMvc.perform(put("/api/users/crud01")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "password": "newpw1234",
					  "name": "수정이름"
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("수정이름"));

		mockMvc.perform(get("/api/users"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].userId").value("crud01"));

		mockMvc.perform(delete("/api/users/crud01"))
			.andExpect(status().isNoContent());

		assertThat(userRepository.findByUserId("crud01")).isNull();
	}
}
