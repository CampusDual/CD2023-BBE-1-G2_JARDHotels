package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.model.core.dao.GuestDao;
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
public class GuestServiceTest {
    @Mock
    DefaultOntimizeDaoHelper daoHelper;

    @InjectMocks
    GuestService guestService;

    @Mock
    PersonService personService;

    @Mock
    StaffService staffService;

    @Mock
    GuestDao guestDao;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GuestServiceQuery {
        @Test
        void guestQueryTest() {
            EntityResult er = new EntityResultMapImpl();
            EntityResult er2 = new EntityResultMapImpl();
            List<Integer> l2 = new ArrayList<>();
            l2.add(1);
            er.setCode(0);
            er.put("id", l2);
            er2.put("id", l2);
            Map<String, Object> guestToQuery = new HashMap<>();
            guestToQuery.put("id", 1);
            List<String> l = new ArrayList();
            l.add("id");
            when(daoHelper.query(any(GuestDao.class), anyMap(), anyList())).thenReturn(er);
            when(personService.personQuery(anyMap(), anyList())).thenReturn(er2);
            EntityResult result = guestService.guestQuery(guestToQuery, l);
            assertEquals(0, result.getCode());
            assertEquals("", result.getMessage());
            verify(daoHelper, times(1)).query(any(GuestDao.class), anyMap(), anyList());
        }

        @Test
        void guestQueryTestNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            Map<String, Object> guestToQuery = new HashMap<>();
            guestToQuery.put("id", 1);
            when(daoHelper.query(any(GuestDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = guestService.guestQuery(guestToQuery, new ArrayList<>());
            assertEquals(1, result.getCode());
            assertEquals("The guest doesn't exist", result.getMessage());
            verify(daoHelper, times(1)).query(any(GuestDao.class), anyMap(), anyList());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GuestServiceInsert {
        @Test
        void guestInsertTest() {
            EntityResult er = new EntityResultMapImpl();
            EntityResult er2 = new EntityResultMapImpl();
            EntityResult er3 = new EntityResultMapImpl();

            List<Integer> l2 = new ArrayList<>();
            l2.add(1);

            er.setCode(0);
            er2.setCode(0);
            er3.setCode(1);
            er.setMessage("");
            er.put("id", 1);
            er2.put("id", l2);

            Map<String, Object> guestToInsert = new HashMap<>();
            guestToInsert.put("id", 1);

            when(daoHelper.insert(any(GuestDao.class), anyMap())).thenReturn(er);
            when(personService.personQuery(anyMap(), anyList())).thenReturn(er2);
            when(daoHelper.query(any(GuestDao.class), anyMap(), anyList())).thenReturn(er3);

            EntityResult result = guestService.guestInsert(guestToInsert);
            assertEquals("Successful guest insert", result.getMessage());
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).insert(any(GuestDao.class), anyMap());

        }

        @Test
        void guestInsertTestPersonNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.put("id", 1);

            Map<String, Object> guestToInsert = new HashMap<>();
            guestToInsert.put("id", 1);

            when(personService.personQuery(anyMap(), anyList())).thenReturn(er);

            EntityResult result = guestService.guestInsert(guestToInsert);

            assertEquals("Person not found", result.getMessage());
            assertEquals(1, result.getCode());
        }

        @Test
        void guestInsertTestPersonRepeatedGuest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            List<Integer> l2 = new ArrayList<>();
            l2.add(1);
            Map<String, Object> guestToInsert = new HashMap<>();
            guestToInsert.put("id", 1);
            er.put("id", l2);

            when(personService.personQuery(anyMap(), anyList())).thenReturn(er);
            when(daoHelper.query(any(GuestDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult result = guestService.guestInsert(guestToInsert);

            assertEquals("Repeated Guest", result.getMessage());
            assertEquals(1, result.getCode());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GuestServiceUpdate {
        @Test
        void guestUpdateTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            List<Integer> l2 = new ArrayList<>();
            l2.add(1);
            er.put("id", l2);

            Map<String, Object> guestToUpdate = new HashMap<>();
            guestToUpdate.put("id", 1);

            Map<String, Object> guestKey = new HashMap<>();
            guestKey.put("id", 1);

            when(daoHelper.query(any(GuestDao.class), anyMap(), anyList())).thenReturn(er);
            when(personService.personUpdate(anyMap(), any())).thenReturn(er);
            when(personService.personQuery(anyMap(), anyList())).thenReturn(er);

            EntityResult result = guestService.guestUpdate(guestToUpdate, guestKey);
            assertEquals(0, result.getCode());
            assertEquals("Successful guest update", result.getMessage());
        }

        @Test
        void guestUpdateTestGuestNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);

            Map<String, Object> guestToUpdate = new HashMap<>();
            guestToUpdate.put("id", 1);

            Map<String, Object> guestKey = new HashMap<>();
            guestKey.put("id", 1);

            when(daoHelper.query(any(GuestDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult result = guestService.guestUpdate(guestToUpdate, guestKey);
            assertEquals(1, result.getCode());
            assertEquals("Guest not found", result.getMessage());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GuestServiceDelete {
        @Test
        void guestDeleteTestExistingInStaff() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put("id", 1);

            Map<String, Object> guestKey = new HashMap<>();
            guestKey.put("id", 1);

            when(daoHelper.query(any(GuestDao.class), anyMap(), anyList())).thenReturn(er);
            when(staffService.staffQuery(anyMap(), anyList())).thenReturn(er);
            when(daoHelper.delete(any(GuestDao.class), anyMap())).thenReturn(er);

            EntityResult result = guestService.guestDelete(guestKey);

            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).delete(any(GuestDao.class), anyMap());
            assertEquals("Successful guest delete", result.getMessage());
        }

        @Test
        void guestDeleteTestNotExistingInStaff() {
            EntityResult er = new EntityResultMapImpl();
            EntityResult er2 = new EntityResultMapImpl();
            er2.setCode(1);
            er.setCode(0);
            er.put("id", 1);

            Map<String, Object> guestKey = new HashMap<>();
            guestKey.put("id", 1);

            when(daoHelper.query(any(GuestDao.class), anyMap(), anyList())).thenReturn(er);
            when(staffService.staffQuery(anyMap(), anyList())).thenReturn(er2);
            when(personService.personDelete(anyMap())).thenReturn(er);

            EntityResult result = guestService.guestDelete(guestKey);

            assertEquals(0, result.getCode());
            assertEquals("Successful guest delete", result.getMessage());
        }

        @Test
        void guestDeleteTestGuestNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);

            Map<String, Object> guestKey = new HashMap<>();
            guestKey.put("id", 1);

            when(daoHelper.query(any(GuestDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult result = guestService.guestDelete(guestKey);

            assertEquals(1, result.getCode());
            assertEquals("Guest not found", result.getMessage());
        }
    }
}
