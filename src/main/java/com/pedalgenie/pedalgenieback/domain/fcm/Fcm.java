package com.pedalgenie.pedalgenieback.domain.fcm;

import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fcm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String token;

    @Builder
    public Fcm(String token){
        this.token=token;
    }

    public void updateToken(String token) {
        this.token = token;
    }

    public void confirmMember(Member member){
        this.member=member;;
    }
}
