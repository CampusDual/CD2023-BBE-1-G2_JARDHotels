package com.campusdual.jardhotels.model.dto.dtomapper;

import com.campusdual.jardhotels.model.Room;
import com.campusdual.jardhotels.model.dto.RoomDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    RoomDTO toDto(Room room);

    Room toEntity(RoomDTO roomDTO);

    List<RoomDTO> toDtoList(List<Room> rooms);

    List<Room> toEntityList(List<RoomDTO> roomsDTO);
}
