package com.sparta.springusersetting.domain.card.dto;

import com.sparta.springusersetting.domain.card.entity.Card;
import com.sparta.springusersetting.domain.comment.dto.response.CommentResponseDto;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class CardWithViewCountResponseDto {
    private Long id;
    private String title;
    private String contents;
    private LocalDate deadline;
    private String userEmail;
    private Long cardViewCount;
    private List<ActivityLogResponseDto> activityLogs;
    private List<CommentResponseDto> commentList;

    public CardWithViewCountResponseDto(Card card, Long cardViewCount) {
        this.id = card.getId();
        this.title = card.getTitle();
        this.contents = card.getContents();
        this.deadline = card.getDeadline();
        this.userEmail = card.getManager().getEmail();
        this.cardViewCount = cardViewCount;
        this.activityLogs = card.getActivityLogs().stream().map(ActivityLogResponseDto::new).toList();
        this.commentList = card.getCommentList().stream()
                .map(CommentResponseDto::new)
                .toList();;
    }
}
