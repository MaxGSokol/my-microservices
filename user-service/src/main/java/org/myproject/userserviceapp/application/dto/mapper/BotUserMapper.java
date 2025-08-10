package org.myproject.userserviceapp.application.dto.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.myproject.userserviceapp.application.dto.BotUserDto;
import org.myproject.userserviceapp.domain.entity.BotUser;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public abstract class BotUserMapper {

    public abstract BotUserDto toDto(BotUser botUser);

    public abstract BotUser toEntity(BotUserDto botUserDto);
}
