package com.innowise.user_service.service;

import com.innowise.user_service.exception.CardNotFoundException;
import com.innowise.user_service.mapper.CardInfoMapper;
import com.innowise.user_service.model.domain.CardInfoEntity;
import com.innowise.user_service.model.dto.CardInfoDTO;
import com.innowise.user_service.repository.CardInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final CardInfoMapper cardInfoMapper;

    public CardInfoDTO getCardById(Long id) {
        return cardInfoRepository.findById(id)
                .map(cardInfoMapper::toDto)
                .orElseThrow(() -> new CardNotFoundException(id));
    }

    public Page<CardInfoDTO> getAllCards(Pageable pageable) {
        return cardInfoRepository.findAll(pageable)
                .map(cardInfoMapper::toDto);
    }

    public CardInfoDTO createCard(CardInfoDTO cardDTO) {
        CardInfoEntity entity = cardInfoMapper.toEntity(cardDTO);
        CardInfoEntity saved = cardInfoRepository.save(entity);
        return cardInfoMapper.toDto(saved);
    }

    public CardInfoDTO updateCard(Long id, CardInfoDTO cardDTO) {
        CardInfoEntity entity = cardInfoRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        entity.setNumber(cardDTO.getNumber());
        entity.setHolder(cardDTO.getHolder());
        entity.setExpirationDate(cardDTO.getExpirationDate());

        return cardInfoMapper.toDto(entity);
    }

    public void deleteCard(Long id) {
        if (!cardInfoRepository.existsById(id)) {
            throw new CardNotFoundException(id);
        }
        cardInfoRepository.deleteById(id);
    }
}