package com.ddevrang.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ddevrang.security1.config.auth.PrincipalDetails;
import com.ddevrang.security1.model.User;
import com.ddevrang.security1.repository.UserRepository;

@Controller // View를 리턴하겠다는 의미.
public class IndexController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/test/login")
	public @ResponseBody String testLogin(Authentication authentication,
			@AuthenticationPrincipal PrincipalDetails userDetails) { 		// DI (의존성 주입)
		System.out.println("/test/login =================");

		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

		System.out.println("authentication : " + principalDetails.getUser());

		System.out.println("userDetails : " + userDetails.getUser());

		return "세션 정보 확인하기";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOAuthLogin(Authentication authentication,
			@AuthenticationPrincipal OAuth2User oauth) { 	// DI (의존성 주입)
		System.out.println("/test/login =================");

		OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
		System.out.println("authentication : " + oauth2User.getAttributes());
		System.out.println("OAuth2User : " + oauth.getAttributes());

		return "OAuth 세션 정보 확인하기";
	}

	// localhost:8080
	// localhost:8080/
	@GetMapping({ "", "/" })
	public String index() {
		// 머스테치 기본 폴더 : src/main/resources/
		// ViewResolver 설정 : templates(prefix) .mustache(suffix) 따로 설정하지않아도 자동으로 설정된다.
		return "index"; // src/main/resources/templates/index.mustache
	}

	// OAuth 로그인을 해도 PrincipalDetails로 받을 수 있고,
	// 일반 로그인을 해도 PrincipalDetails로 받을 수 있다.
	// 상단에 만든 test들 처럼 분기처리할 필요가 없어서 편리하다.
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("PrincipalDetails : "+principalDetails.getUser());
		return "user";
	}

	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}

	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}

	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}

	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}

	@PostMapping("/join")
	public String join(User user) {

		user.setRole("ROLE_USER");

		// 비밀번호를 암호화해서 저장하지않으면, 시큐리티로 로그인을 할 수가 없음.
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);

		userRepository.save(user);

		return "redirect:/loginForm";
	}

	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}

}
