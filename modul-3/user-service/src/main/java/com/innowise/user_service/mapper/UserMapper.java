package com.innowise.user_service.mapper;

import com.innowise.user_service.model.domain.UserEntity;
import com.innowise.user_service.model.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CardInfoMapper.class})
public interface UserMapper {

    UserDTO toDto(UserEntity user);

    UserEntity toEntity(UserDTO dto);
}
