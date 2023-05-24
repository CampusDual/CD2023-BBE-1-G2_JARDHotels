package com.campusdual.jardhotels.controller;


import com.campusdual.jardhotels.exceptions.*;
import com.campusdual.jardhotels.model.Booking;
import com.campusdual.jardhotels.model.dto.BookingDTO;
import com.campusdual.jardhotels.model.dto.GuestDTO;
import com.campusdual.jardhotels.model.dto.HotelDTO;
import com.campusdual.jardhotels.model.dto.RoomDTO;
import com.campusdual.jardhotels.service.BookingService;
import com.campusdual.jardhotels.service.GuestService;
import com.campusdual.jardhotels.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.json.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    BookingController bookingController;

    @Mock
    BookingService bookingService;

    @Mock
    RoomService roomService;

    @Mock
    GuestService guestService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    void addBookingTest() throws Exception {

        // given
        Booking booking = new Booking();
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1);
        GuestDTO guestDTO = new GuestDTO();
        guestDTO.setId(1);

        // when
        when(roomService.queryRoom(any(RoomDTO.class))).thenReturn(roomDTO);
        when(guestService.queryGuest(any(GuestDTO.class))).thenReturn(guestDTO);

        MvcResult addBookingMvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/bookings/add").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(booking))).andReturn();

        // then
        assertEquals(HttpStatus.OK.value(), addBookingMvcResult.getResponse().getStatus());
    }

    @Test
    void addBookingWhenRoomNotFoundTest() throws Exception {

        // given
        BookingDTO booking = new BookingDTO();
        booking.setRoom(1);


        // when
        when(roomService.queryRoom(any(RoomDTO.class))).thenThrow(new RoomNotFound("Room not found"));

        MvcResult addBookingMvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/bookings/add")
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(booking))).andReturn();

        // then
        assertEquals("Room not found", addBookingMvcResult.getResponse().getErrorMessage());
    }

    @Test
    void addBookingWhenGuestNotFoundTest() throws Exception {

        // given
        BookingDTO booking = new BookingDTO();
        booking.setRoom(1);
        booking.setGuest(1);

        // when
        when(roomService.queryRoom(any(RoomDTO.class))).thenReturn(new RoomDTO());
        when(guestService.queryGuest(any(GuestDTO.class))).thenThrow(new HotelNotFound("Guest not found"));

        MvcResult addBookingMvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/bookings/add")
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(booking))).andReturn();

        // then
        assertEquals("Guest not found", addBookingMvcResult.getResponse().getErrorMessage());
    }

    @Test
    void addBookingWhenCheckinDateLowerThanCurrentDateTest() throws Exception {
        LocalDate currentDate = LocalDate.now();
        LocalDate previousDay = currentDate.minusDays(1);

        Date previousDate = Date.from(previousDay.atStartOfDay(ZoneId.systemDefault()).toInstant());
        // given
        BookingDTO booking = new BookingDTO();
        booking.setRoom(1);
        booking.setGuest(1);
        booking.setCheckindate(previousDate);

        // when
        when(roomService.queryRoom(any(RoomDTO.class))).thenReturn(new RoomDTO());
        when(guestService.queryGuest(any(GuestDTO.class))).thenReturn(new GuestDTO());
        when(bookingService.insertBooking(any(BookingDTO.class))).thenThrow(new CheckinLowerThanCurrentDate("Check-in date must be greater than or equal to current date"));

        MvcResult addBookingMvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/bookings/add")
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(booking))).andReturn();

        // then
        assertEquals("Check-in date must be greater than or equal to current date", addBookingMvcResult.getResponse().getErrorMessage());

    }

    @Test
    void addBookingWhenCheckoutDateLowerThanCheckinDateTest() throws Exception {
        String f1 = "2023-05-23";
        String f2 = "2023-05-22";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaEntrada = null;
        Date fechaSalida = null;

        try {
            fechaEntrada = dateFormat.parse(f1);
            fechaSalida = dateFormat.parse(f2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // given
        BookingDTO booking = new BookingDTO();
        booking.setRoom(1);
        booking.setGuest(1);
        booking.setCheckindate(fechaEntrada);
        booking.setCheckoutdate(fechaSalida);

        // when
        when(roomService.queryRoom(any(RoomDTO.class))).thenReturn(new RoomDTO());
        when(guestService.queryGuest(any(GuestDTO.class))).thenReturn(new GuestDTO());
        when(bookingService.insertBooking(any(BookingDTO.class))).thenThrow(new CheckinGreaterThanCheckout("Check-out date must be greater than check-in date"));

        MvcResult addBookingMvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/bookings/add")
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(booking))).andReturn();

        // then
        assertEquals("Check-out date must be greater than check-in date", addBookingMvcResult.getResponse().getErrorMessage());
    }

    @Test
    void addBookingWhenTheDateRangeOverlapsWiththeDatesOfAnExistingBookingTest() throws Exception {
        String f1 = "2023-05-23";
        String f2 = "2023-05-25";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaEntrada = null;
        Date fechaSalida = null;

        try {
            fechaEntrada = dateFormat.parse(f1);
            fechaSalida = dateFormat.parse(f2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // given
        BookingDTO booking = new BookingDTO();
        booking.setRoom(1);
        booking.setGuest(1);
        booking.setCheckindate(fechaEntrada);
        booking.setCheckoutdate(fechaSalida);

        // when
        when(roomService.queryRoom(any(RoomDTO.class))).thenReturn(new RoomDTO());
        when(guestService.queryGuest(any(GuestDTO.class))).thenReturn(new GuestDTO());
        when(bookingService.insertBooking(any(BookingDTO.class))).thenThrow(new OverlappingBooking("The date range overlaps with the dates of an existing booking"));

        MvcResult addBookingMvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/bookings/add")
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(booking))).andReturn();

        // then
        assertEquals("The date range overlaps with the dates of an existing booking", addBookingMvcResult.getResponse().getErrorMessage());
    }


}

