package com.campusdual.jardhotels.api;

import com.campusdual.jardhotels.model.dto.HotelDTO;

import java.util.List;

public interface IHotelService {
    int insertHotel(HotelDTO hotelDTO);

    int deleteHotel(HotelDTO hotelDTO);

    List<HotelDTO> queryAll();

    HotelDTO queryHotel(HotelDTO hotelDTO);

}
