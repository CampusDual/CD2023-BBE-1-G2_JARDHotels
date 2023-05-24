package com.campusdual.jardhotels.service;

import com.campusdual.jardhotels.model.Booking;
import com.campusdual.jardhotels.model.dao.BookingDAO;
import com.campusdual.jardhotels.model.dao.RoomDAO;
import com.campusdual.jardhotels.model.dto.BookingDTO;
import com.campusdual.jardhotels.model.dto.dtomapper.BookingMapper;
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

public class BookingServiceTest {
    @Mock
    BookingDAO bookingDAO;

    @InjectMocks
    BookingService bookingService;

    @Mock
    RoomDAO roomDAO;

    @InjectMocks
    RoomService roomService;

    @Test
    void addBookingTest() {

        // given
        Booking booking = new Booking();
        booking.setId(1);
        BookingDTO bookingDTO = BookingMapper.INSTANCE.toDto(booking);

        // when
        when(bookingDAO.saveAndFlush(any(Booking.class))).thenReturn(booking);
        int insertedId = bookingService.insertBooking(bookingDTO);

        // then
        verify(bookingDAO, times(1)).saveAndFlush(any(Booking.class));
        assertEquals(booking.getId(), insertedId);
    }

}
