package com.ddevrang.security1.config.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.ddevrang.security1.config.auth.PrincipalDetails;
import com.ddevrang.security1.config.oauth.provider.FacebookUserInfo;
import com.ddevrang.security1.config.oauth.provider.GoogleUserInfo;
import com.ddevrang.security1.config.oauth.provider.NaverUserInfo;
import com.ddevrang.security1.config.oauth.provider.OAuth2UserInfo;
import com.ddevrang.security1.model.User;
import com.ddevrang.security1.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	// 구글 로그인 시 후처리되는 함수
	// 구글로부터 받은 userRequest 데이터에 대한 후처리.
	// 함수 종료 시 @AuthentictionPrincipal 어노테이션이 만들어진다.
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("getClientRegistration : "+userRequest.getClientRegistration());
		System.out.println("getAccessToken : "+userRequest.getAccessToken().getTokenValue());
		
		// 구글로그인 버튼 클릭 -> 구글 로그인창 -> 로그인 완료 -> code를 리턴(OAuth2-Client 라이브러리) -> AccessToken 요청
		// userRequest 정보 -> loadUser 함수 호출 -> 구글로 부터 회원 프로필을 받는다.
		OAuth2User oauth2User = super.loadUser(userRequest);
		System.out.println("getAttributes : "+oauth2User.getAttributes());
		
		// 받은 데이터를 기반으로 자동 회원가입을 진행
		OAuth2UserInfo oAuth2UserInfo = null;
		if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
			System.out.println("구글 로그인 요청");
			oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
		}else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
			System.out.println("페이스북 로그인 요청");
			oAuth2UserInfo = new FacebookUserInfo(oauth2User.getAttributes());
		}else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
			System.out.println("네이버 로그인 요청");
			oAuth2UserInfo = new NaverUserInfo((Map)oauth2User.getAttributes().get("response"));
		}else {
			System.out.println("우리는 구글, 페이스북, 네이버만 지원합니다.");
		}
		
		String provider = oAuth2UserInfo.getProvider();
		String providerId = oAuth2UserInfo.getProviderId();
		String username = provider + "_" + providerId;
		String password = bCryptPasswordEncoder.encode("떼브랑");
		String email = oAuth2UserInfo.getEmail();
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		
		if (userEntity == null) {
			System.out.println("로그인이 최초입니다. 자동회원가입을 진행합니다.");
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(userEntity);
		}else {
			System.out.println("로그인을 이미 한적이 있습니다. 당신은 자동회원가입이 되어있습니다.");
		}
		
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}
}
