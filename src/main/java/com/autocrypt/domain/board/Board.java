package com.autocrypt.domain.board;


import com.autocrypt.domain.BaseTimeEntity;
import com.autocrypt.domain.reply.Reply;
import com.autocrypt.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    private String content;

    private int count; // 조회수

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }
    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER)
    private List<Reply> replyList;

}
