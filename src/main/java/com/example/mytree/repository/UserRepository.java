package com.example.mytree.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.mytree.domain.User;

@Mapper
public interface UserRepository {

	int insert(User user);

	User findByUserId(@Param("userId") String userId);

	User findByCredentials(@Param("userId") String userId, @Param("password") String password);

	List<User> findAll();

	int update(User user);

	int deleteByUserId(@Param("userId") String userId);
}
