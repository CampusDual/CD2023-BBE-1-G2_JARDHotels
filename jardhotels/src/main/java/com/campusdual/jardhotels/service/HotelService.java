package com.campusdual.jardhotels.service;

import com.campusdual.jardhotels.api.IHotelService;
import com.campusdual.jardhotels.model.Hotel;
import com.campusdual.jardhotels.model.dao.HotelDAO;
import com.campusdual.jardhotels.model.dto.HotelDTO;
import com.campusdual.jardhotels.model.dto.dtomapper.HotelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service("HotelService")
@Lazy
public class HotelService implements IHotelService {

    @Autowired
    private HotelDAO hotelDAO;

    @Override
    public int insertHotel(HotelDTO hotelDTO) {
        Hotel hotel = HotelMapper.INSTANCE.toEntity(hotelDTO);
        hotelDAO.saveAndFlush(hotel);
        return hotel.getId();
    }

}
