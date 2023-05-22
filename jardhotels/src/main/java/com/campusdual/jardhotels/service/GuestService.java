package com.campusdual.jardhotels.service;

import com.campusdual.jardhotels.api.IGuestService;
import com.campusdual.jardhotels.model.Guest;
import com.campusdual.jardhotels.model.dao.GuestDAO;
import com.campusdual.jardhotels.model.dto.GuestDTO;
import com.campusdual.jardhotels.model.dto.dtomapper.GuestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("GuestService")
@Lazy
public class GuestService implements IGuestService {

    @Autowired
    private GuestDAO guestDAO;

    @Override
    public int insertGuest(GuestDTO guestDTO) {
        Guest guest = GuestMapper.INSTANCE.toEntity(guestDTO);
        guestDAO.saveAndFlush(guest);
        return guest.getId();
    }

    @Override
    public int deleteGuest(GuestDTO guestDTO) {
        Guest guest = GuestMapper.INSTANCE.toEntity(guestDTO);
        guestDAO.delete(guest);
        return guestDTO.getId();
    }

    @Override
    public List<GuestDTO> queryAll() {
        return GuestMapper.INSTANCE.toDtoList(guestDAO.findAll());
    }

    @Override
    public GuestDTO queryGuest(GuestDTO guestDTO) {
        Guest guest = GuestMapper.INSTANCE.toEntity(guestDTO);
        return GuestMapper.INSTANCE.toDto(guestDAO.getReferenceById(guest.getId()));
    }

    @Override
    public GuestDTO updateGuest(GuestDTO guestDTO) {

        Guest guest = GuestMapper.INSTANCE.toEntity(guestDTO);
        guestDAO.saveAndFlush(guest);
        return guestDTO;
    }

}
