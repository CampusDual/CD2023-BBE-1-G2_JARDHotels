package com.campusdual.jardhotels.controller;

import com.campusdual.jardhotels.api.IRoomService;
import com.campusdual.jardhotels.model.dto.RoomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private IRoomService roomService;

    @PostMapping(value = "/add")
    public int addRoom(@RequestBody RoomDTO roomDTO) {
        return roomService.insertRoom(roomDTO);
    }

    @DeleteMapping(value = "/delete")
    public int deleteRoom(@RequestBody RoomDTO roomDTO) {
        return roomService.deleteRoom(roomDTO);
    }

    @GetMapping(value = "/getAll")
    public List<RoomDTO> getAllRooms() {
        return roomService.queryAll();
    }

}
