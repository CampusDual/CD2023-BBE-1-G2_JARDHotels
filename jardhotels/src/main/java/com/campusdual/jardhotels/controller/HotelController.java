package com.campusdual.jardhotels.controller;

import com.campusdual.jardhotels.api.IHotelService;
import com.campusdual.jardhotels.model.dto.HotelDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    private IHotelService hotelService;

    @PostMapping(value = "/add")
    public int addHotel(@RequestBody HotelDTO hotelDTO) {
        return hotelService.insertHotel(hotelDTO);
    }

    @DeleteMapping(value = "/delete")
    public int deleteHotel(@RequestBody HotelDTO hotelDTO) {
        return hotelService.deleteHotel(hotelDTO);
    }

    @GetMapping(value = "/getAll")
    public List<HotelDTO> getAllHotels() {
        return hotelService.queryAll();
    }

}
