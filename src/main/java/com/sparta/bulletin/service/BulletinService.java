package com.sparta.bulletin.service;


import com.sparta.bulletin.model.Bulletin;
import com.sparta.bulletin.dto.BulletinDto;
import com.sparta.bulletin.model.Comment;
import com.sparta.bulletin.repository.BulletinRepository;
import com.sparta.bulletin.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class BulletinService {

    private final BulletinRepository bulletinRepository;
    private final CommentRepository commentRepository;


    @Transactional // 안달리면 update 되지 않는다.
    public void update(Long id, BulletinDto requestDto){
        Bulletin bulletin = bulletinRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );
        bulletin.update(requestDto);
    }


    @Transactional
    public Bulletin addComment(Long bulletinId, Comment comment) {
        // 1) 게시글 조회
        Bulletin bulletin = bulletinRepository.findById(bulletinId).orElseThrow(
                () -> new NullPointerException("해당 게시글 아이디가 존재하지 않습니다.")
        );
        // 3) 게시글에 댓글을 추가합니다.
        bulletin.addComment(comment);
        return bulletin;
    }



}
