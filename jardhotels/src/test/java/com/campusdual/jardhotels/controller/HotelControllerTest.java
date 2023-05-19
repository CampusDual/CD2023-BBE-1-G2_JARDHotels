package com.campusdual.jardhotels.controller;

import com.campusdual.jardhotels.exceptions.HotelNotFound;
import com.campusdual.jardhotels.model.Hotel;
import com.campusdual.jardhotels.model.dto.HotelDTO;
import com.campusdual.jardhotels.model.dto.dtomapper.HotelMapper;
import com.campusdual.jardhotels.service.HotelService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class HotelControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    HotelController hotelController;

    @Mock
    HotelService hotelService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(hotelController).build();
    }

    @Test
    void addHotelTest() throws Exception {

        // given
        Hotel hotel = new Hotel();

        // when
        MvcResult addHotelMvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/hotels/add")
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(hotel))).andReturn();

        // then
        assertEquals(HttpStatus.OK.value(), addHotelMvcResult.getResponse().getStatus());
    }

    @Test
    void getAllHotelTest() throws Exception {

        // given
        List<HotelDTO> hotelDTOS = List.of(new HotelDTO(), new HotelDTO());

        // when
        when(hotelService.queryAll()).thenReturn(hotelDTOS);
        MvcResult getAllMvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/hotels/getAll")).andReturn();

        // then
        verify(hotelService, times(1)).queryAll();
        assertEquals(HttpStatus.OK.value(), getAllMvcResult.getResponse().getStatus());
        assertEquals(hotelDTOS.size(), new ObjectMapper().readTree(getAllMvcResult.getResponse()
                .getContentAsString()).size());

    }

    @Test
    void updateHotelTest() throws Exception {

        // given
        HotelDTO hotelDTO = new HotelDTO();
        hotelDTO.setId(1);

        // when
        when(hotelService.queryHotel(any(HotelDTO.class))).thenReturn(hotelDTO);
        when(hotelService.updateHotel(any(HotelDTO.class))).thenReturn(hotelDTO);
        MvcResult updateMvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/hotels/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(hotelDTO))).andReturn();

        // then
        assertEquals(HttpStatus.OK.value(), updateMvcResult.getResponse().getStatus());
    }

    @Test
    void deleteHotelTest() throws Exception {

        // given
        HotelDTO hotelDTO = new HotelDTO();
        hotelDTO.setId(1);

        // when
        when(hotelService.queryHotel(any(HotelDTO.class))).thenReturn(hotelDTO);
        when(hotelService.deleteHotel(any(HotelDTO.class))).thenReturn(1);

        MvcResult deleteMvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/hotels/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(hotelDTO))).andReturn();

        // then
        verify(hotelService, times(1)).queryHotel(any(HotelDTO.class));
        verify(hotelService, times(1)).deleteHotel(any(HotelDTO.class));
        assertEquals(HttpStatus.OK.value(), deleteMvcResult.getResponse().getStatus());
    }

    @Test
    void deleteNotExistingHotelTest() throws Exception {

        // given
        HotelDTO hotelDTO = new HotelDTO();

        // when
        when(hotelService.queryHotel(any(HotelDTO.class))).thenThrow(new HotelNotFound("Hotel not found"));
        MvcResult deleteMvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/hotels/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(hotelDTO))).andReturn();

        // then
        assertEquals("Hotel not found",deleteMvcResult.getResponse().getErrorMessage());
    }
}

