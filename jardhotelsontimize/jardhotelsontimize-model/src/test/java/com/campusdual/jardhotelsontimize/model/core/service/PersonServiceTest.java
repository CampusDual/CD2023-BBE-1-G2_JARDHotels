package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.model.core.dao.PersonDao;
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
public class PersonServiceTest {
    @Mock
    DefaultOntimizeDaoHelper daoHelper;

    @InjectMocks
    PersonService personService;

    @Mock
    PersonDao personDao;

    @Mock
    UserService userService;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PersonServiceQuery {
        @Test
        void personQueryTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put("id", 1);
            Map<String, Object> personToQuery = new HashMap<>();
            personToQuery.put("id", 1);
            when(daoHelper.query(any(PersonDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = personService.personQuery(personToQuery, new ArrayList<>());
            assertEquals(0, result.getCode());
            assertEquals("", result.getMessage());
            verify(daoHelper, times(1)).query(any(PersonDao.class), anyMap(), anyList());
        }

        @Test
        void personQueryTestNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            Map<String, Object> personToQuery = new HashMap<>();
            personToQuery.put("id", 1);
            when(daoHelper.query(any(PersonDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = personService.personQuery(personToQuery, new ArrayList<>());
            assertEquals(1, result.getCode());
            assertEquals("The person doesn't exist", result.getMessage());
            verify(daoHelper, times(1)).query(any(PersonDao.class), anyMap(), anyList());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PersonServiceInsert {
        @Test
        void personInsertTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.setMessage("");
            er.put("id", List.of(1));
            Map<String, Object> personToInsert = new HashMap<>();
            personToInsert.put("id", 1);
            personToInsert.put("username", "");
            personToInsert.put("email", "aaaaa@aaa.aa");
            personToInsert.put("password", "1234");
            when(daoHelper.insert(any(PersonDao.class), anyMap())).thenReturn(er);
            when(daoHelper.query(any(PersonDao.class), anyMap(), anyList())).thenReturn(er);
            when(userService.userInsert(anyMap())).thenReturn(er);

            EntityResult result = personService.personInsert(personToInsert);
            assertEquals("Successful person insertion", result.getMessage());
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).insert(any(PersonDao.class), anyMap());

        }

        @Test
        void personInsertTestFail() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.put("id", 1);

            Map<String, Object> personToInsert = new HashMap<>();
            personToInsert.put("id", 1);
            personToInsert.put("username", "");
            personToInsert.put("email", "aaaaa@aaa.aa");
            personToInsert.put("password", "1234");

            when(daoHelper.insert(any(PersonDao.class), anyMap())).thenThrow(new RuntimeException(""));
            EntityResult result = personService.personInsert(personToInsert);

            assertNotEquals("Successful person insertion", result.getMessage());
            assertEquals(1, result.getCode());
            verify(daoHelper, times(1)).insert(any(PersonDao.class), anyMap());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PersonServiceUpdate {
        @Test
        void personUpdateTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put("id", 1);
            Map<String, Object> personToUpdate = new HashMap<>();
            personToUpdate.put("id", 1);
            Map<String, Object> personKey = new HashMap<>();
            personKey.put("id", 1);
            when(daoHelper.update(any(PersonDao.class), anyMap(), anyMap())).thenReturn(er);
            when(daoHelper.query(any(PersonDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = personService.personUpdate(personToUpdate, personKey);
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).update(any(PersonDao.class), anyMap(), anyMap());
            assertEquals("Successful person update", result.getMessage());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PersonServiceDelete {
        @Test
        void personDeleteTest() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.setMessage("");
            er.put("id", List.of(1));
            Map<String, Object> personKey = new HashMap<>();
            personKey.put("id", 1);
            when(daoHelper.delete(any(PersonDao.class), anyMap())).thenReturn(er);
            when(daoHelper.query(any(PersonDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = personService.personDelete(personKey);

            assertEquals("Successful person delete", result.getMessage());
            assertEquals(0, result.getCode());
            verify(daoHelper, times(1)).delete(any(PersonDao.class), anyMap());
        }

        @Test
        void personDeleteTestPersonNotFound() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage("");
            Map<String, Object> personKey = new HashMap<>();
            personKey.put("id", 1);
            when(daoHelper.query(any(PersonDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = personService.personDelete(personKey);

            assertEquals("Person not found", result.getMessage());
            assertEquals(1, result.getCode());
        }
    }


}
