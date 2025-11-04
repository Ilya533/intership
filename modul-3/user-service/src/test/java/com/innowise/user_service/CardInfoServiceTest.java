package com.innowise.user_service.service;

import com.innowise.user_service.mapper.CardInfoMapper;
import com.innowise.user_service.model.domain.CardInfoEntity;
import com.innowise.user_service.model.dto.CardInfoDTO;
import com.innowise.user_service.repository.CardInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.innowise.user_service.service.CardInfoService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardInfoServiceTest {

    @Mock
    private CardInfoRepository cardInfoRepository;

    @Mock
    private CardInfoMapper cardInfoMapper;

    @InjectMocks
    private CardInfoService cardInfoService;

    @Test
    void getCardById_ShouldReturnCard_WhenCardExists() {
        Long cardId = 1L;
        CardInfoEntity cardEntity = new CardInfoEntity();
        CardInfoDTO cardDTO = new CardInfoDTO();

        when(cardInfoRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));
        when(cardInfoMapper.toDto(cardEntity)).thenReturn(cardDTO);

        CardInfoDTO result = cardInfoService.getCardById(cardId);

        assertNotNull(result);
        verify(cardInfoRepository).findById(cardId);
    }

    @Test
    void deleteCard_ShouldDelete_WhenCardExists() {
        Long cardId = 1L;

        when(cardInfoRepository.existsById(cardId)).thenReturn(true);

        cardInfoService.deleteCard(cardId);

        verify(cardInfoRepository).deleteById(cardId);
    }
}