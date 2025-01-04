package com.pedalgenie.pedalgenieback.global.jwt;

import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {
    // 현재 로그인한 멤버의 memberId 가져오기
    public static Long getCurrentMemberId() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getMemberId();
    }

    // 현재 로그인한 멤버의 role 가져오기
    public static String getCurrentRole() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getRole();
    }
}
