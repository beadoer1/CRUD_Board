package com.sparta.bulletin.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class User extends Timestamped{

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private Long kakaoId;

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.kakaoId = null;
    }

    public User(String username, String password, Long kakaoId){
        this.username = username;
        this.password = password;
        this.kakaoId = kakaoId;
    }

}
