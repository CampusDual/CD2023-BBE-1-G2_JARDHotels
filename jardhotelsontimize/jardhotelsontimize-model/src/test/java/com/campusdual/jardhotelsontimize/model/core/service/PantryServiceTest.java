package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.model.core.dao.PantryDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PantryServiceTest {
    @Mock
    DefaultOntimizeDaoHelper daoHelper;

    @InjectMocks
    PantryService pantryService;

    @Mock
    HotelService hotelService;

    @Mock
    MenuService menuService;

    @Mock
    PantryDao pantryDao;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PantryServiceQuery {
        @Test
        void pantryQueryTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put("id", 1);
            Map<String, Object> pantryToQuery = new HashMap<>();
            pantryToQuery.put("id", 1);
            when(daoHelper.query(any(PantryDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = pantryService.pantryQuery(pantryToQuery, new ArrayList<>());
            assertEquals(0, result.getCode());
            assertEquals("", result.getMessage());
            verify(daoHelper, times(1)).query(any(PantryDao.class), anyMap(), anyList());
        }

        @Test
        void pantryQueryTestNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            Map<String, Object> pantryToQuery = new HashMap<>();
            pantryToQuery.put("id", 1);
            when(daoHelper.query(any(PantryDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = pantryService.pantryQuery(pantryToQuery, new ArrayList<>());
            assertEquals(1, result.getCode());
            assertEquals("Pantry not found", result.getMessage());
            verify(daoHelper, times(1)).query(any(PantryDao.class), anyMap(), anyList());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PantryServiceInsert {
        @Test
        void pantryInsertTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.setMessage("");
            er.put("id", 1);
            Map<String, Object> pantryToInsert = new HashMap<>();
            pantryToInsert.put("id", 1);
            pantryToInsert.put("idhotel", 1);
            pantryToInsert.put("idmenu", 1);
            pantryToInsert.put("amount", 1);
            when(daoHelper.insert(any(PantryDao.class), anyMap())).thenReturn(er);
            when(hotelService.hotelQuery(anyMap(), anyList())).thenReturn(er);
            when(menuService.menuQuery(anyMap(), anyList())).thenReturn(er);
            EntityResult result = pantryService.pantryInsert(pantryToInsert);
            assertEquals("Successful pantry insert", result.getMessage());
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).insert(any(PantryDao.class), anyMap());

        }

        @Test
        void patryInsertHotelTestNotFound() {
            Map<String, Object> pantryToInsert = new HashMap<>();
            pantryToInsert.put("id", 1);
            pantryToInsert.put("idhotel", 1);
            pantryToInsert.put("idmenu", 1);
            pantryToInsert.put("amount", 1);
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage("Hotel not found");
            when(hotelService.hotelQuery(anyMap(), anyList())).thenReturn(er);
            EntityResult result = pantryService.pantryInsert(pantryToInsert);
            assertEquals("Hotel not found", result.getMessage());
            assertEquals(1, result.getCode());
        }

        @Test
        void patryInsertMenuTestNotFound() {
            Map<String, Object> pantryToInsert = new HashMap<>();
            pantryToInsert.put("id", 1);
            pantryToInsert.put("idhotel", 1);
            pantryToInsert.put("idmenu", 1);
            pantryToInsert.put("amount", 1);
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage("Menu not found");
            when(hotelService.hotelQuery(anyMap(), anyList())).thenReturn(new EntityResultMapImpl());
            when(menuService.menuQuery(anyMap(), anyList())).thenReturn(er);
            EntityResult result = pantryService.pantryInsert(pantryToInsert);
            assertEquals("Menu not found", result.getMessage());
            assertEquals(1, result.getCode());
        }

        @Test
        void pantryInsertTestFail() {
            Map<String, Object> pantryToInsert = new HashMap<>();
            pantryToInsert.put("id", 1);
            pantryToInsert.put("idhotel", 1);
            pantryToInsert.put("idmenu", 1);
            pantryToInsert.put("amount", 1);
            when(hotelService.hotelQuery(anyMap(), anyList())).thenReturn(new EntityResultMapImpl());
            when(menuService.menuQuery(anyMap(), anyList())).thenReturn(new EntityResultMapImpl());
            when(daoHelper.insert(any(PantryDao.class), anyMap())).thenThrow(new RuntimeException("Error"));
            EntityResult result = pantryService.pantryInsert(pantryToInsert);
            assertEquals(1, result.getCode());
            verify(daoHelper, times(1)).insert(any(PantryDao.class), anyMap());
            assertNotEquals("Successfully pantry insert", result.getMessage());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PantryServiceUpdate {
        @Test
        void pantryUpdateTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.setMessage("");
            er.put("id", 1);
            Map<String, Object> pantryToUpdate = new HashMap<>();
            pantryToUpdate.put("id", 1);
            when(daoHelper.update(any(PantryDao.class), anyMap(), anyMap())).thenReturn(er);
            when(daoHelper.query(any(PantryDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = pantryService.pantryUpdate(pantryToUpdate, pantryToUpdate);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).update(any(PantryDao.class), anyMap(), anyMap());
            assertEquals("Successful pantry update", result.getMessage());
        }

        @Test
        void pantryUpdateTestFail() {
            EntityResult er = new EntityResultMapImpl();
            er.setMessage("");
            er.put("id", 1);
            Map<String, Object> pantryToUpdate = new HashMap<>();
            pantryToUpdate.put("id", 1);
            when(daoHelper.update(any(PantryDao.class), anyMap(), anyMap())).thenThrow(new RuntimeException("Error"));
            when(daoHelper.query(any(PantryDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = pantryService.pantryUpdate(pantryToUpdate, pantryToUpdate);
            assertEquals(1, result.getCode());
            verify(daoHelper, times(1)).update(any(PantryDao.class), anyMap(), anyMap());
            assertNotEquals("Successful pantry update", result.getMessage());
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PantryServiceDelete {
        @Test
        void pantryDeleteTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.setMessage("");
            er.put("id", 1);
            er.put("idhotel", List.of(1));
            Map<String, Object> pantryKey = new HashMap<>();
            pantryKey.put("id", 1);
            when(daoHelper.delete(any(PantryDao.class), anyMap())).thenReturn(er);
            when(daoHelper.query(any(PantryDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = pantryService.pantryDelete(pantryKey);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).delete(any(PantryDao.class), anyMap());
            assertEquals("Successful pantry delete", result.getMessage());
        }

        @Test
        void pantryDeleteTestPantryNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage("");
            Map<String, Object> pantryKey = new HashMap<>();
            pantryKey.put("id", 1);
            when(daoHelper.query(any(PantryDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = pantryService.pantryDelete(pantryKey);
            assertEquals(1, result.getCode());
            assertEquals("Pantry not found", result.getMessage());
        }
    }
}