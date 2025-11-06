package com.innowise.user_service.mapper;

import com.innowise.user_service.model.domain.CardInfoEntity;
import com.innowise.user_service.model.dto.CardInfoDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {

    CardInfoDTO toDto(CardInfoEntity entity);

    CardInfoEntity toEntity(CardInfoDTO dto);
}
