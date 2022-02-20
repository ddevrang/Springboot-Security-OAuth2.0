package com.ddevrang.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddevrang.security1.model.User;

// 기본적으로 CRUD 함수를 JpaRepository가 들고 있음.
// @Repository라는 어노테이션이 없어도 IoC가 된다. (자동으로 Bean 등록)
// 그 이유는 JpaRepository를 상속했기 때문에.!
public interface UserRepository extends JpaRepository<User, Integer>{
	
	// findBy는 규칙 -> Username 문법 (JPA 네이밍쿼리 = JPA 쿼리메서드)
	// Select * from user where username = 1?
	public User findByUsername(String username);
	
}
