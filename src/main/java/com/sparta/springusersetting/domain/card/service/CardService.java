package com.sparta.springusersetting.domain.card.service;


import com.sparta.springusersetting.attachment.service.AttachmentService;
import com.sparta.springusersetting.domain.card.dto.*;
import com.sparta.springusersetting.domain.card.entity.Card;
import com.sparta.springusersetting.domain.card.exception.BadAccessCardException;
import com.sparta.springusersetting.domain.card.repository.CardRepository;
import com.sparta.springusersetting.domain.common.dto.AuthUser;
import com.sparta.springusersetting.domain.lists.entity.Lists;
import com.sparta.springusersetting.domain.lists.repository.ListsRepository;
import com.sparta.springusersetting.domain.notification.notificationutil.NotificationUtil;
import com.sparta.springusersetting.domain.notification.slack.SlackChatUtil;
import com.sparta.springusersetting.domain.participation.service.MemberManageService;
import com.sparta.springusersetting.domain.user.entity.User;
import com.sparta.springusersetting.domain.user.enums.MemberRole;
import com.sparta.springusersetting.domain.user.exception.BadAccessUserException;
import com.sparta.springusersetting.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserService userService;
    private final ListsRepository listsRepository;
    private final MemberManageService memberManageService;
<<<<<<< HEAD
    private final SlackChatUtil slackChatUtil;
    private final NotificationUtil notificationUtil;
=======
    private final AttachmentService attachmentService;

>>>>>>> dev

    @Transactional
    public String createCard(AuthUser authUser, CardRequestDto requestDto, MultipartFile file)
    {
        Lists lists = listsRepository.findById(requestDto.getListId()).orElse(null);
        User createUser = User.fromAuthUser(authUser);
        if(memberManageService.checkMemberRole(createUser.getId(),lists.getBoard().getWorkspace().getId()) == MemberRole.ROLE_READ_USER)
        {
            throw new BadAccessUserException();
        }

        User manager = userService.findUser(requestDto.getManagerId());
        Card card = new Card(manager, lists, requestDto.getTitle(), requestDto.getContents(), requestDto.getDeadline());
        cardRepository.save(card);
        if(file != null && !file.isEmpty()) {
            try {
                attachmentService.saveFile(authUser, card.getId(), file);
            } catch (IOException e)
            {
                throw new RuntimeException("파일 저장중 오류 발생");
            }
        }
        return "카드 생성이 완료되었습니다.";
    }

    @Transactional(readOnly = true)
    public CardResponseDto getCard(AuthUser authUser, Long cardId) {
        if(authUser == null)
            throw new BadAccessUserException();

        Card card = cardRepository.findById(cardId).orElse(null);
        CardResponseDto cardResponseDto = new CardResponseDto(
                card.getId(),
                card.getTitle(),
                card.getContents(),
                card.getDeadline(),
                card.getManager().getEmail(),
                card.getActivityLogs().stream().map(ActivityLogResponseDto::new).toList(),
                card.getCommentList());
        return cardResponseDto;
    }


    @Transactional
    public String updateCard(AuthUser authUser, CardRequestDto requestDto, Long cardId, User user) throws IOException {
        Lists lists = listsRepository.findById(requestDto.getListId()).orElse(null);
        User createUser = User.fromAuthUser(authUser);

        if(memberManageService.checkMemberRole(createUser.getId(),lists.getBoard().getWorkspace().getId()) == MemberRole.ROLE_READ_USER)
        {
            throw new BadAccessUserException();
        }

        User manager = userService.findUser(requestDto.getManagerId());
        Card card = cardRepository.findById(cardId).orElse(null);
        card.update(manager,lists,requestDto.getTitle(),requestDto.getContents(),requestDto.getDeadline());
        cardRepository.save(card);

        // 카드 변경 알림
        notificationUtil.UpdateCardNotification(user, card);

        return "카드 수정이 완료되었습니다.";
    }
    @Transactional
    public String deleteCard(AuthUser authUser, Long cardId) {
        User deletedUser = User.fromAuthUser(authUser);
        Card card = cardRepository.findById(cardId).orElse(null);
        if(memberManageService.checkMemberRole(deletedUser.getId(),card.getLists().getBoard().getWorkspace().getId()) == MemberRole.ROLE_READ_USER)
        {
            throw new BadAccessUserException();
        }

        cardRepository.delete(card);
        return "카드 삭제가 완료되었습니다.";
    }

    @Transactional(readOnly = true)
    public Slice<CardSearchResponseDto> searchCard(Long userId, CardSearchRequestDto searchRequest, Long cursorId, int pageSize) {
        User user = userService.findUser(userId);

        if (searchRequest.getWorkspaceId() == null) {
            throw new BadAccessCardException();
        }

        // 사용자가 해당 워크스페이스에 속해 있는지 확인
        MemberRole memberRole = memberManageService.checkMemberRole(userId, searchRequest.getWorkspaceId());
        if (memberRole == null) {
            throw new BadAccessCardException();
        }

        // QueryDSL을 사용한 검색 수행
        return cardRepository.searchCards(searchRequest, cursorId, pageSize);
    }

    public Card findCard(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(null);
        return card;

    }
}
