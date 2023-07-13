package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.model.core.dao.MenuDao;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {
    @Mock
    DefaultOntimizeDaoHelper daoHelper;

    @InjectMocks
    MenuService menuService;

    @Mock
    MenuDao menuDao;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class MenuServiceQuery {
        @Test
        void menuQueryTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put("id", 1);
            Map<String, Object> menuToQuery = new HashMap<>();
            menuToQuery.put("id", 1);
            when(daoHelper.query(any(MenuDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = menuService.menuQuery(menuToQuery, new ArrayList<>());
            assertEquals(0, result.getCode());
            assertEquals("", result.getMessage());
            verify(daoHelper, times(1)).query(any(MenuDao.class), anyMap(), anyList());
        }

        @Test
        void menuQueryTestNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            Map<String, Object> menuToQuery = new HashMap<>();
            menuToQuery.put("id", 1);
            when(daoHelper.query(any(MenuDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = menuService.menuQuery(menuToQuery, new ArrayList<>());
            assertEquals(1, result.getCode());
            assertEquals("Menu not found", result.getMessage());
            verify(daoHelper, times(1)).query(any(MenuDao.class), anyMap(), anyList());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class MenuServiceInsert {
        @Test
        void menuInsertTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.setMessage("");
            er.put("id", 1);
            Map<String, Object> menuToInsert = new HashMap<>();
            menuToInsert.put("id", 1);
            when(daoHelper.insert(any(MenuDao.class), anyMap())).thenReturn(er);
            EntityResult result = menuService.menuInsert(menuToInsert);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).insert(any(MenuDao.class), anyMap());
            assertEquals("Successfully menu insert", result.getMessage());
        }

        @Test
        void menuInsertTestFail() {

            Map<String, Object> menuToInsert = new HashMap<>();
            menuToInsert.put("id", 1);
            when(daoHelper.insert(any(MenuDao.class), anyMap())).thenThrow(new RuntimeException("Error"));
            EntityResult result = menuService.menuInsert(menuToInsert);
            assertEquals(1, result.getCode());
            verify(daoHelper, times(1)).insert(any(MenuDao.class), anyMap());
            assertNotEquals("Successfully menu insert", result.getMessage());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class MenuServiceUpdate {
        @Test
        void menuUpdateTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.setMessage("");
            er.put("id", 1);
            Map<String, Object> menuToUpdate = new HashMap<>();
            menuToUpdate.put("id", 1);
            when(daoHelper.update(any(MenuDao.class), anyMap(), anyMap())).thenReturn(er);
            when(daoHelper.query(any(MenuDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = menuService.menuUpdate(menuToUpdate, menuToUpdate);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).update(any(MenuDao.class), anyMap(), anyMap());
            assertEquals("Successfully menu update", result.getMessage());
        }

        @Test
        void menuUpdateTestFail() {
            EntityResult er = new EntityResultMapImpl();
            er.setMessage("");
            er.put("id", 1);
            Map<String, Object> menuToUpdate = new HashMap<>();
            menuToUpdate.put("id", 1);
            when(daoHelper.update(any(MenuDao.class), anyMap(), anyMap())).thenThrow(new RuntimeException("Error"));
            when(daoHelper.query(any(MenuDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = menuService.menuUpdate(menuToUpdate, menuToUpdate);
            assertEquals(1, result.getCode());
            verify(daoHelper, times(1)).update(any(MenuDao.class), anyMap(), anyMap());
            assertNotEquals("Successfully menu update", result.getMessage());
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class MenuServiceDelete {
        @Test
        void menuDeleteTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.setMessage("");
            er.put("id", 1);
            Map<String, Object> menuKey = new HashMap<>();
            menuKey.put("id", 1);
            when(daoHelper.delete(any(MenuDao.class), anyMap())).thenReturn(er);
            when(daoHelper.query(any(MenuDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = menuService.menuDelete(menuKey);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).delete(any(MenuDao.class), anyMap());
            assertEquals("Successfully menu delete", result.getMessage());
        }

        @Test
        void menuDeleteTestMenuNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage("");
            Map<String, Object> menuKey = new HashMap<>();
            menuKey.put("id", 1);
            when(daoHelper.query(any(MenuDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = menuService.menuDelete(menuKey);
            assertEquals(1, result.getCode());
            assertEquals("Menu not found", result.getMessage());
        }
    }
}