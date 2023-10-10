package com.cst438.controller;

import java.util.List;
import java.util.Optional;

import com.cst438.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.service.GradebookService;
@RestController
@CrossOrigin
public class StudentController {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    GradebookService gradebookService;

    @GetMapping("/student")
    public StudentDTO[] getStudents(){
        List<Student> list = (List<Student>) studentRepository.findAll();
        StudentDTO[] arr = new StudentDTO[list.size()];
        for(int i = 0; i < list.size(); i++){
            Student s = list.get(i);
            StudentDTO dto = new StudentDTO(s.getStudent_id(), s.getName(), s.getStatusCode(), s.getEmail(), s.getStatus());
            arr[i] = dto;
        }
        return arr;
    }

    @PostMapping("/student")
    public int createStudent(@RequestBody StudentDTO sdto){
        Student s = new Student();
        s.setName(sdto.studentName());
        s.setEmail(sdto.email());
        s.setStatus(sdto.status());
        s.setStatusCode(sdto.statusCode());
        studentRepository.save(s);
        return s.getStudent_id();
    }


    @GetMapping("/student/{id}")
    public StudentDTO getStudent(@PathVariable("id") int id){
        Optional<Student> s = studentRepository.findById(id);
        if(s.isPresent()){
            return new StudentDTO(s.get().getStudent_id(), s.get().getName(), s.get().getStatusCode(), s.get().getEmail(), s.get().getStatus());
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }

    }

    @DeleteMapping("/student/{id}")
    public void deleteStudent(@PathVariable("id") int id, @RequestParam("force") Optional<String> force){
        List<Enrollment> enrollmentList = enrollmentRepository.getAllEnrollmentsForStudent(id);
        if(!enrollmentList.isEmpty()){
            if(force.isEmpty() || (force.isPresent()) && force.get().equalsIgnoreCase("false")){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use force to delete");
            }else if(force.isPresent() && force.get().equalsIgnoreCase("true")){
                studentRepository.deleteById(id);
            }
        }else{
            studentRepository.deleteById(id);
        }
    }
    @PutMapping("/student/{id}")
    public void updateStudent(@PathVariable("id")int id,
                              @RequestBody StudentDTO sdto){
        Optional<Student> s = studentRepository.findById(id);
        if(s.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }else{
            Student student = s.get();
            student.setName(sdto.studentName());
            student.setEmail(sdto.email());
            student.setStatus(sdto.status());
            student.setStatusCode(sdto.statusCode());
            studentRepository.save(student);
        }

    }

}
