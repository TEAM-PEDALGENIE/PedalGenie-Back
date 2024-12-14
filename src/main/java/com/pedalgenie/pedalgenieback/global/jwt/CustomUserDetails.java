package com.pedalgenie.pedalgenieback.global.jwt;

import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.member.entity.MemberRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;


@Getter
public class CustomUserDetails implements UserDetails {
    private Member member;
    private Map<String, Object> attribute;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    private GrantedAuthority getAuthority(MemberRole role) {
        return new SimpleGrantedAuthority("ROLE_" + role.name());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();

        // 역할에 따라 권한 부여
        switch (member.getMemberRole()) {
            case ADMIN:
                authorityList.add(getAuthority(MemberRole.ADMIN));
                break;
            case SHOP:
                authorityList.add(getAuthority(MemberRole.SHOP));
                break;
            case CUSTOMER:
                authorityList.add(getAuthority(MemberRole.CUSTOMER));
                break;
        }
        return authorityList;
    }

    public Long getMemberId() {
        return member.getMemberId();
    }
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getNickname();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
