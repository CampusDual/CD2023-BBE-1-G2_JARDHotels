package com.campusdual.jardhotels.controller;

import com.campusdual.jardhotels.exceptions.GuestNotFound;
import com.campusdual.jardhotels.model.Guest;
import com.campusdual.jardhotels.model.dto.GuestDTO;
import com.campusdual.jardhotels.service.GuestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class GuestControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    GuestController guestController;

    @Mock
    GuestService guestService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(guestController).build();
    }

    @Test
    void addGuestTest() throws Exception {

        // given
        Guest guest = new Guest();

        // when
        MvcResult addGuestMvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/guests/add")
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(guest))).andReturn();

        // then
        assertEquals(HttpStatus.OK.value(), addGuestMvcResult.getResponse().getStatus());
    }

    @Test
    void getAllGuestTest() throws Exception {

        // given
        List<GuestDTO> guestDTOS = List.of(new GuestDTO(), new GuestDTO());

        // when
        when(guestService.queryAll()).thenReturn(guestDTOS);
        MvcResult getAllMvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/guests/getAll")).andReturn();

        // then
        verify(guestService, times(1)).queryAll();
        assertEquals(HttpStatus.OK.value(), getAllMvcResult.getResponse().getStatus());
        assertEquals(guestDTOS.size(), new ObjectMapper().readTree(getAllMvcResult.getResponse()
                .getContentAsString()).size());

    }


    @Test
    void deleteGuestTest() throws Exception {

        // given
        GuestDTO guestDTO = new GuestDTO();
        guestDTO.setId(1);

        // when
        when(guestService.queryGuest(any(GuestDTO.class))).thenReturn(guestDTO);
        when(guestService.deleteGuest(any(GuestDTO.class))).thenReturn(1);

        MvcResult deleteMvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/guests/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(guestDTO))).andReturn();

        // then
        verify(guestService, times(1)).queryGuest(any(GuestDTO.class));
        verify(guestService, times(1)).deleteGuest(any(GuestDTO.class));
        assertEquals(HttpStatus.OK.value(), deleteMvcResult.getResponse().getStatus());
    }

    @Test
    void deleteNotExistingGuestTest() throws Exception {

        // given
        GuestDTO guestDTO = new GuestDTO();

        // when
        when(guestService.queryGuest(any(GuestDTO.class))).thenThrow(new GuestNotFound("Guest not found"));
        MvcResult deleteMvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/guests/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(guestDTO))).andReturn();

        // then
        assertEquals("Guest not found",deleteMvcResult.getResponse().getErrorMessage());
    }
}

