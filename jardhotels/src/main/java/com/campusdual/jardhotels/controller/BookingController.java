package com.campusdual.jardhotels.controller;

import com.campusdual.jardhotels.api.IBookingService;
import com.campusdual.jardhotels.api.IGuestService;
import com.campusdual.jardhotels.api.IRoomService;
import com.campusdual.jardhotels.exceptions.*;
import com.campusdual.jardhotels.model.dto.BookingDTO;
import com.campusdual.jardhotels.model.dto.GuestDTO;
import com.campusdual.jardhotels.model.dto.RoomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private IBookingService bookingService;

    @Autowired
    private IGuestService guestService;

    @Autowired
    private IRoomService roomService;

    @PostMapping(value = "/add")
    public int addBooking(@RequestBody BookingDTO bookingDTO) {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(bookingDTO.getRoom());
        try {
            roomService.queryRoom(roomDTO);
        } catch (Exception e) {
            throw new RoomNotFound("Room not found");
        }
        GuestDTO guestDTO = new GuestDTO();
        guestDTO.setId(bookingDTO.getGuest());
        try {
            guestService.queryGuest(guestDTO);
        } catch (Exception e) {
            throw new GuestNotFound("Guest not found");
        }
        int toret = 0;
        try {
            toret = bookingService.insertBooking(bookingDTO);
        } catch (Exception e) {
            if (e.getMessage().contains("Check-in date must be greater than or equal to current date")) {
                throw new CheckinLowerThanCurrentDate("Check-in date must be greater than or equal to current date");
            } else if (e.getMessage().contains("Check-out date must be greater than check-in date")) {
                throw new CheckinGreaterThanCheckout("Check-out date must be greater than check-in date");
            } else {
                throw new OverlappingBooking("The date range overlaps with the dates of an existing booking");
            }
        }
        return toret;

    }

}
