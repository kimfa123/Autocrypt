package com.autocrypt.domain.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //sequence, auto_increment

    @Column(nullable = false, length = 30, unique = true)
    private String username; //아이디

    @Column(nullable = false, length = 100)
    private String password; //패스워드

    @Column(nullable = false, length = 50)
    private String email; //이메일

    @Column(nullable = false, length = 20)
    private String nickname; //닉네임

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // 비밀번호 암호화 매소드
    public void setPassword(String password) {
        this.password = password;
    }

    // 권한 매소드
    public String getRoleKey() {
        return this.role.getKey();
    }
    //회원수정 매소드
    public void update(String password, String nickname) {
        this.password = password;
        this.nickname = nickname ;
    }
}
