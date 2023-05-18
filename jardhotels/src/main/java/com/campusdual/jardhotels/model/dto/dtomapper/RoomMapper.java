package com.campusdual.jardhotels.model.dto.dtomapper;

import com.campusdual.jardhotels.model.Hotel;
import com.campusdual.jardhotels.model.Room;
import com.campusdual.jardhotels.model.dto.RoomDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RoomMapper {


    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    @Mapping(source = "hotel", target = "hotel", qualifiedByName = "hotelToHotelDto")
    RoomDTO toDto(Room room);

    @Mapping(source = "hotel", target = "hotel", qualifiedByName = "hotelDtoToHotel")
    Room toEntity(RoomDTO roomDTO);

    List<RoomDTO> toDtoList(List<Room> rooms);

    List<Room> toEntityList(List<RoomDTO> roomsDTO);

    @Named("hotelDtoToHotel")
    default Hotel hotelDtoToHotel(int hotel) {
        Hotel h = new Hotel();
        h.setId(hotel);
        return h;
    }

    @Named("hotelToHotelDto")
    default int hotelToHotelDto(Hotel hotel) {
        return hotel != null ? hotel.getId() : 0;
    }
}
