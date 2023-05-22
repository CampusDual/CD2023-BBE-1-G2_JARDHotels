package com.campusdual.jardhotels.api;

import com.campusdual.jardhotels.model.dto.GuestDTO;

import java.util.List;

public interface IGuestService {
    int insertGuest(GuestDTO guestDTO);

    int deleteGuest(GuestDTO guestDTO);

    List<GuestDTO> queryAll();

    GuestDTO queryGuest(GuestDTO guestDTO);

    GuestDTO updateGuest(GuestDTO guestDTO);

}
