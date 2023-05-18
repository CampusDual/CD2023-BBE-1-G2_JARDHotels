package com.campusdual.jardhotels.service;


import com.campusdual.jardhotels.model.Hotel;
import com.campusdual.jardhotels.model.Room;
import com.campusdual.jardhotels.model.dao.RoomDAO;
import com.campusdual.jardhotels.model.dto.RoomDTO;
import com.campusdual.jardhotels.model.dto.dtomapper.RoomMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)

public class RoomServiceTest {
    @Mock
    RoomDAO roomDAO;

    @InjectMocks
    RoomService roomService;

    @Test
    void addRoomTest() {
        Hotel hotel = new Hotel();
        hotel.setId(1);
        hotel.setName("One");
        hotel.setAddress("address");
        hotel.setStars(1);

        Room room = new Room();
        room.setId(1);
        room.setNumber(1);
        room.setCapacity(1);
        room.setDescription("description");
        room.setHotel(hotel);

        RoomDTO roomDTO = RoomMapper.INSTANCE.toDto(room);
        when(roomDAO.saveAndFlush(any(Room.class))).thenReturn(room);
        int insertedId = roomService.insertRoom(roomDTO);
        assertNotNull(insertedId);
        assertEquals(room.getId(), insertedId);
        verify(roomDAO, times(1)).saveAndFlush(any(Room.class));
        verify(roomDAO, times(0)).findAll();
    }

    @Test
    void deleteRoomTest() {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1);

        doNothing().when(roomDAO).delete(any(Room.class));
        int id = roomService.deleteRoom(roomDTO);
        verify(roomDAO, times(1)).delete(any(Room.class));
        assertEquals(roomDTO.getId(), id);
    }

    @Test
    void queryAllRoomTest() {
        List<Room> rooms = List.of(new Room(), new Room(), new Room());
        when(roomDAO.findAll()).thenReturn(rooms);
        List<RoomDTO> roomDTOS = roomService.queryAll();
        assertEquals(3, roomDTOS.size());
    }
}
