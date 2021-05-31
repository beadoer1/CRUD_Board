package com.sparta.bulletin.controller;

import com.sparta.bulletin.dto.BulletinDto;
import com.sparta.bulletin.dto.CommentDto;
import com.sparta.bulletin.model.Bulletin;
import com.sparta.bulletin.model.Comment;
import com.sparta.bulletin.repository.BulletinRepository;
import com.sparta.bulletin.repository.CommentRepository;
import com.sparta.bulletin.security.UserDetailsImpl;
import com.sparta.bulletin.service.BulletinService;
import com.sparta.bulletin.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final CommentService commentService;
    private final BulletinService bulletinService;
    private final BulletinRepository bulletinRepository;

    @PostMapping("/api/bulletins/{id}/comments")
    public List<Comment> createComment(@PathVariable Long id, @RequestBody CommentDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        String username = userDetails.getUsername();
        requestDto.setWriter(username);

        Comment comment = new Comment(requestDto);
        commentRepository.save(comment);

        bulletinService.addComment(id,comment);

        Bulletin bulletin = bulletinRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("게시물 아이디가 존재하지 않습니다.")
        );
        return bulletin.getCommentList();
    }

    @GetMapping("/api/comments")
    public List<Comment> getComment(){
        return commentRepository.findAllByOrderByModifiedAtDesc();
    }

    @GetMapping("/api/checkWriter/{id}")
    public boolean checkWriter(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("없는 댓글 입니다.")
        );
        String writer = comment.getWriter();
        String username = userDetails.getUsername();
        boolean result = false;
        if(writer.equals(username)){
            result = true;
        }
        return result;
    }

    @DeleteMapping("/api/comments/{id}")
    public Long  deleteComment(@PathVariable Long id){
        commentRepository.deleteById(id);
        return id;
    }

    @PutMapping ("/api/comments/{id}")
    public Long updateComment(@PathVariable Long id, @RequestBody CommentDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        String username = userDetails.getUsername();
        requestDto.setWriter(username);

        commentService.update(id,requestDto);
        return id;
    }

}
