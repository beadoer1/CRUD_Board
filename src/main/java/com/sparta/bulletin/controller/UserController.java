package com.sparta.bulletin.controller;

import com.sparta.bulletin.dto.UserDto;
import com.sparta.bulletin.model.User;
import com.sparta.bulletin.repository.UserRepository;
import com.sparta.bulletin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    // 회원 로그인 페이지
    @GetMapping("/user/login")
    public String login(Model model) {
        if(isAuthenticated()){
            return "redirect:/";
        }
        // 위 url로 요청이 오면 login 페이지를 열어줌(thymeleaf에 의해 login String만 내려주면 자동으로 연결)
        return "login";
    }

    // 로그인 에러 시 페이지
    @GetMapping("/user/login/error")
    public String loginError(Model model) {
        // 똑같이 로그인 페이지를 연결하지만 에러도 함께 전송(thymeleaf에 의해 login String만 내려주면 자동으로 연결)
        model.addAttribute("loginError", true);
        return "login";
    }

    @GetMapping("/api/user/{id}") // 아래와 같이 예외처리 해주지않으면 Error 발생
    public String getUserNameById(@PathVariable Long id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다."));
        return user.getUsername();
    }

    // 회원 가입 페이지
    @GetMapping("/user/signup")
    public String signup() {
        if(isAuthenticated()){
            return "redirect:/";
        }
        // 회원가입 url 내려왔을 때 signup 페이지 연결
        return "signup";
    }

    // 회원 가입 요청 처리
    @PostMapping("/user/signup")
    public String registerUser(UserDto requestDto, Model model) {
        try {
            userService.registerUser(requestDto);
        } catch(IllegalArgumentException e){
            model.addAttribute("signupMessage", e.getMessage());
            return "signup";
        }
        return "login";
    }

    @GetMapping("/user/forbidden")
    public String forbidden() {
        return "forbidden";
    }

    // kakao 로그인 구현
    @GetMapping("/user/kakao/callback")
    public String kakaoLogin(String code) {
        // authorizedCode: 카카오 서버로부터 받은 인가 코드
        userService.kakaoLogin(code);
        return "redirect:/";
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }
}
