package com.campusdual.jardhotels.model.dto.dtomapper;
import com.campusdual.jardhotels.model.Hotel;
import com.campusdual.jardhotels.model.dto.HotelDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface HotelMapper {
    HotelMapper INSTANCE = Mappers.getMapper(HotelMapper.class);

    HotelDTO toDto(Hotel hotel);


    Hotel toEntity(HotelDTO hotelDTO);
    List<HotelDTO> toDtoList(List<Hotel>hotels);

    List<Hotel> toEntityList(List<HotelDTO>hotelsDTO);
}
