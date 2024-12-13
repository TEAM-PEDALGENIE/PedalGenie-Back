package com.pedalgenie.pedalgenieback.global.oauth.service;

import com.pedalgenie.pedalgenieback.domain.member.dto.MemberLoginResponseDto;
import com.pedalgenie.pedalgenieback.domain.member.dto.MemberResponseDto;
import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.member.entity.MemberRole;
import com.pedalgenie.pedalgenieback.domain.member.repository.MemberRepository;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import com.pedalgenie.pedalgenieback.global.jwt.TokenDto;
import com.pedalgenie.pedalgenieback.global.jwt.TokenProvider;
import com.pedalgenie.pedalgenieback.global.oauth.dto.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    @Value("${spring.security.oauth2.client.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.provider.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.provider.user-info-uri}")
    private String kakaoUserInfoUri;

    // 카카오 로그인 메서드
    @Transactional
    public MemberLoginResponseDto loginKakao(String authorizationCode) {
        // 인가 코드로 액세스 토큰 받기
        String kakaoAccessToken = getKakaoAccessToken(authorizationCode);

        // 사용자 정보 가져오기
        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken);

        // 이메일로 기존 회원 조회 또는 신규 회원 등록
        Member member = memberRepository.findByEmail(kakaoUserInfo.getEmail())
                .orElseGet(() -> registerKakao(kakaoUserInfo));

        //  token 발급
        String accessToken = tokenProvider.createAccessToken(member.getMemberId(), member.getMemberRole().name());
        String refreshToken = tokenProvider.createRefreshToken(member.getMemberId());

        // 응답 정보 dto
        MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                // 추후 제거 필요 -----------
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                // -----------------------
                .build();

        // 토큰 dto
        TokenDto tokenDto = TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        // 로그인 dto 반환
        return MemberLoginResponseDto.builder()
                .memberResponseDto(memberResponseDto)
                .tokenDto(tokenDto)
                .build();
    }


    // 인가코드로 access token 받아오는 메서드
    private String getKakaoAccessToken(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);

        body.add("code", authorizationCode);

        // POST 요청 보내기
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(tokenUri, HttpMethod.POST, entity, Map.class);

            Map<String, String> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("access_token")) {
                return responseBody.get("access_token");
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.KAKAO_ACCESS_TOKEN_ERROR);
        }
        return null;
    }

    // access token으로 카카오 사용자 정보 가져오는 메서드
    private KakaoUserInfo getKakaoUserInfo(String kakaoAccessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + kakaoAccessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // 요청 보내기
            ResponseEntity<Map> response = restTemplate.exchange(kakaoUserInfoUri, HttpMethod.GET, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();

            // 사용자 정보에서 이메일과 닉네임을 추출
            if (responseBody != null) {
                Map<String, Object> properties = (Map<String, Object>) responseBody.get("properties");
                Map<String, String> kakaoAccount = (Map<String, String>) responseBody.get("kakao_account");

                String email = kakaoAccount.get("email");
                String nickname =  (String) properties.get("nickname");

                return KakaoUserInfo.builder()
                        .email(email)
                        .nickname(nickname)
                        .build();
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.KAKAO_USER_INFO_ERROR);
        }
        return null; // 실패시 null 반환
    }

    // 회원가입 메서드 (처음 로그인하는 유저처리)
    private Member registerKakao(KakaoUserInfo kakaoUserInfo) {
        Member member = Member.builder()
                .email(kakaoUserInfo.getEmail())
                .nickname(kakaoUserInfo.getNickname())
                .memberRole(MemberRole.CUSTOMER)
                .build();

        return memberRepository.save(member);
    }
}
