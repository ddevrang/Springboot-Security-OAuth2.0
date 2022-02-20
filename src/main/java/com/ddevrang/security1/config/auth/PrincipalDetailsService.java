package com.ddevrang.security1.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ddevrang.security1.model.User;
import com.ddevrang.security1.repository.UserRepository;

// 시큐리티 설정에 .loginProcessingUrl("/login")을 해두었기 때문에 /login 요청이 오면
// 자동으로 UserDetailsService 타입으로 IoC되어있는 loadUserByUsername 함수가 실행된다. (스프링 규칙)
@Service
public class PrincipalDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	// 시큐리티 session(내부 Authentiction(내부 UserDetails))
	// 함수 종료 시 @AuthentictionPrincipal 어노테이션이 만들어진다.
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		 User userEntity = userRepository.findByUsername(username);
		 
		 if(userEntity != null) {
			 return new PrincipalDetails(userEntity);
		 }
		
		return null;
	}

}
