package com.campusdual.jardhotels.controller;

import com.campusdual.jardhotels.exceptions.HotelNotFound;
import com.campusdual.jardhotels.exceptions.RoomNotFound;
import com.campusdual.jardhotels.model.dto.HotelDTO;
import com.campusdual.jardhotels.model.dto.RoomDTO;
import com.campusdual.jardhotels.service.HotelService;
import com.campusdual.jardhotels.service.RoomService;
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
public class  RoomControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    RoomController roomController;

    @Mock
    HotelService hotelService;

    @Mock
    RoomService roomService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();
    }

    @Test
    void addRoomTest() throws Exception {

        // given
        HotelDTO hotelDTO = new HotelDTO();
        hotelDTO.setId(1);
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setHotel(1);

        // when
        when(hotelService.queryHotel(any(HotelDTO.class))).thenReturn(hotelDTO);
        when(roomService.insertRoom(any(RoomDTO.class))).thenReturn(1);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rooms/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(roomDTO))).andReturn();

        // then
        assertEquals(HttpStatus.OK.value(),mvcResult.getResponse().getStatus());
    }

    @Test
    void addRoomToNonExistingHotelTest() throws Exception {

        // given
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setHotel(99);

        // when
        when(hotelService.queryHotel(any(HotelDTO.class))).thenThrow(new HotelNotFound("Hotel not found"));
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rooms/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(roomDTO))).andReturn();

        // then
        assertEquals("Hotel not found",mvcResult.getResponse().getErrorMessage());
    }

    @Test
    void getAllRoomTest() throws Exception{

        // given
        List<RoomDTO> roomDTOS = List.of(new RoomDTO(), new RoomDTO());

        // when
        when(roomService.queryAll()).thenReturn(roomDTOS);
        MvcResult getAllMvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rooms/getAll")).andReturn();

        // then
        verify(roomService, times(1)).queryAll();
        assertEquals(HttpStatus.OK.value(), getAllMvcResult.getResponse().getStatus());
        assertEquals(roomDTOS.size(),new ObjectMapper().readTree(getAllMvcResult.getResponse().getContentAsString()).size());

    }

    @Test
    void updateRoomTest() throws Exception {

        // given
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1);

        // when
        when(roomService.queryRoom(any(RoomDTO.class))).thenReturn(roomDTO);
        when(roomService.updateRoom(any(RoomDTO.class))).thenReturn(roomDTO);
        MvcResult updateMvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rooms/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(roomDTO))).andReturn();

        // then
        assertEquals(HttpStatus.OK.value(), updateMvcResult.getResponse().getStatus());
    }

    @Test
    void deleteRoomTest() throws Exception {

        // given
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1);

        // when
        when(roomService.queryRoom(any(RoomDTO.class))).thenReturn(roomDTO);
        when(roomService.deleteRoom(any(RoomDTO.class))).thenReturn(1);
        MvcResult deleteMvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rooms/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roomDTO))).andReturn();

        // then
        verify(roomService, times(1)).queryRoom(any(RoomDTO.class));
        verify(roomService, times(1)).deleteRoom(any(RoomDTO.class));
        assertEquals(HttpStatus.OK.value(), deleteMvcResult.getResponse().getStatus());
    }

    @Test
    void deleteNotExistingRoomTest() throws Exception {

        // given
        RoomDTO roomDTO = new RoomDTO();

        // when
        when(roomService.queryRoom(any(RoomDTO.class))).thenThrow(new RoomNotFound("Room not found"));
        MvcResult deleteMvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rooms/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roomDTO))).andReturn();

        // then
        assertEquals("Room not found",deleteMvcResult.getResponse().getErrorMessage());
    }
}

