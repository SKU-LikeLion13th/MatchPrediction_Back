package com.sku_sku.MatchPrediction.reposiroty;

import com.sku_sku.MatchPrediction.domain.PredictionSubmission;
import com.sku_sku.MatchPrediction.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredictionSubmissionRepository extends JpaRepository<PredictionSubmission, Long> {
    List<PredictionSubmission> findByStudent(Student student);

    int countByStudent(Student student);
}
