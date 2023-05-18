package com.campusdual.jardhotels.controller;


import com.campusdual.jardhotels.exceptions.HotelNotFound;
import com.campusdual.jardhotels.model.Hotel;
import com.campusdual.jardhotels.model.dto.HotelDTO;
import com.campusdual.jardhotels.service.HotelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class HotelControllerTest {
    private MockMvc mockMvc;
    @InjectMocks
    HotelController hotelController;
    @Mock
    HotelService hotelService;
    @BeforeEach
    void setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(hotelController).build();
    }
    @Test
    void addHotelTest() throws Exception {
        Hotel hotel = new Hotel();
        hotel.setName("hotelname");
        hotel.setStars(1);
        hotel.setAddress("address");
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/hotels/add").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(hotel))).andReturn();
        assertEquals(HttpStatus.OK.value(),mvcResult.getResponse().getStatus());
    }
    @Test
    void deleteHotelTest() throws Exception {
        HotelDTO hotelDTO = new HotelDTO();
        hotelDTO.setId(1);
        hotelDTO.setName("Hotel");
        hotelDTO.setStars(5);
        hotelDTO.setAddress("Dirección");
        when(hotelService.queryHotel(any(HotelDTO.class))).thenReturn(hotelDTO);
        when(hotelService.deleteHotel(any(HotelDTO.class))).thenReturn(1);
        MvcResult deleteMvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/hotels/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(hotelDTO)))
                .andReturn();
        assertEquals(HttpStatus.OK.value(), deleteMvcResult.getResponse().getStatus());
        verify(hotelService, times(1)).queryHotel(any(HotelDTO.class));
        verify(hotelService, times(1)).deleteHotel(any(HotelDTO.class));
    }
    @Test
    void deleteNotExistingHotelTest() throws Exception{
        HotelDTO hotelDTO = new HotelDTO();
        when(hotelService.queryHotel(any(HotelDTO.class))).thenThrow(new HotelNotFound("Hotel not found"));
        MvcResult deleteMvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/hotels/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(hotelDTO)))
                .andReturn();
        assertEquals("Hotel not found",deleteMvcResult.getResponse().getErrorMessage());
    }

    @Test
    void getAllHotelTest() throws Exception{
        List<HotelDTO> hotelDTOS = new ArrayList<HotelDTO>();
        HotelDTO hotel1 = new HotelDTO();
        hotel1.setId(1);
        hotel1.setName("Hotel");
        hotel1.setStars(5);
        hotel1.setAddress("Dirección");
        HotelDTO hotel2 = new HotelDTO();
        hotel2.setId(2);
        hotel2.setName("Hotel2");
        hotel2.setStars(3);
        hotel2.setAddress("Dirección2");
        hotelDTOS.add(hotel1);
        hotelDTOS.add(hotel2);
        when(hotelService.queryAll()).thenReturn(hotelDTOS);
        MvcResult getAllMvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/hotels/getAll")).andReturn();
        assertEquals(HttpStatus.OK.value(), getAllMvcResult.getResponse().getStatus());
        verify(hotelService, times(1)).queryAll();
        assertEquals(hotelDTOS.size(),new ObjectMapper().readTree(getAllMvcResult.getResponse().getContentAsString()).size());

    }
}

