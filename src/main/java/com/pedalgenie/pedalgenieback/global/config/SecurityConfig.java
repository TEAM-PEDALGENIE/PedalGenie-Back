package com.pedalgenie.pedalgenieback.global.config;

import com.pedalgenie.pedalgenieback.domain.member.repository.MemberRepository;
import com.pedalgenie.pedalgenieback.global.jwt.JwtFilter;
import com.pedalgenie.pedalgenieback.global.jwt.TokenProvider;
import com.pedalgenie.pedalgenieback.global.jwt.refresh.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTemplate<String, String> redisTemplate;


    // 허용된 URI 목록
    private static final List<String> ALLOWED_URIS = Arrays.asList(
            "/swagger-ui/index.html",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/auth/**",
            "http://localhost:3000/**"
    );


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .formLogin(auth -> auth.disable()) // 기본 로그인 폼 비활성화
                .httpBasic(HttpBasicConfigurer::disable) // 기본 HTTP 기본 인증 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(logout -> logout.disable()) // 기본 로그아웃 비활성화
                // 경로 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ALLOWED_URIS.toArray(new String[0])).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAfter(new JwtFilter(tokenProvider, memberRepository, refreshTokenRepository, redisTemplate), OAuth2LoginAuthenticationFilter.class);

        return http.build();
    }

    // 필터 체인에서 제외 (토큰 불필요)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> {
            web.ignoring()
                    .requestMatchers(
                            "/swagger-ui/**",
                            "/swagger-resources/**",
                            "/v3/api-docs/**",
                            "/webjars/**",
                            "/auth/**",
                            "/shops/**",
                            "/products/**",
                            "/admin/images/**"
                    );
        };
    }



    protected CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", getDefaultCorsConfiguration());
        return source;
    }

    private CorsConfiguration getDefaultCorsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        return config;
    }
}
