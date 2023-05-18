package com.campusdual.jardhotels.controller;


import com.campusdual.jardhotels.model.Hotel;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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
    @Test void deleteHotelTest() throws Exception {

    }
}
