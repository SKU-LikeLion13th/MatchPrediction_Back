package com.sku_sku.MatchPrediction.reposiroty;


import com.sku_sku.MatchPrediction.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentReposiroty extends JpaRepository<Student, Long> {

    Student findByEmail(String email);
}