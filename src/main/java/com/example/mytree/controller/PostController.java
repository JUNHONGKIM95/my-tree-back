package com.example.mytree.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mytree.dto.CreatePostRequest;
import com.example.mytree.dto.DeletePostRequest;
import com.example.mytree.dto.PostResponse;
import com.example.mytree.service.PostService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/posts")
public class PostController {

	private final PostService postService;

	public PostController(PostService postService) {
		this.postService = postService;
	}

	@PostMapping
	public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
		PostResponse response = postService.createPost(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<PostResponse>> getPosts() {
		return ResponseEntity.ok(postService.getPosts());
	}

	@GetMapping("/{postNo}")
	public ResponseEntity<PostResponse> getPost(@PathVariable Long postNo) {
		return ResponseEntity.ok(postService.getPost(postNo));
	}

	@DeleteMapping("/{postNo}")
	public ResponseEntity<Void> deletePost(
		@PathVariable Long postNo,
		@RequestParam(required = false) String requesterUserId,
		@RequestHeader(name = "X-Requester-User-Id", required = false) String requesterUserIdHeader,
		@RequestBody(required = false) DeletePostRequest request
	) {
		String resolvedRequesterUserId = requesterUserId;
		if (resolvedRequesterUserId == null || resolvedRequesterUserId.isBlank()) {
			resolvedRequesterUserId = requesterUserIdHeader;
		}
		if ((resolvedRequesterUserId == null || resolvedRequesterUserId.isBlank()) && request != null) {
			resolvedRequesterUserId = request.requesterUserId();
		}

		postService.deletePost(postNo, resolvedRequesterUserId);
		return ResponseEntity.noContent().build();
	}
}
