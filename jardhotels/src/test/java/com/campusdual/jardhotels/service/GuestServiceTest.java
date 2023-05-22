package com.campusdual.jardhotels.service;

import com.campusdual.jardhotels.model.Guest;
import com.campusdual.jardhotels.model.dao.GuestDAO;
import com.campusdual.jardhotels.model.dto.GuestDTO;
import com.campusdual.jardhotels.model.dto.dtomapper.GuestMapper;
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

public class GuestServiceTest {
    @Mock
    GuestDAO guestDAO;

    @InjectMocks
    GuestService guestService;

    @Test
    void addGuestTest() {

        // given
        Guest guest = new Guest();
        guest.setId(1);
        GuestDTO guestDTO = GuestMapper.INSTANCE.toDto(guest);

        // when
        when(guestDAO.saveAndFlush(any(Guest.class))).thenReturn(guest);
        int insertedId = guestService.insertGuest(guestDTO);

        // then
        verify(guestDAO, times(1)).saveAndFlush(any(Guest.class));
        assertEquals(guest.getId(), insertedId);
    }

    @Test
    void getGuestTest() {

        // given
        Guest guest = new Guest();
        guest.setId(1);
        GuestDTO guestDTO = GuestMapper.INSTANCE.toDto(guest);

        // when
        when(guestDAO.getReferenceById(anyInt())).thenReturn(guest);
        GuestDTO queriedGuest = guestService.queryGuest(guestDTO);

        // then
        verify(guestDAO, times(1)).getReferenceById(anyInt());
        assertEquals(guest.getId(), queriedGuest.getId());
    }
}
