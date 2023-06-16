package com.campusdual.jardhotelsontimize.ws.core.rest;

import com.campusdual.jardhotelsontimize.api.core.service.IHotelService;
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
import static org.mockito.Mockito.*;

class HotelRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IHotelService hotelService;

    @InjectMocks
    private HotelRestController hotelRestController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(hotelRestController).build();
    }

    @Test
    void testFilter() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("columns", Arrays.asList("id", "name"));
        requestBody.put("filter", new HashMap<>());

        EntityResult entityResult = new EntityResultMapImpl();
        entityResult.setCode(0);
        entityResult.setMessage("");
        when(hotelService.hotelQuery(anyMap(), anyList())).thenReturn(entityResult);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/hotels/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestBody)))
                        .andReturn();

        verify(hotelService, times(1)).hotelQuery(any(), any());
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }
}
