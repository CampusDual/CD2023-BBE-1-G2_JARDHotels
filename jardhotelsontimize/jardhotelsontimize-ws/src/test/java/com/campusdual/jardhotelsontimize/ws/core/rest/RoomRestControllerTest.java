package com.campusdual.jardhotelsontimize.ws.core.rest;

import com.campusdual.jardhotelsontimize.api.core.service.IRoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RoomRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IRoomService roomService;

    @InjectMocks
    private RoomRestController roomRestController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(roomRestController).build();
    }

    @Test
    public void testFilter() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("columns", Arrays.asList("id", "name"));
        requestBody.put("filter", new HashMap<>());

        EntityResult entityResult = new EntityResultMapImpl();
        entityResult.setCode(0);
        entityResult.setMessage("");
        when(roomService.roomQuery(anyMap(), anyList())).thenReturn(entityResult);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rooms/filter").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(requestBody))).andReturn();

        verify(roomService, times(1)).roomQuery(any(), any());
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }
}
