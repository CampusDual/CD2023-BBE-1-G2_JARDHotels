package com.campusdual.jardhotels.service;

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

import static org.junit.jupiter.api.Assertions.*;
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

        // given
        Room room = new Room();
        room.setId(1);
        RoomDTO roomDTO = RoomMapper.INSTANCE.toDto(room);

        // when
        when(roomDAO.saveAndFlush(any(Room.class))).thenReturn(room);
        int insertedId = roomService.insertRoom(roomDTO);

        // then
        verify(roomDAO, times(1)).saveAndFlush(any(Room.class));
        assertEquals(room.getId(), insertedId);
    }

    @Test
    void queryAllRoomTest() {

        // given
        List<Room> rooms = List.of(new Room(), new Room(), new Room());

        // when
        when(roomDAO.findAll()).thenReturn(rooms);
        List<RoomDTO> queriedRooms = roomService.queryAll();

        // then
        assertEquals(rooms.size(), queriedRooms.size());
    }

    @Test
    void queryRoomTest() {

        // given
        Room room = new Room();
        room.setId(1);
        RoomDTO roomDTO = RoomMapper.INSTANCE.toDto(room);

        // when
        when(roomDAO.getReferenceById(anyInt())).thenReturn(room);
        RoomDTO queriedRoom = roomService.queryRoom(roomDTO);

        // then
        verify(roomDAO, times(1)).getReferenceById(anyInt());
        assertNotNull(queriedRoom);
        assertEquals(room.getId(), queriedRoom.getId());
    }

    @Test
    void getNonExistingRoomTest() {

        // given
        int nonExistentId = 99;
        Room room = new Room();
        room.setId(nonExistentId);
        RoomDTO roomDTO = RoomMapper.INSTANCE.toDto(room);

        // when
        when(roomDAO.getReferenceById(nonExistentId)).thenReturn(null);
        RoomDTO queriedRoom = roomService.queryRoom(roomDTO);

        // then
        verify(roomDAO, times(1)).getReferenceById(nonExistentId);
        assertNull(queriedRoom);
    }

    @Test
    void updateRoomTest() {

        // given
        Room room = new Room();
        room.setId(1);
        RoomDTO roomDTO = RoomMapper.INSTANCE.toDto(room);

        // when
        when(roomDAO.saveAndFlush(any(Room.class))).thenReturn(room);
        RoomDTO updatedRoom = roomService.updateRoom(roomDTO);

        // then
        verify(roomDAO, times(1)).saveAndFlush(any(Room.class));
        assertEquals(room.getId(), updatedRoom.getId());
    }
    
    @Test
    void deleteRoomTest() {

        // given
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1);

        // when
        doNothing().when(roomDAO).delete(any(Room.class));
        int id = roomService.deleteRoom(roomDTO);

        // then
        verify(roomDAO, times(1)).delete(any(Room.class));
        assertEquals(roomDTO.getId(), id);
    }
}
