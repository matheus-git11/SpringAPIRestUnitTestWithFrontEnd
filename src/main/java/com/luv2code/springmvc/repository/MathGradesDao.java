package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.grade.MathGrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MathGradesDao extends JpaRepository<MathGrade,Integer> {

     public Iterable<MathGrade> findGradeByStudentId(int id);

     public void deleteByStudentId(int id);

}
