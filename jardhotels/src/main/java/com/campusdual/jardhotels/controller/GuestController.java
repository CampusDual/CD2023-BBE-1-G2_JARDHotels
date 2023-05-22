package com.campusdual.jardhotels.controller;

import com.campusdual.jardhotels.api.IGuestService;
import com.campusdual.jardhotels.model.dto.GuestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guests")
public class GuestController {

    @Autowired
    private IGuestService guestService;

    @PostMapping(value = "/add")
    public int addGuest(@RequestBody GuestDTO guestDTO) {
        return guestService.insertGuest(guestDTO);
    }

    @DeleteMapping(value = "/delete")
    public int deleteGuest(@RequestBody GuestDTO guestDTO) {
        /*try {
            guestService.queryGuest(guestDTO);
        } catch (Exception e) {
            throw new GuestNotFound("Guest not found");
        }*/
        return guestService.deleteGuest(guestDTO);
    }

    @GetMapping(value = "/getAll")
    public List<GuestDTO> getAllGuests() {
        return guestService.queryAll();
    }

    /*@PutMapping(value = "/update")
    public GuestDTO updateGuest(@RequestBody GuestDTO guestDTO) {

        GuestDTO guestDB;

        try {
            guestDB = guestService.queryGuest(guestDTO);
        } catch (Exception e) {
            throw new GuestNotFound("Guest not found");
        }

        if (guestDTO.getName() != null)
            guestDB.setName(guestDTO.getName());

        if (guestDTO.getStars() != 0)
            guestDB.setStars(guestDTO.getStars());

        if (guestDTO.getAddress() != null)
            guestDB.setAddress(guestDTO.getAddress());

        return guestService.updateGuest(guestDB);
    }*/
}
