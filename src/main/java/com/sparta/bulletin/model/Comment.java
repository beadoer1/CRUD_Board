package com.sparta.bulletin.model;


import com.sparta.bulletin.dto.CommentDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Comment extends Timestamped{

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(nullable = false)
    private String writer;

    @Column(nullable = false)
    private String writing;


    public Comment(CommentDto requestDto){
        this.writer = requestDto.getWriter();
        this.writing = requestDto.getWriting();
    }
    public void update(CommentDto requestDto){
        this.writer = requestDto.getWriter();
        this.writing = requestDto.getWriting();
    }
}
