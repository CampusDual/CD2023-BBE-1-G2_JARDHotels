package com.campusdual.jardhotels.model.dto.dtomapper;

import com.campusdual.jardhotels.model.Guest;
import com.campusdual.jardhotels.model.dto.GuestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface GuestMapper {
    GuestMapper INSTANCE = Mappers.getMapper(GuestMapper.class);

    GuestDTO toDto(Guest guest);

    Guest toEntity(GuestDTO guestDTO);

    List<GuestDTO> toDtoList(List<Guest> guests);

    List<Guest> toEntityList(List<GuestDTO> guestsDTO);
}
