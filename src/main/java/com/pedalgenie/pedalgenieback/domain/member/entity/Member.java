package com.pedalgenie.pedalgenieback.domain.member.entity;


import com.pedalgenie.pedalgenieback.global.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor (access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long memberId;

    @NotNull
    private String nickname;

    @NotNull
    private String email;

    @Enumerated(EnumType.STRING)
    @NotNull
    private MemberRole memberRole = MemberRole.CUSTOMER; // 디폴트는 일반 고객

    private String password; // 어드민, 사장님용

    private Long oauthId; // 일반 고객용

    @Builder
    public Member(Long memberId, String nickname, MemberRole memberRole, String email, String password, Long oauthId){
        this.memberId = memberId;
        this.nickname = nickname;
        this.memberRole = memberRole;
        this.email = email;
        this.password = password;
        this.oauthId = oauthId;
    }

}
