package com.sparta.springusersetting.domain.card.service;


import com.sparta.springusersetting.domain.card.dto.CardRequestDto;
import com.sparta.springusersetting.domain.card.dto.CardResponseDto;
import com.sparta.springusersetting.domain.card.entity.Card;
import com.sparta.springusersetting.domain.card.repository.CardRepository;
import com.sparta.springusersetting.domain.common.dto.AuthUser;
import com.sparta.springusersetting.domain.lists.entity.Lists;
import com.sparta.springusersetting.domain.lists.repository.ListsRepository;
import com.sparta.springusersetting.domain.user.entity.User;
import com.sparta.springusersetting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final ListsRepository listsRepository;

    public void createCard(AuthUser authUser, CardRequestDto requestDto)
    {

        //User createUser = User.fromAuthUser(authUser);
        User manager = userRepository.findById(requestDto.getManagerId()).orElse(null);
        Lists lists = listsRepository.findById(requestDto.getListId()).orElse(null);
        Card card = new Card(requestDto.getTitle(),requestDto.getContents(),requestDto.getDeadline(),manager,lists);
        cardRepository.save(card);
    }

    public CardResponseDto getCard(AuthUser authUser, Long cardId) {
        Card card = cardRepository.findById(cardId).orElse(null);
        CardResponseDto cardResponseDto = new CardResponseDto(
                card.getId(),
                card.getTitle(),
                card.getContents(),
                card.getDeadline(),
                card.getManager().getEmail(),
                card.getActivityLogs().toString(),
                card.getComments());
        return cardResponseDto;
    }


    @Transactional
    public void updateCard(AuthUser authUser, CardRequestDto requestDto, Long cardId)
    {
        User manager = userRepository.findById(requestDto.getManagerId()).orElse(null);
        Lists lists = listsRepository.findById(requestDto.getListId()).orElse(null);
        Card card = cardRepository.findById(cardId).orElse(null);
        card.update(manager,lists,requestDto.getTitle(),requestDto.getContents(),requestDto.getDeadline());
        cardRepository.save(card);
    }

    public void deleteCard(AuthUser authUser, Long cardId) {
        Card card = cardRepository.findById(cardId).orElse(null);
        cardRepository.delete(card);
    }

    public Card findCard(AuthUser authUser, Long cardId)
    {
        Card card = cardRepository.findById(cardId).orElseThrow(null);
        return card;

    }
}
