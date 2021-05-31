package com.sparta.bulletin.service;


import com.sparta.bulletin.dto.UserDto;
import com.sparta.bulletin.model.User;
import com.sparta.bulletin.repository.UserRepository;
import com.sparta.bulletin.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sparta.bulletin.security.kakao.KakaoOAuth2;
import com.sparta.bulletin.security.kakao.KakaoUserInfo;
import org.springframework.web.bind.annotation.GetMapping;



import java.util.Optional;

@Service // UserService에는 @RequiredArgsConstructor 가 없는 이유가 따로 있나??
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final KakaoOAuth2 kakaoOAuth2;
    private final AuthenticationManager authenticationManager;
    private static final String ADMIN_TOKEN = "AAABnv/xRVklrnYxKZ0aHgTBcXukeZygoC";

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, KakaoOAuth2 kakaoOAuth2, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kakaoOAuth2 = kakaoOAuth2;
        this.authenticationManager = authenticationManager;
    }


    public void kakaoLogin(String authorizedCode) {
        // 카카오 OAuth2 를 통해 카카오 사용자 정보 조회
        KakaoUserInfo userInfo = kakaoOAuth2.getUserInfo(authorizedCode);
        Long kakaoId = userInfo.getId();
        String nickname = userInfo.getNickname();

        // 우리 DB 에서 회원 Id 와 패스워드
        // 회원 Id = 카카오 nickname
        String username = nickname;
        // 패스워드 = 카카오 Id + ADMIN TOKEN
        String password = kakaoId + ADMIN_TOKEN;

        // DB 에 중복된 Kakao Id 가 있는지 확인
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);

        // 카카오 정보로 회원가입
        if (kakaoUser == null) {
            // 패스워드 인코딩
            String encodedPassword = passwordEncoder.encode(password);

            kakaoUser = new User(nickname, encodedPassword, kakaoId);
            userRepository.save(kakaoUser);
        }

        // 로그인 처리
        Authentication kakaoUsernamePassword = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(kakaoUsernamePassword);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 회원가입 시 닉네임, 패스워드 필터링
    public User registerUser(UserDto requestDto) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();
        String passwordCheck = requestDto.getPasswordCheck();
        // 닉네 최소 3자 이상, 알파벳 대소문자(a~z, A~Z), 숫자(0~9)임
        if(username.length() < 3 || !checkId(username)){
            throw new IllegalArgumentException("양식 불일치");
        }
        // 비밀번호는 최소 4자 이상이며, 닉네임과 같은 값이 포함된 경우 회원가입 실패
        if(password.length() < 4){
            throw new IllegalArgumentException("pw 짧음");
        } else if(password.contains(username)){
            throw new IllegalArgumentException("닉네임 포함");
        }
        // 비밀번호 중복체크
        if(!password.equals(passwordCheck)){
            throw new IllegalArgumentException("비밀번호 불일치");
        }
        // 회원 ID 중복 확인
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new IllegalArgumentException("중복 ID");
        }
        // 패스워드 인코딩
        password = passwordEncoder.encode(password);

        User user = new User(username, password);
        userRepository.save(user);

        return user;
    }

    private boolean checkId(String userId){
        //알파벳 대소문자(a~z, A~Z), 숫자(0~9)임
        for(int i = 0; i < userId.length();i++){
            char chr = userId.charAt(i);
            if(('a'<=chr && chr<='z')||('A'<=chr && chr<='Z')||('0'<=chr && chr<='9')){
                continue;
            }else{
                return false;
            }
        }
        return true;
    }

}