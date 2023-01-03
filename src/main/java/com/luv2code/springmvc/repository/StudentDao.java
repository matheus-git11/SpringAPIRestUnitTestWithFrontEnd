package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.student.CollegeStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentDao extends JpaRepository<CollegeStudent,Integer> { /// se algo der erro trocar por CrudRepository
    CollegeStudent findByEmailAddress(String emailAddress);
}
