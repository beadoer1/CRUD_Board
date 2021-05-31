package com.sparta.bulletin.controller;

import com.sparta.bulletin.model.Bulletin;
import com.sparta.bulletin.dto.BulletinDto;
import com.sparta.bulletin.model.Comment;
import com.sparta.bulletin.repository.BulletinRepository;
import com.sparta.bulletin.repository.CommentRepository;
import com.sparta.bulletin.security.UserDetailsImpl;
import com.sparta.bulletin.service.BulletinService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BulletinController {

    private final BulletinRepository bulletinRepository;
    private final BulletinService bulletinService;
    private final CommentRepository commentRepository;

    @GetMapping("/api/bulletins")
    public List<Bulletin> getBulletin(){
        return bulletinRepository.findAllByOrderByModifiedAtDesc();
    }

    @GetMapping("/api/bulletins/{id}") // 아래와 같이 예외처리 해주지않으면 Error 발생
    public Bulletin getBulletinById(@PathVariable Long id){
        return bulletinRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );
    }

    @PostMapping("/api/bulletins")
    public Bulletin createBulletin(@RequestBody BulletinDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        System.out.println(userDetails);
        String username = userDetails.getUsername();
        requestDto.setWriter(username);

        Bulletin bulletin = new Bulletin(requestDto);
        bulletinRepository.save(bulletin);

        return bulletin;
    }

    @PutMapping("/api/bulletins/{id}")
    public Long updateBulletins(@PathVariable Long id,@RequestBody BulletinDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        String username = userDetails.getUsername();
        requestDto.setWriter(username);

        bulletinService.update(id,requestDto);
        return id;
    }

    @DeleteMapping("/api/bulletins/{id}")
    public Long deleteBulletin(@PathVariable Long id){
        Bulletin bulletin = bulletinRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("게시물 아이디가 존재하지 않습니다.")
        );
        List<Comment> commentList = bulletin.getCommentList();
        commentRepository.deleteAll(commentList);
        bulletinRepository.deleteById(id);
        return id;
    }


}
