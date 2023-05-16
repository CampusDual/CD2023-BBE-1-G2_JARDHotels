package com.campusdual.jardhotels.service;

import com.campusdual.jardhotels.model.Hotel;
import com.campusdual.jardhotels.model.dao.HotelDAO;
import com.campusdual.jardhotels.model.dto.HotelDTO;
import com.campusdual.jardhotels.model.dto.dtomapper.HotelMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@ExtendWith(MockitoExtension.class)

public class HotelServiceTest {
    @Mock
    HotelDAO hotelDAO;

    @InjectMocks
    HotelService hotelService;

    @Test
    void addHotelTest() {
        Hotel hotel = new Hotel();
        hotel.setId(1);
        hotel.setName("One");
        hotel.setAddress("address");
        hotel.setStars(1);

        HotelDTO hotelDTO = HotelMapper.INSTANCE.toDto(hotel);
        when(hotelDAO.saveAndFlush(any(Hotel.class))).thenReturn(hotel);
        int insertedId = hotelService.insertHotel(hotelDTO);
        assertNotNull(insertedId);
        assertEquals(hotel.getId(), insertedId);
        verify(hotelDAO, times(1)).saveAndFlush(any(Hotel.class));
        verify(hotelDAO, times(0)).findAll();
    }

    @Test
    void deleteHotelTest() {
        HotelDTO hotelDTO = new HotelDTO();
        hotelDTO.setId(1);

        doNothing().when(hotelDAO).delete(any(Hotel.class));
        int id = hotelService.deleteHotel(hotelDTO);
        verify(hotelDAO, times(1)).delete(any(Hotel.class));
        assertEquals(hotelDTO.getId(), id);
    }

    @Test
    void queryAllHotelTest() {
        List<Hotel> hotels = List.of(new Hotel(), new Hotel(), new Hotel());
        when(hotelDAO.findAll()).thenReturn(hotels);
        List<HotelDTO> hotelDTOS = hotelService.queryAll();
        assertEquals(3, hotelDTOS.size());

    }
}
