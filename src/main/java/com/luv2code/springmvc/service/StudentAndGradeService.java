package com.luv2code.springmvc.service;

import com.luv2code.springmvc.models.StudentGrades;
import com.luv2code.springmvc.models.grade.Grade;
import com.luv2code.springmvc.models.grade.HistoryGrade;
import com.luv2code.springmvc.models.grade.MathGrade;
import com.luv2code.springmvc.models.grade.ScienceGrade;
import com.luv2code.springmvc.models.student.CollegeStudent;
import com.luv2code.springmvc.models.student.GradebookCollegeStudent;
import com.luv2code.springmvc.repository.HistoryGradesDao;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.ScienceGradesDao;
import com.luv2code.springmvc.repository.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    private StudentDao studentDao;

    @Autowired
    @Qualifier("mathGrades")
    private MathGrade mathGrade;

    @Autowired
    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;

    @Autowired
    @Qualifier("historyGrades")
    private HistoryGrade historyGrade;

    @Autowired
    private MathGradesDao mathGradesDao;
    @Autowired
    private ScienceGradesDao scienceGradesDao;
    @Autowired
    private HistoryGradesDao historyGradesDao;

    @Autowired
    private StudentGrades studentGrades;

    public void createStudent(String firstName, String lastName, String emailAddress) {
        CollegeStudent student = new CollegeStudent(firstName, lastName, emailAddress);
        studentDao.save(student);
    }

    public boolean checkIfStudentExistsById(int id) {
        Optional<CollegeStudent> student = studentDao.findById(id);
        return student.isPresent();
    }

    public void deleteStudentById(int id) {
        if (checkIfStudentExistsById(id)) {
            studentDao.deleteById(id);
            mathGradesDao.deleteByStudentId(id);
            historyGradesDao.deleteByStudentId(id);
            scienceGradesDao.deleteByStudentId(id);

        }
    }

    public Iterable<CollegeStudent> getGradebook() {
        Iterable<CollegeStudent> collegeStudents = studentDao.findAll();
        return collegeStudents;
    }

    public boolean createGrade(double grade, int studentId, String gradeType) {
        if (!checkIfStudentExistsById(studentId)) {
            return false;
        }

        if (grade >= 0 && grade <= 100) {
            switch (gradeType) {
                case "math" -> {
                    mathGrade.setGrade(grade);
                    mathGrade.setStudentId(studentId);
                    mathGradesDao.save(mathGrade);
                    return true;
                }
                case "science" -> {
                    scienceGrade.setGrade(grade);
                    scienceGrade.setStudentId(studentId);
                    scienceGradesDao.save(scienceGrade);
                    return true;
                }
                case "history" -> {
                    historyGrade.setGrade(grade);
                    historyGrade.setStudentId(studentId);
                    historyGradesDao.save(historyGrade);
                    return true;
                }
            }
        }
        return false;

    }


    public int deleteGrade(int gradeId, String gradeType) {
        int studentId = 0;
        switch (gradeType) {

            case "math" -> {
                Optional<MathGrade> grade = mathGradesDao.findById(gradeId);
                if (!grade.isPresent()) {
                    return studentId;
                }
                studentId = grade.get().getStudentId();
                mathGradesDao.deleteById(gradeId);

            }
            case "history" -> {
                Optional<HistoryGrade> grade = historyGradesDao.findById(gradeId);
                if (!grade.isPresent()) {
                    return studentId;
                }
                studentId = grade.get().getStudentId();
                historyGradesDao.deleteById(gradeId);

            }
            case "science" -> {
                Optional<ScienceGrade> grade = scienceGradesDao.findById(gradeId);
                if (!grade.isPresent()) {
                    return studentId;
                }
                studentId = grade.get().getStudentId();
                scienceGradesDao.deleteById(gradeId);

            }
        }
        return studentId;
    }

    public GradebookCollegeStudent studentInformation(int studentId) {

        Optional<CollegeStudent> student = studentDao.findById(studentId);
        if (student.isPresent()) {
            Iterable<MathGrade> mathGrades = mathGradesDao.findGradeByStudentId(studentId);
            Iterable<ScienceGrade> scienceGrades = scienceGradesDao.findGradeByStudentId(studentId);
            Iterable<HistoryGrade> historyGrades = historyGradesDao.findGradeByStudentId(studentId);

            List<Grade> mathGradeList = new ArrayList<>();
            mathGrades.forEach(mathGradeList::add);

            List<Grade> scienceGradeList = new ArrayList<>();
            scienceGrades.forEach(scienceGradeList::add);

            List<Grade> historyGradeList = new ArrayList<>();
            historyGrades.forEach(historyGradeList::add);

            studentGrades.setMathGradeResults(mathGradeList);
            studentGrades.setScienceGradeResults(scienceGradeList);
            studentGrades.setHistoryGradeResults(historyGradeList);

            return new GradebookCollegeStudent(
                    student.get().getId(),
                    student.get().getFirstname(),
                    student.get().getLastname(),
                    student.get().getEmailAddress(),
                    studentGrades);

        } else {
            return null;
        }
    }

}
