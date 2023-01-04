package com.luv2code.springmvc.controller;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.models.student.CollegeStudent;
import com.luv2code.springmvc.models.student.GradebookCollegeStudent;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GradebookController {

    @Autowired
    private Gradebook gradebook;

    @Autowired
    private StudentAndGradeService studentService;
    @Autowired
    private StudentDao studentDao;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getStudents(Model m) {
        Iterable<CollegeStudent> collegeStudents = studentService.getGradebook();
        m.addAttribute("students", collegeStudents);
        return "index";
    }

    @PostMapping(value = "/")
    public String createStudent(@ModelAttribute("student") CollegeStudent student, Model m) {
        studentService.createStudent(student.getFirstname(), student.getLastname(), student.getEmailAddress());
        Iterable<CollegeStudent> collegeStudents = studentService.getGradebook();
        m.addAttribute("students",collegeStudents);
        return "index";
    }

    @GetMapping("/delete/student/{id}")
    public String deleteStudent(@PathVariable int id , Model m){

        if(!studentService.checkIfStudentExistsById(id)){
            return "error";
        }

        studentService.deleteStudentById(id);
        Iterable<CollegeStudent> collegeStudents = studentService.getGradebook();
        m.addAttribute("students",collegeStudents);
        return "index";
    }


    @GetMapping("/studentInformation/{id}")
    public String studentInformation(@PathVariable int id, Model m) {

        if(!studentService.checkIfStudentExistsById(id)){
            return "error";
        }

        studentService.configureStudentInformationModel(id,m);

        return "studentInformation";
    }


    @PostMapping(value = "/grades")
    public String createGrade(@RequestParam("grade") double grade,
                              @RequestParam("gradeType") String gradeType,
                              @RequestParam("studentId") int studentId,
                              Model m){

        if(!studentService.checkIfStudentExistsById(studentId)){
            return "error";
        }

        boolean success = studentService.createGrade(grade,studentId,gradeType);

        if(!success){
            return "error";
        }

        studentService.configureStudentInformationModel(studentId,m);

        return "studentInformation";
    }

    @GetMapping("/grades/{id}/{gradeType}")
    public String deleteGrade(@PathVariable int id,
                              @PathVariable String gradeType,
                              Model m){

        int studentId = studentService.deleteGrade(id,gradeType);

        if(studentId == 0){
            return "error";
        }

        studentService.configureStudentInformationModel(studentId,m);

        return "studentInformation";
    }























}
