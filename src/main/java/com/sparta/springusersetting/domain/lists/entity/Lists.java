package com.sparta.springusersetting.domain.lists.entity;

import com.sparta.springusersetting.domain.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "lists")
public class Lists extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer pos;

    @Column(nullable = false)
    private String createdBy;

    private String modifiedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @OneToMany(mappedBy = "lists", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> cardList = new ArrayList<>();

    @Builder
    public Lists(String title, Integer pos, String createdBy, Board board) {
        this.title = title;
        this.pos = pos;
        this.createdBy = createdBy;
        this.board = board;
    }

    public void updateTitle(String title, String modifiedBy) {
        this.title = title;
        this.modifiedBy = modifiedBy;
    }

    public void updatePosition(Integer pos, String modifiedBy) {
        this.pos = pos;
        this.modifiedBy = modifiedBy;
    }

    public void updatePosition(Integer pos) {
        this.pos = pos;
    }
}
