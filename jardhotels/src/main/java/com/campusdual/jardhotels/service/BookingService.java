package com.campusdual.jardhotels.service;

import com.campusdual.jardhotels.api.IBookingService;
import com.campusdual.jardhotels.model.Booking;
import com.campusdual.jardhotels.model.dao.BookingDAO;
import com.campusdual.jardhotels.model.dto.BookingDTO;
import com.campusdual.jardhotels.model.dto.dtomapper.BookingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("BookingService")
@Lazy
public class BookingService implements IBookingService {

    @Autowired
    private BookingDAO bookingDAO;

    @Override
    public int insertBooking(BookingDTO bookingDTO) {
        Booking booking = BookingMapper.INSTANCE.toEntity(bookingDTO);
        bookingDAO.saveAndFlush(booking);
        return booking.getId();
    }


}
