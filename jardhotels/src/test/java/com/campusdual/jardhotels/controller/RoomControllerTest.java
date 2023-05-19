package com.campusdual.jardhotels.controller;


import com.campusdual.jardhotels.exceptions.HotelNotFound;
import com.campusdual.jardhotels.exceptions.RoomNotFound;
import com.campusdual.jardhotels.model.Hotel;
import com.campusdual.jardhotels.model.Room;
import com.campusdual.jardhotels.model.dto.HotelDTO;
import com.campusdual.jardhotels.model.dto.RoomDTO;
import com.campusdual.jardhotels.model.dto.dtomapper.HotelMapper;
import com.campusdual.jardhotels.model.dto.dtomapper.RoomMapper;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RoomControllerTest {
    private MockMvc mockMvc;
    @InjectMocks
    RoomController roomController;
    @Mock
    HotelService hotelService;
    @Mock
    RoomService roomService;
    @BeforeEach
    void setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();
    }
    @Test
    void addRoomTest() throws Exception {

        Hotel hotel = new Hotel();
        hotel.setId(1);
        Room room = new Room();
        room.setNumber(20);
        room.setCapacity(1);
        room.setDescription("descripcion");
        room.setHotel(hotel);
        RoomDTO roomDTO = RoomMapper.INSTANCE.toDto(room);
        HotelDTO hotelDTO = HotelMapper.INSTANCE.toDto(hotel);

        when(hotelService.queryHotel(any(HotelDTO.class))).thenReturn(hotelDTO);
        when(roomService.insertRoom(any(RoomDTO.class))).thenReturn(1);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rooms/add")
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(roomDTO))).andReturn();
        assertEquals(HttpStatus.OK.value(),mvcResult.getResponse().getStatus());
    }
    @Test
    void addRoomToNonExistingHotelTest() throws Exception {

        Hotel hotel = new Hotel();
        hotel.setId(1);
        Room room = new Room();
        room.setNumber(20);
        room.setCapacity(1);
        room.setDescription("descripcion");
        room.setHotel(hotel);
        RoomDTO roomDTO = RoomMapper.INSTANCE.toDto(room);
        HotelDTO hotelDTO = HotelMapper.INSTANCE.toDto(hotel);

        when(hotelService.queryHotel(any(HotelDTO.class))).thenThrow(new HotelNotFound("Hotel not found"));
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rooms/add")
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(roomDTO))).andReturn();
        assertEquals("Hotel not found",mvcResult.getResponse().getErrorMessage());
    }
    @Test
    void deleteRoomTest() throws Exception {

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1);
        roomDTO.setNumber(20);
        roomDTO.setCapacity(1);
        roomDTO.setDescription("descripcion");
        roomDTO.setHotel(1);

        when(roomService.queryRoom(any(RoomDTO.class))).thenReturn(roomDTO);
        when(roomService.deleteRoom(any(RoomDTO.class))).thenReturn(1);
        MvcResult deleteMvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rooms/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roomDTO)))
                .andReturn();
        assertEquals(HttpStatus.OK.value(), deleteMvcResult.getResponse().getStatus());
        verify(roomService, times(1)).queryRoom(any(RoomDTO.class));
        verify(roomService, times(1)).deleteRoom(any(RoomDTO.class));
    }
    @Test
    void deleteNotExistingRoomTest() throws Exception {

        RoomDTO roomDTO = new RoomDTO();
        when(roomService.queryRoom(any(RoomDTO.class))).thenThrow(new RoomNotFound("Room not found"));
        MvcResult deleteMvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/rooms/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roomDTO)))
                .andReturn();
        assertEquals("Room not found",deleteMvcResult.getResponse().getErrorMessage());
    }

    @Test
    void getAllRoomTest() throws Exception{

        List<RoomDTO> roomDTOS = new ArrayList<RoomDTO>();
        RoomDTO room1 = new RoomDTO();
        room1.setId(1);
        room1.setNumber(20);
        room1.setCapacity(1);
        room1.setDescription("descripcion");
        room1.setHotel(1);
        RoomDTO room2 = new RoomDTO();
        room2.setId(2);
        room2.setNumber(22);
        room2.setCapacity(2);
        room2.setDescription("descripcion");
        room2.setHotel(1);
        roomDTOS.add(room1);
        roomDTOS.add(room2);

        when(roomService.queryAll()).thenReturn(roomDTOS);
        MvcResult getAllMvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rooms/getAll")).andReturn();
        assertEquals(HttpStatus.OK.value(), getAllMvcResult.getResponse().getStatus());
        verify(roomService, times(1)).queryAll();
        assertEquals(roomDTOS.size(),new ObjectMapper().readTree(getAllMvcResult.getResponse().getContentAsString()).size());

    }
}

