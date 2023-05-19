package com.campusdual.jardhotels.service;

import com.campusdual.jardhotels.model.Hotel;
import com.campusdual.jardhotels.model.dao.HotelDAO;
import com.campusdual.jardhotels.model.dto.HotelDTO;
import com.campusdual.jardhotels.model.dto.dtomapper.HotelMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)

public class HotelServiceTest {
    @Mock
    HotelDAO hotelDAO;

    @InjectMocks
    HotelService hotelService;

    @Test
    void addHotelTest() {

        // given
        Hotel hotel = new Hotel();
        hotel.setId(1);
        HotelDTO hotelDTO = HotelMapper.INSTANCE.toDto(hotel);

        // when
        when(hotelDAO.saveAndFlush(any(Hotel.class))).thenReturn(hotel);
        int insertedId = hotelService.insertHotel(hotelDTO);

        // then
        verify(hotelDAO, times(1)).saveAndFlush(any(Hotel.class));
        assertEquals(hotel.getId(), insertedId);
    }

    @Test
    void queryAllHotelTest() {

        // given
        List<Hotel> hotels = List.of(new Hotel(), new Hotel(), new Hotel());

        // when
        when(hotelDAO.findAll()).thenReturn(hotels);
        List<HotelDTO> queriedHotels = hotelService.queryAll();

        // then
        assertEquals(hotels.size(), queriedHotels.size());
    }

    @Test
    void queryHotelTest() {

        // given
        Hotel hotel = new Hotel();
        hotel.setId(1);
        HotelDTO hotelDTO = HotelMapper.INSTANCE.toDto(hotel);

        // when
        when(hotelDAO.getReferenceById(anyInt())).thenReturn(hotel);
        HotelDTO queriedHotel = hotelService.queryHotel(hotelDTO);

        // then
        verify(hotelDAO, times(1)).getReferenceById(anyInt());
        assertNotNull(queriedHotel);
        assertEquals(hotel.getId(), queriedHotel.getId());
    }

    @Test
    void getNonExistingHotelTest() {

        // given
        int nonExistentId = 99;
        Hotel hotel = new Hotel();
        hotel.setId(nonExistentId);
        HotelDTO hotelDTO = HotelMapper.INSTANCE.toDto(hotel);

        // when
        when(hotelDAO.getReferenceById(nonExistentId)).thenReturn(null);
        HotelDTO queriedHotel = hotelService.queryHotel(hotelDTO);

        // then
        verify(hotelDAO, times(1)).getReferenceById(nonExistentId);
        assertNull(queriedHotel);
    }

    @Test
    void updateHotelTest() {

        // given
        Hotel hotel = new Hotel();
        hotel.setId(1);
        HotelDTO hotelDTO = HotelMapper.INSTANCE.toDto(hotel);

        // when
        when(hotelDAO.saveAndFlush(any(Hotel.class))).thenReturn(hotel);
        HotelDTO updatedHotel = hotelService.updateHotel(hotelDTO);

        // then
        verify(hotelDAO, times(1)).saveAndFlush(any(Hotel.class));
        assertEquals(hotel.getId(), updatedHotel.getId());
    }

    @Test
    void deleteHotelTest() {

        // given
        HotelDTO hotelDTO = new HotelDTO();
        hotelDTO.setId(1);

        // when
        doNothing().when(hotelDAO).delete(any(Hotel.class));
        int id = hotelService.deleteHotel(hotelDTO);

        // then
        verify(hotelDAO, times(1)).delete(any(Hotel.class));
        assertEquals(hotelDTO.getId(), id);
    }
}
