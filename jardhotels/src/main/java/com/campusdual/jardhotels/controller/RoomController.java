package com.campusdual.jardhotels.controller;

import com.campusdual.jardhotels.api.IHotelService;
import com.campusdual.jardhotels.api.IRoomService;
import com.campusdual.jardhotels.exceptions.ForbiddenOperation;
import com.campusdual.jardhotels.exceptions.HotelNotFound;
import com.campusdual.jardhotels.exceptions.NotUniqueRoomNumber;
import com.campusdual.jardhotels.exceptions.RoomNotFound;
import com.campusdual.jardhotels.model.dto.HotelDTO;
import com.campusdual.jardhotels.model.dto.RoomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IHotelService hotelService;

    @PostMapping(value = "/add")
    public int addRoom(@RequestBody RoomDTO roomDTO) {
        HotelDTO hotelDTO = new HotelDTO();
        hotelDTO.setId(roomDTO.getHotel());
        try {
            hotelService.queryHotel(hotelDTO);
        } catch (Exception e) {
            throw new HotelNotFound("Hotel not found");
        }
        int toret = 0;
        try {
            toret = roomService.insertRoom(roomDTO);
        } catch (Exception e) {
            throw new NotUniqueRoomNumber("A room with this number already exists in the same hotel");

        }
        return toret;

    }

    @DeleteMapping(value = "/delete")
    public int deleteRoom(@RequestBody RoomDTO roomDTO) {
        try {
            roomService.queryRoom(roomDTO);
        } catch (Exception e) {
            throw new RoomNotFound("Room not found");
        }
        return roomService.deleteRoom(roomDTO);
    }

    @GetMapping(value = "/getAll")
    public List<RoomDTO> getAllRooms() {
        return roomService.queryAll();
    }

    @PutMapping(value = "/update")
    public RoomDTO updateRoom(@RequestBody RoomDTO roomDTO) {

        RoomDTO roomDB;
        try {
            roomDB = roomService.queryRoom(roomDTO);

        } catch (Exception e) {
            throw new RoomNotFound("Room not found");
        }

        if (roomDTO.getHotel() != 0 && roomDTO.getHotel() != roomDB.getHotel())
            throw new ForbiddenOperation("You cannot change the hotel");

        if (roomDTO.getNumber() != 0)
            roomDB.setNumber(roomDTO.getNumber());

        if (roomDTO.getCapacity() != 0)
            roomDB.setCapacity(roomDTO.getCapacity());

        if (roomDTO.getDescription() != null)
            roomDB.setDescription(roomDTO.getDescription());

        return roomService.updateRoom(roomDB);
    }
}
