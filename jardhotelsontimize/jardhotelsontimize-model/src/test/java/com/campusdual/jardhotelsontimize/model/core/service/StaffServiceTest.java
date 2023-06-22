package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.model.core.dao.StaffDao;
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
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class StaffServiceTest {
    @Mock
    DefaultOntimizeDaoHelper daoHelper;

    @InjectMocks
    StaffService staffService;

    @Mock
    PersonService personService;

    @Mock
    GuestService guestService;

    @Mock
    BankAccountService bankAccountService;

    @Mock
    JobService jobService;
    @Mock
    HotelService hotelService;

    @Mock
    StaffDao staffDao;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class StaffServiceQuery {
        @Test
        void staffQueryTest() {
            EntityResult er = new EntityResultMapImpl();
            EntityResult er2 = new EntityResultMapImpl();
            List<Integer> l2 = new ArrayList<>();
            l2.add(1);
            er.setCode(0);
            er.put("id", l2);
            er2.put("id", l2);
            Map<String, Object> staffToQuery = new HashMap<>();
            staffToQuery.put("id", 1);
            List<String> l = new ArrayList();
            l.add("id");
            when(daoHelper.query(any(StaffDao.class), anyMap(), anyList())).thenReturn(er);
            when(personService.personQuery(anyMap(), anyList())).thenReturn(er2);
            EntityResult result = staffService.staffQuery(staffToQuery, l);
            assertEquals(0, result.getCode());
            assertEquals("", result.getMessage());
            verify(daoHelper, times(1)).query(any(StaffDao.class), anyMap(), anyList());
        }

        @Test
        void staffQueryTestNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            Map<String, Object> staffToQuery = new HashMap<>();
            staffToQuery.put("id", 1);
            when(daoHelper.query(any(StaffDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = staffService.staffQuery(staffToQuery, new ArrayList<>());
            assertEquals(1, result.getCode());
            assertEquals("The staff member doesn't exist", result.getMessage());
            verify(daoHelper, times(1)).query(any(StaffDao.class), anyMap(), anyList());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class StaffServiceInsert {
        @Test
        void staffInsertTest() {
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

            Map<String, Object> staffToInsert = new HashMap<>();
            staffToInsert.put("id", 1);
            staffToInsert.put("bankaccount", "1");
            staffToInsert.put("bankaccountformat", 5);
            staffToInsert.put("salary", 1);
            staffToInsert.put("job", 1);
            staffToInsert.put("idhotel",1);

            when(daoHelper.insert(any(StaffDao.class), anyMap())).thenReturn(er);
            when(personService.personQuery(anyMap(), anyList())).thenReturn(er2);
            when(daoHelper.query(any(StaffDao.class), anyMap(), anyList())).thenReturn(er3);
            when(bankAccountService.bankaccountQuery(anyMap(), anyList())).thenReturn(er2);
            when(jobService.jobQuery(anyMap(), anyList())).thenReturn(er2);
            when(hotelService.hotelQuery(anyMap(), anyList())).thenReturn(er2);

            EntityResult result = staffService.staffInsert(staffToInsert);
            assertEquals("Successful staff insert", result.getMessage());
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).insert(any(StaffDao.class), anyMap());

        }

        @Test
        void staffInsertTestPersonNotFound() {
            EntityResult er2 = new EntityResultMapImpl();
            er2.setCode(1);

            Map<String, Object> staffToInsert = new HashMap<>();
            staffToInsert.put("id", 1);
            staffToInsert.put("bankaccount", "1");
            staffToInsert.put("bankaccountformat", 5);
            staffToInsert.put("salary", 1);
            staffToInsert.put("job", 1);
            staffToInsert.put("idhotel", 1);

            when(personService.personQuery(anyMap(), anyList())).thenReturn(er2);

            EntityResult result = staffService.staffInsert(staffToInsert);
            assertEquals("Person not found", result.getMessage());
            assertEquals(1, result.getCode());
        }

        @Test
        void staffInsertTestPersonRepeatedStaff() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            List<Integer> l = new ArrayList<>();
            l.add(1);
            er.put("id", l);

            Map<String, Object> staffToInsert = new HashMap<>();
            staffToInsert.put("id", 1);
            staffToInsert.put("bankaccount", "1");
            staffToInsert.put("bankaccountformat", 5);
            staffToInsert.put("salary", 1);
            staffToInsert.put("job", 1);
            staffToInsert.put("idhotel", 1);

            when(personService.personQuery(anyMap(), anyList())).thenReturn(er);
            when(daoHelper.query(any(StaffDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult result = staffService.staffInsert(staffToInsert);
            assertEquals("Repeated staff member", result.getMessage());
            assertEquals(1, result.getCode());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class StaffServiceUpdate {
        @Test
        void staffUpdateTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            List<String> l = new ArrayList();
            List<Integer> l2 = new ArrayList<>();
            l2.add(1);
            l.add("55454547878");
            er.put("id", l2);
            er.put("bankaccount", l);
            er.put("bankaccountformat", l2);
            er.put("salary", l2);
            er.put("job", l2);

            Map<String, Object> staffToUpdate = new HashMap<>();
            staffToUpdate.put("id", l2);
            staffToUpdate.put("bankaccount", "1");
            staffToUpdate.put("bankaccountformat", 5);
            staffToUpdate.put("salary", 1);
            staffToUpdate.put("job", 1);

            Map<String, Object> staffKey = new HashMap<>();
            staffKey.put("id", 1);

            when(daoHelper.query(any(StaffDao.class), anyMap(), anyList())).thenReturn(er);
            when(personService.personUpdate(anyMap(), any())).thenReturn(er);
            when(personService.personQuery(anyMap(), anyList())).thenReturn(er);
            when(bankAccountService.bankaccountQuery(anyMap(), anyList())).thenReturn(er);
            when(jobService.jobQuery(anyMap(), anyList())).thenReturn(er);

            EntityResult result = staffService.staffUpdate(staffToUpdate, staffKey);
            assertEquals("Successful staff update", result.getMessage());
            assertEquals(0, result.getCode());
        }

        @Test
        void staffUpdateTestStaffNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);

            Map<String, Object> staffToUpdate = new HashMap<>();
            staffToUpdate.put("id", 1);

            Map<String, Object> staffKey = new HashMap<>();
            staffKey.put("id", 1);

            when(daoHelper.query(any(StaffDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult result = staffService.staffUpdate(staffToUpdate, staffKey);
            assertEquals(1, result.getCode());
            assertEquals("Staff member not found", result.getMessage());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class StaffServiceDelete {
        @Test
        void staffDeleteTestExistingInGuest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put("id", 1);

            Map<String, Object> staffKey = new HashMap<>();
            staffKey.put("id", 1);

            when(daoHelper.query(any(StaffDao.class), anyMap(), anyList())).thenReturn(er);
            when(guestService.guestQuery(anyMap(), anyList())).thenReturn(er);
            when(daoHelper.delete(any(StaffDao.class), anyMap())).thenReturn(er);

            EntityResult result = staffService.staffDelete(staffKey);

            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).delete(any(StaffDao.class), anyMap());
            assertEquals("Successful staff delete", result.getMessage());
        }

        @Test
        void staffDeleteTestNotExistingInGuest() {
            EntityResult er = new EntityResultMapImpl();
            EntityResult er2 = new EntityResultMapImpl();
            er2.setCode(1);
            er.setCode(0);
            er.put("id", 1);

            Map<String, Object> staffKey = new HashMap<>();
            staffKey.put("id", 1);

            when(daoHelper.query(any(StaffDao.class), anyMap(), anyList())).thenReturn(er);
            when(guestService.guestQuery(anyMap(), anyList())).thenReturn(er2);
            when(personService.personDelete(anyMap())).thenReturn(er);

            EntityResult result = staffService.staffDelete(staffKey);

            assertEquals(0, result.getCode());
            assertEquals("Successful staff delete", result.getMessage());
        }

        @Test
        void staffDeleteTestStaffNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);

            Map<String, Object> staffKey = new HashMap<>();
            staffKey.put("id", 1);

            when(daoHelper.query(any(StaffDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult result = staffService.staffDelete(staffKey);

            assertEquals(1, result.getCode());
            assertEquals("Staff not found", result.getMessage());
        }
    }
}
