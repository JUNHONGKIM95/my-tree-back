package com.example.mytree.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.mytree.domain.Post;

@Mapper
public interface PostRepository {

	int insert(Post post);

	Post findByPostNo(@Param("postNo") Long postNo);

	List<Post> findAll();

	int deleteByPostNo(@Param("postNo") Long postNo);

	int deleteAll();
}
