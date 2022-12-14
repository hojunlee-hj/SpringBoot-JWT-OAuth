package com.example.jwtWithOauth.config.oauth;

import com.example.jwtWithOauth.config.CustomBCryptPasswordEncoder;
import com.example.jwtWithOauth.config.auth.PrincipalDetails;
import com.example.jwtWithOauth.config.oauth.provider.GoogleUserInfo;
import com.example.jwtWithOauth.config.oauth.provider.NaverUserInfo;
import com.example.jwtWithOauth.config.oauth.provider.OAuth2UserInfo;
import com.example.jwtWithOauth.domain.User;
import com.example.jwtWithOauth.domain.UserRole;
import com.example.jwtWithOauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final CustomBCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    // 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
    // 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest = " + userRequest.getClientRegistration()); // registrationId로 어떤 OAuth 로 로그인 했는지 알 수 있음
        System.out.println("userRequest.getAccessToken() = " + userRequest.getAccessToken().getTokenValue());
        System.out.println("userRequest.getAccessToken().getExpiresAt() = " + userRequest.getAccessToken().getExpiresAt());
        // 구글 로그인 버튼 클릭 -> 구글 로그인창 -> 로그인을 완료 -> code 를 리턴 (OAuth-Client 라이브러리) -> AccessToken 요청
        // userRequest 정보 -> 이 정보를 이용해 회원 Profile 정보를 받아야함 (loadUser 이용) -> 구글로 부터 회원 프로필 정보 받음.
        System.out.println("userRequest.getClientRegistration().getClientId() = " + userRequest.getClientRegistration().getClientId());
        System.out.println("userRequest.getClientRegistration().getClientName() = " + userRequest.getClientRegistration().getClientName());
        System.out.println("super.loadUser(userRequest).getAttributes() = " + super.loadUser(userRequest).getAttributes());


        OAuth2User oauth2User = super.loadUser(userRequest);

        System.out.println("oauth2User.getAttributes() = " + oauth2User.getAttributes());

        // String provider = userRequest.getClientRegistration().getRegistrationId(); // Google
        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map)oauth2User.getAttributes().get("response"));
        }else{
            System.out.println("우리는 구글, 네이버만 지원해요");
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String userName = provider + "_" + providerId;
        String password = bCryptPasswordEncoder.encode("DNAProjectOAuth");
        String email = oAuth2UserInfo.getEmail();

        User userEntity = userRepository.findByUsername(userName);

        if (userEntity == null) {
            System.out.println("최초 로그인 입니다.");
            userEntity = User.builder().username(userName).password(password)
                    .email(email)
                    .role(UserRole.USER_ROLE)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }else{
            System.out.println("이미 로그인을 한적이 있습니다.");
        }

        // User 정보와 Oauth Attributes 정보까지 Authentication 이 가지고 있다.
        return new PrincipalDetails(userEntity, oauth2User.getAttributes());
    }
}
