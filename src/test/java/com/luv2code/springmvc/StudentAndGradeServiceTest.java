package com.luv2code.springmvc;

import com.luv2code.springmvc.models.grade.HistoryGrade;
import com.luv2code.springmvc.models.grade.MathGrade;
import com.luv2code.springmvc.models.grade.ScienceGrade;
import com.luv2code.springmvc.models.student.CollegeStudent;
import com.luv2code.springmvc.models.student.GradebookCollegeStudent;
import com.luv2code.springmvc.repository.HistoryGradesDao;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.ScienceGradesDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest
class StudentAndGradeServiceTest {

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradesDao mathGradeDao;

    @Autowired
    private ScienceGradesDao scienceGradeDao;

    @Autowired
    private HistoryGradesDao historyGradeDao;

    @Autowired
    private JdbcTemplate jdbc;

    @Value("${sql.scripts.create.student}")
    private String sqlAddStudent;

    @Value("${sql.scripts.create.math.grade}")
    private String sqlAddMathGrade;

    @Value("${sql.scripts.create.science.grade}")
    private String sqlAddScienceGrade;

    @Value("${sql.scripts.create.history.grade}")
    private String sqlAddHistoryGrade;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.script.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.script.delete.science.grade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.script.delete.history.grade}")
    private String sqlDeleteHistoryGrade;

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddScienceGrade);
        jdbc.execute(sqlAddHistoryGrade);
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }

    @Test
    @DisplayName("Create Student and Check Email And Id")
    public void createStudentService() {
        CollegeStudent student = studentDao.findByEmailAddress("Erick@hotmail.com");
        assertEquals("Erick@hotmail.com", student.getEmailAddress(), "find By email");
    }

    @Test
    @DisplayName("Check if Student Exists By Id")
    public void isStudentNullCheck() {
        assertTrue(studentService.checkIfStudentExistsById(1));
        assertFalse(studentService.checkIfStudentExistsById(0));
    }

    @Test
    @DisplayName("Delete Student and student Grade Test")
    public void deleteStudentService() {

        Optional<CollegeStudent> deletedCollegeStudent = studentDao.findById(1);
        Optional<MathGrade> deletedMathGrade = mathGradeDao.findById(1);
        Optional<HistoryGrade> deletedHistoryGrade = historyGradeDao.findById(1);
        Optional<ScienceGrade> deletedScienceGrade = scienceGradeDao.findById(1);


        assertTrue(deletedCollegeStudent.isPresent(), "Should return True");
        assertTrue(deletedMathGrade.isPresent());
        assertTrue(deletedHistoryGrade.isPresent());
        assertTrue(deletedScienceGrade.isPresent());

        studentService.deleteStudentById(1);


        deletedCollegeStudent = studentDao.findById(1);
        deletedMathGrade = mathGradeDao.findById(1);
        deletedScienceGrade = scienceGradeDao.findById(1);
        deletedHistoryGrade = historyGradeDao.findById(1);

        assertFalse(deletedCollegeStudent.isPresent(), "Should return False");
        assertFalse(deletedMathGrade.isPresent(), "Should return False");
        assertFalse(deletedScienceGrade.isPresent(), "Should return False");
        assertFalse(deletedHistoryGrade.isPresent(), "Should return False");


    }

    @Test
    @Sql("/insertData.sql")
    @DisplayName("Check How Much Students have on Database")
    public void getGradebookService() {
        Iterable<CollegeStudent> iterableCollegeStudents = studentService.getGradebook();

        List<CollegeStudent> collegeStudents = new ArrayList<>();

        for (CollegeStudent collegeStudent : iterableCollegeStudents) {
            collegeStudents.add(collegeStudent);
        }

        assertEquals(5, collegeStudents.size());
    }

    @Test
    @DisplayName("Create Grades and verify exists test")
    public void createMathGradeService() {
        //create the grade
        assertTrue(studentService.createGrade(80.50, 1, "math"));
        assertTrue(studentService.createGrade(80.50, 1, "science"));
        assertTrue(studentService.createGrade(80.50, 1, "history"));
        // get all grades with student id
        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(1);
        //verify there is grades

        assertTrue(((Collection<MathGrade>) mathGrades).size() == 2
                , "student has math grades");

        assertTrue(((Collection<ScienceGrade>) scienceGrades).size() == 2
                , "student has math grades");

        assertTrue(((Collection<HistoryGrade>) historyGrades).size() == 2
                , "student has math grades");


    }

    @Test
    @DisplayName("Testing failed return on create a Grade")
    public void createGradeServiceReturnFalse() {
        assertFalse(studentService.createGrade(105, 1, "math")); // greater than 100
        assertFalse(studentService.createGrade(-5, 1, "math")); // lower than 0
        assertFalse(studentService.createGrade(80.5, 2, "math")); //null student id
        assertFalse(studentService.createGrade(80.5, 1, "Literature")); // wrong grade
    }

    @Test
    @DisplayName("Testing Delete Grade and check student ID ")
    public void deleteGradeService() {
        assertEquals(1, studentService.deleteGrade(1, "math"),
                "Returns student id after delete");

        assertEquals(1, studentService.deleteGrade(1, "history"),
                "Returns student id after delete");

        assertEquals(1, studentService.deleteGrade(1, "science"),
                "Returns student id after delete");

    }

    @Test
    @DisplayName("Testing error treatment on deleting grade")
    public void deleteGradeServiceReturnStudentIdOfZero() {
        assertEquals(0, studentService.deleteGrade(0, "science")); // should Pass because we don't have 0 id grade
        assertEquals(0, studentService.deleteGrade(1, "literature")); // should Pass because we don't have literature grade
    }

    @Test
    @DisplayName("Testing student Information")
    public void studentInformation() {
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(1);

        assertNotNull(gradebookCollegeStudent);
        assertEquals(1, gradebookCollegeStudent.getId());
        assertEquals("Erick", gradebookCollegeStudent.getFirstname());
        assertEquals("Roby", gradebookCollegeStudent.getLastname());
        assertEquals("Erick@hotmail.com", gradebookCollegeStudent.getEmailAddress());
        assertTrue(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size() == 1);

    }

    @Test
    @DisplayName("student docent exist error Test")
    public void studentInformationServiceReturnNull() {
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(0);
        assertNull(gradebookCollegeStudent);
    }


}
