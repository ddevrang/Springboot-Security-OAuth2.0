package com.ddevrang.security1.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.ddevrang.security1.model.User;

import lombok.Data;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행한다.
// 로그인 진행이 완료되면 시큐리티 session을 만들게되는데 (Security ContextHolder)
// 여기에 저장할 수 있는 오브젝트는 Authentication 타입의 객체여야만 한다.
// 이 때 Authentication 타입 객체 안에는 User 정보가 있어야하고 
// 그 User 오브젝트의 타입은 UserDetails 타입 객체여야만 한다.

// Security Session => Authentication => UserDetails(PrincipalDetails)

@Data
public class PrincipalDetails implements UserDetails, OAuth2User{

	private User user;		// 컴포지션
	private Map<String, Object> attributes;

	// 일반 로그인에 사용하는 생성자
	public PrincipalDetails(User user) {
		this.user = user;
	}
	
	// OAuth 로그인에 사용하는 생성자
	public PrincipalDetails(User user, Map<String, Object> attributes) {
		this.user = user;
		this.attributes = attributes;
	}
	
	// 해당 유저의 권한을 리턴하는 곳
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>();		// ArrayList는 Collection의 자식이라 가능.
		
		collect.add(new GrantedAuthority() {
			
			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
		
		return collect;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	// 계정만료 여부 (true : 만료아님)
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	// 계정잠금 여부 (true : 잠금아님)
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	// 비밀번호만료 여부 (true : 만료아님)
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	// 계정활성화 여부 (true : 활성화)
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		return null;		// 사용하지 않을 것이고, 중요하지 않아서 그냥 null로 둠.
	}

}
