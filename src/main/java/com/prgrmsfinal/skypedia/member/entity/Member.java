package com.prgrmsfinal.skypedia.member.entity;

import com.prgrmsfinal.skypedia.global.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(value = {AuditingEntityListener.class})
@FilterDef(name = "withdrawnFilter", parameters = @ParamDef(name = "withdrawn", type = Boolean.class))
@Filter(name = "withdrawnFilter", condition = "withdrawn = :withdrawn")
public class Member extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                //자동생성 고유식별자

    @Column(nullable = false)
    private String oauthId;         //구글, 네이버에서 제공하는 고유식별자

    @Column(nullable = false)
    private String name;            //구글, 네이버에서 제공하는 본명

    @Column(nullable = false)
    private String email;           //구글, 네이버에서 제공하는 이메일

    @Column(unique = true)
    private String username;        //최초회원가입시, 제공되는 랜덤 닉네임

    @Enumerated(EnumType.STRING)    // enum 값을 문자열로 저장
    @Column(nullable = false)
    private Role role;              //유저 권한

    @Column(nullable = false)
    private String profileImage;    //사용자 프로필사진

    @Column(nullable = false)
    private boolean withdrawn = Boolean.FALSE; // 탈퇴 여부

    private LocalDateTime withdrawnAt;//탈퇴 날짜


}
