package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.grade.ScienceGrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScienceGradesDao extends JpaRepository<ScienceGrade,Integer> {

   public Iterable<ScienceGrade> findGradeByStudentId(int id);

   public void deleteByStudentId(int id);
}
