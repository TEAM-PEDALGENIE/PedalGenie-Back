package com.pedalgenie.pedalgenieback.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.member.repository.MemberRepository;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import com.pedalgenie.pedalgenieback.global.jwt.refresh.RefreshToken;
import com.pedalgenie.pedalgenieback.global.jwt.refresh.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


@Builder
public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtFilter(TokenProvider tokenProvider, MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, RedisTemplate<String, String> redisTemplate) {
        this.tokenProvider = tokenProvider;
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getTokenFromRequest(request);

        if (accessToken == null || accessToken.trim().isEmpty()) {
            sendErrorResponse(response, ErrorCode.NOT_EXISTS_AUTHORIZATION);
            return;
        }
        try {
            // 엑세스 토큰 유효성 검사
            tokenProvider.validateToken(accessToken);
        } catch (CustomException e) {
            // 엑세스 토큰 만료시 리프레시 토큰으로 엑세스토큰 재발급
            if (e.getErrorCode() == ErrorCode.TOKEN_EXPIRED) {
                String refreshToken = getRefreshTokenFromRequest(request);
                
                // 리프레시 토큰이 없는 경우 예외 처리
                if (refreshToken == null || refreshToken.trim().isEmpty()) {
                    sendErrorResponse(response, ErrorCode.REFRESH_TOKEN_NOT_FOUND);
                    return;
                }
        
                // redis에 저장된 리프레시 토큰과 비교
                Long memberId = tokenProvider.getMemberIdFromToken(refreshToken);
                String redisKey = "refreshToken:" + memberId;
                String savedRefreshToken = redisTemplate.opsForValue().get(redisKey);

                if (savedRefreshToken == null || !refreshToken.equals(savedRefreshToken)) {
                    sendErrorResponse(response, ErrorCode.REFRESH_TOKEN_EXPIRED);
                    return;
                }

                // 저장된 리프레시 토큰이 유효하면 새 엑세스 토큰 생성
                Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_MEMBER_ID));

                String role = member.getMemberRole().name();

                String newAccessToken = tokenProvider.createAccessToken(memberId, role);
                response.setHeader("Authorization", "Bearer " + newAccessToken);  // 새 엑세스 토큰을 헤더에 담아서 응답
                accessToken = newAccessToken;
            }
            // 다른 예외는 그대로 처리
            else {
                sendErrorResponse(response, e.getErrorCode());
                return;
            }
        }

        // 엑세스 토큰이 유효하면 인증 처리 진행
        Long memberId = tokenProvider.getMemberIdFromToken(accessToken);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_MEMBER_ID));

        CustomUserDetails customUserDetails = new CustomUserDetails(member);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }


    // 토큰 에러 응답 생성 메서드
    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(errorCode.getHttpStatus().value());

        ResponseTemplate<Object> responseTemplate = ResponseTemplate.<Object>builder()
                .status(errorCode.getHttpStatus().value())
                .success(false)
                .message(errorCode.getMessage())
                .data(null)
                .build();

        String jsonResponse = objectMapper.writeValueAsString(responseTemplate);
        response.getWriter().write(jsonResponse);
    }



    // 요청 헤더에서 엑세스 을 추출하는 메서드
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 부분을 제거한 후 토큰 반환
        }
        return null;
    }

    // 쿠키에서 리프레시 토큰 추출하는 메서드
    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
