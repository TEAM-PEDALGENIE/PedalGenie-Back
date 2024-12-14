package com.pedalgenie.pedalgenieback.domain.member.service;

import com.pedalgenie.pedalgenieback.domain.member.dto.MemberLoginRequestDto;
import com.pedalgenie.pedalgenieback.domain.member.dto.MemberLoginResponseDto;
import com.pedalgenie.pedalgenieback.domain.member.dto.MemberResponseDto;
import com.pedalgenie.pedalgenieback.domain.member.dto.MemberRegisterRequestDto;
import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.member.entity.MemberRole;
import com.pedalgenie.pedalgenieback.domain.member.repository.MemberRepository;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import com.pedalgenie.pedalgenieback.global.jwt.CustomUserDetails;
import com.pedalgenie.pedalgenieback.global.jwt.CustomUserDetailsService;
import com.pedalgenie.pedalgenieback.global.jwt.TokenDto;
import com.pedalgenie.pedalgenieback.global.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    // 자체 회원가입 메서드
    @Transactional
    public MemberResponseDto register(MemberRegisterRequestDto requestDto) {
        String email = requestDto.getEmail();
        String nickname = requestDto.getNickname();
        String password = requestDto.getPassword();

        // 이메일 중복 확인
        if (memberRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode. ALREADY_REGISTERED_MEMBER_EMAIL);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .password(encodedPassword)
                .memberRole(MemberRole.SHOP) // 디폴트는 사장님 유저
                .build();
        // 회원 저장
        memberRepository.save(member);

        // response dto 반환
        return MemberResponseDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }

    // 자체 로그인 메서드
    public MemberLoginResponseDto login(MemberLoginRequestDto loginRequestDTO) {
        String email = loginRequestDTO.getEmail();
        String password = loginRequestDTO.getPassword();

        // 이메일로 사용자 정보 로드
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // 비밀번호 검증 (details와 입력된 비밀번호 비교)
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        if (!authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }

        Member member = ((CustomUserDetails) userDetails).getMember();

        // 사장님, 어드민인지 권한 확인
        if (!(member.getMemberRole().equals(MemberRole.SHOP) || member.getMemberRole().equals(MemberRole.ADMIN))) {
            throw new CustomException(ErrorCode.FORBIDDEN_ROLE);
        }

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
    // 회원 조회 메서드
    public MemberResponseDto getMemberInfo(Long memberId) {
        // 회원 조회 및 예외 처리
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_MEMBER_ID));

        return MemberResponseDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getMemberRole().name())
                .build();
    }

}
