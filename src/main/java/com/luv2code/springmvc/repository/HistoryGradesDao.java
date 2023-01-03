package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.grade.HistoryGrade;
import com.luv2code.springmvc.models.grade.MathGrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryGradesDao extends JpaRepository<HistoryGrade,Integer> {

    public Iterable<HistoryGrade> findGradeByStudentId(int id);

    public void deleteByStudentId(int id);
}
