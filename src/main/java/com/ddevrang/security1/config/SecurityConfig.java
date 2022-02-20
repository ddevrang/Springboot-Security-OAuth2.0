package com.ddevrang.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails.UserInfoEndpoint;

import com.ddevrang.security1.config.oauth.PrincipalOauth2UserService;

@Configuration		// loC. 메모리에 띄워주기 위함.
@EnableWebSecurity		// 활성화시키기 위함. 스프링 시큐리티 필터가 스프링 필터체인에 등록된다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)		// secured 어노테이션 활성화, preAuthorize 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	@Bean		// 해당 메서드의 리턴되는 오브젝트를 IoC로 리턴해준다.
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests()
			.antMatchers("/user/**").authenticated()		// 해당주소로 접근하려면 인증이 필요하다.
			.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")		// 인증뿐 아니라 권한도 필요하다.
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")		// 인증뿐 아니라 권한도 필요하다.
			.anyRequest().permitAll()			// 나머지 주소들에 대해서는 접근을 허용한다.
			.and()
			.formLogin()							// 접근이 거부된 경우 로그인페이지로 이동
			.loginPage("/loginForm")		//  "/loginForm"으로 이동하게된다.
			.loginProcessingUrl("/login")		// "/login" 주소가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행해줌.
			.defaultSuccessUrl("/")
			.and()
			.oauth2Login()
			.loginPage("/loginForm")
			.userInfoEndpoint()
			.userService(principalOauth2UserService);
	}
}
