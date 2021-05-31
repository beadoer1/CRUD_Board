package com.sparta.bulletin.service;

import com.sparta.bulletin.dto.CommentDto;
import com.sparta.bulletin.model.Comment;
import com.sparta.bulletin.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional
    public void update(Long id, CommentDto requestDto){
        // id(고유값)으로 DB에서 바꾸고자 하는 객체를 불러온다.
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("댓글 아이디가 존재하지 않습니다.")
        );
        // 불러온 객체에 update 메서드를 이용해 값을 씌워준다.
        comment.update(requestDto);
    }
}
