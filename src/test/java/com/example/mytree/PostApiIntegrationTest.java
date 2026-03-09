package com.example.mytree;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.mytree.domain.User;
import com.example.mytree.repository.PostRepository;
import com.example.mytree.repository.UserRepository;

@SpringBootTest
@WebAppConfiguration
class PostApiIntegrationTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		postRepository.deleteAll();
		userRepository.findAll().forEach(user -> userRepository.deleteByUserId(user.getUserId()));
		userRepository.insert(new User("admin", "963963", "관리자", LocalDateTime.now(), "127.0.0.1"));
		userRepository.insert(new User("writer01", "pw1234", "writer-name", LocalDateTime.now(), "127.0.0.1"));
		userRepository.insert(new User("writer02", "pw1234", "other-writer", LocalDateTime.now(), "127.0.0.1"));
	}

	@Test
	void createAndReadPosts() throws Exception {
		Long postNo = createPost("writer01", "첫 번째 메모", "게시글 내용입니다.");

		mockMvc.perform(get("/api/posts"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].postNo").value(postNo))
			.andExpect(jsonPath("$[0].title").value("첫 번째 메모"));

		mockMvc.perform(get("/api/posts/{postNo}", postNo))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.postNo").value(postNo))
			.andExpect(jsonPath("$.content").value("게시글 내용입니다."));
	}

	@Test
	void ownerCanDeleteOwnPost() throws Exception {
		Long postNo = createPost("writer01", "삭제 테스트", "작성자 삭제");

		mockMvc.perform(delete("/api/posts/{postNo}", postNo).queryParam("requesterUserId", "writer01"))
			.andExpect(status().isNoContent());

		assertThat(postRepository.findByPostNo(postNo)).isNull();
	}

	@Test
	void adminCanDeleteAnyPost() throws Exception {
		Long postNo = createPost("writer01", "관리자 삭제", "관리자 권한");

		mockMvc.perform(delete("/api/posts/{postNo}", postNo).queryParam("requesterUserId", "admin"))
			.andExpect(status().isNoContent());

		assertThat(postRepository.findByPostNo(postNo)).isNull();
	}

	@Test
	void otherUserCannotDeleteAnotherUsersPost() throws Exception {
		Long postNo = createPost("writer01", "권한 없음", "삭제 불가");

		mockMvc.perform(delete("/api/posts/{postNo}", postNo).queryParam("requesterUserId", "writer02"))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.message").value("User writer02 cannot delete post: " + postNo));
	}

	@Test
	void createPostFailsWhenUserDoesNotExist() throws Exception {
		mockMvc.perform(post("/api/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "userId": "missing-user",
					  "title": "제목",
					  "content": "내용"
					}
					"""))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("User not found: missing-user"));
	}

	private Long createPost(String userId, String title, String content) throws Exception {
		String requestBody = """
			{
			  "userId": "%s",
			  "title": "%s",
			  "content": "%s"
			}
			""".formatted(userId, title, content);

		mockMvc.perform(post("/api/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.userId").value(userId))
			.andExpect(jsonPath("$.title").value(title))
			.andExpect(jsonPath("$.content").value(content));

		return postRepository.findAll().getFirst().getPostNo();
	}
}
