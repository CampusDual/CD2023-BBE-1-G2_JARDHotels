package com.campusdual.jardhotels.api;

import com.campusdual.jardhotels.model.dto.RoomDTO;

import java.util.List;

public interface IRoomService {
    int insertRoom(RoomDTO roomDTO);

    int deleteRoom(RoomDTO roomDTO);

    List<RoomDTO> queryAll();

    RoomDTO queryRoom(RoomDTO roomDTO);

    RoomDTO updateRoom(RoomDTO roomDTO);
}
