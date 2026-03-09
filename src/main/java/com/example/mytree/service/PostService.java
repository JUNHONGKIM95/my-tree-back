package com.example.mytree.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mytree.domain.Post;
import com.example.mytree.dto.CreatePostRequest;
import com.example.mytree.dto.PostResponse;
import com.example.mytree.exception.PostDeleteForbiddenException;
import com.example.mytree.exception.PostNotFoundException;
import com.example.mytree.exception.UserNotFoundException;
import com.example.mytree.repository.PostRepository;
import com.example.mytree.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class PostService {

	private static final String ADMIN_USER_ID = "admin";

	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final Clock clock;

	public PostService(PostRepository postRepository, UserRepository userRepository, Clock clock) {
		this.postRepository = postRepository;
		this.userRepository = userRepository;
		this.clock = clock;
	}

	@Transactional
	public PostResponse createPost(CreatePostRequest request) {
		if (userRepository.findByUserId(request.userId()) == null) {
			throw new UserNotFoundException(request.userId());
		}

		Post post = new Post(
			null,
			request.userId(),
			request.title(),
			request.content(),
			LocalDateTime.now(clock)
		);
		postRepository.insert(post);
		return PostResponse.from(post);
	}

	public List<PostResponse> getPosts() {
		return postRepository.findAll().stream().map(PostResponse::from).toList();
	}

	public PostResponse getPost(Long postNo) {
		Post post = postRepository.findByPostNo(postNo);
		if (post == null) {
			throw new PostNotFoundException(postNo);
		}
		return PostResponse.from(post);
	}

	@Transactional
	public void deletePost(Long postNo, String requesterUserId) {
		if (requesterUserId == null || requesterUserId.isBlank()) {
			throw new UserNotFoundException("unknown");
		}

		if (userRepository.findByUserId(requesterUserId) == null) {
			throw new UserNotFoundException(requesterUserId);
		}

		Post post = postRepository.findByPostNo(postNo);
		if (post == null) {
			throw new PostNotFoundException(postNo);
		}

		boolean canDelete = ADMIN_USER_ID.equals(requesterUserId) || requesterUserId.equals(post.getUserId());
		if (!canDelete) {
			throw new PostDeleteForbiddenException(postNo, requesterUserId);
		}

		postRepository.deleteByPostNo(postNo);
	}
}
