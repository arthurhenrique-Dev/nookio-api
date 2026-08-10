package com.henrique.nookio_api.modules.messages.interfaces;

import com.henrique.nookio_api.modules.files.models.File;
import com.henrique.nookio_api.modules.messages.dto.MessageResponseDto;
import com.henrique.nookio_api.modules.messages.models.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "id", source = "message.id")
    @Mapping(target = "file", source = "file")
    MessageResponseDto toResponse(Message message, File file);
}