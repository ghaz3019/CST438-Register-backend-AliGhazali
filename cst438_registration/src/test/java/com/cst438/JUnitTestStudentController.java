package com.cst438;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cst438.domain.ScheduleDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
public class JUnitTestStudentController {
    @Autowired
    private MockMvc m;

    @Transactional
    @Test
    public void listStudentsTest() throws Exception{
        MockHttpServletResponse resp;
        resp = m.perform(MockMvcRequestBuilders.get("/student")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertEquals(200, resp.getStatus());

        StudentDTO[] list = fromJsonString(resp.getContentAsString(), StudentDTO[].class);
        assertEquals(3, list.length);

    }

    @Transactional
    @Test
    public void updateStudentTest() throws Exception{
        MockHttpServletResponse resp;
        String req = "{\"id\": 2,\"studentName\": \"Ali Ghazali\",\"statusCode\": 16,\"email\": \"mghazali@csumb.edu\",\"status\": \"Enrolled\"}";
        resp = m.perform(MockMvcRequestBuilders.get("/student").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        StudentDTO[] list = fromJsonString(resp.getContentAsString(), StudentDTO[].class);
        StudentDTO dto = null;
        for(StudentDTO s : list){
            if(s.id() == 2){
                dto = s;
                break;
            }
        }
        System.out.println(dto);
        // checking initial values
        assertEquals(200, resp.getStatus());
        assertEquals(2, dto.id());
        assertEquals("david", dto.studentName());
        assertEquals("dwisneski@csumb.edu", dto.email());

        //updating with new values
        resp = m.perform(MockMvcRequestBuilders.put("/student/"+dto.id())
                .contentType(MediaType.APPLICATION_JSON).content(req)).andReturn().getResponse();

        assertEquals(200, resp.getStatus());

        //getting new values
        resp = m.perform(MockMvcRequestBuilders.get("/student").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        list = fromJsonString(resp.getContentAsString(), StudentDTO[].class);
        dto = null;

        for(StudentDTO s : list){
            if(s.id() == 2){
                dto = s;
                break;
            }
        }
        System.out.println(dto);
        //checking if values got updated
        assertEquals(200, resp.getStatus());
        assertEquals(2, dto.id());
        assertEquals("Ali Ghazali", dto.studentName());
        assertEquals("mghazali@csumb.edu", dto.email());
    }

    @Transactional
    @Test
    public void addStudentTest() throws Exception{
        String req = "{\"id\": 1,\"studentName\": \"Ali Ghazali\",\"statusCode\": 16,\"email\": \"mghazali@csumb.edu\",\"status\": \"Enrolled\"}";



        MockHttpServletResponse resp;
        resp = m.perform(MockMvcRequestBuilders.post("/student")
                .contentType(MediaType.APPLICATION_JSON).content(req)).andReturn().getResponse();

        assertEquals(200, resp.getStatus());

        resp = m.perform(MockMvcRequestBuilders.get("/student")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        StudentDTO[] arr = fromJsonString(resp.getContentAsString(), StudentDTO[].class);

        boolean flag = false;

        for(StudentDTO dto : arr){
            if(dto.studentName().equals("Ali Ghazali")){
                flag = true;
            }
        }

        assertTrue(flag);
    }

    @Transactional
    @Test
    public void deleteStudentTest() throws Exception{
        String req = "{\"id\": 1,\"studentName\": \"Ali Ghazali\",\"statusCode\": 16,\"email\": \"mghazali@csumb.edu\",\"status\": \"Enrolled\"}";

        MockHttpServletResponse resp;
        resp = m.perform(MockMvcRequestBuilders.delete("/student/1")).andReturn().getResponse();
        assertEquals(400, resp.getStatus()); // bad request because student has enrollments

        resp = m.perform(MockMvcRequestBuilders.post("/student").contentType(MediaType.APPLICATION_JSON)
                .content(req)).andReturn().getResponse(); //inserting student without enrollments
        assertEquals(200, resp.getStatus()); // checking if student was added
        int id = Integer.parseInt(resp.getContentAsString());
        resp = m.perform(MockMvcRequestBuilders.delete("/student/"+id)).andReturn().getResponse();
        assertEquals(200, resp.getStatus()); //checking if new added student was deleted

        resp = m.perform(MockMvcRequestBuilders.delete("/student/1?force=true")).andReturn().getResponse(); //deleting using FORCE
        assertEquals(200,resp.getStatus()); //checking if student with enrollments is deleted using force

    }


    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T  fromJsonString(String str, Class<T> valueType ) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
