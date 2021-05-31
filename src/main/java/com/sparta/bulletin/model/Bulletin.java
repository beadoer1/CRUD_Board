package com.sparta.bulletin.model;


import com.sparta.bulletin.dto.BulletinDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Bulletin extends Timestamped{

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String writer;
    @Column(nullable = false)
    private String writing;
    @OneToMany
    @JoinColumn(name = "BULLETIN_ID")
    private List<Comment> commentList;

    public Bulletin(BulletinDto requestDto){
        this.title = requestDto.getTitle();
        this.writer = requestDto.getWriter();
        this.writing = requestDto.getWriting();
    }

    public void update(BulletinDto requestDto){
        this.title = requestDto.getTitle();
        this.writer = requestDto.getWriter();
        this.writing = requestDto.getWriting();
    }

    public void addComment(Comment comment) {
        this.commentList.add(comment);
    }
}
