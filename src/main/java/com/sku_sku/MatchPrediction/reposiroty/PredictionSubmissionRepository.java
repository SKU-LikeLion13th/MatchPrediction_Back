package com.sku_sku.MatchPrediction.reposiroty;

import com.sku_sku.MatchPrediction.domain.PredictionSubmission;
import com.sku_sku.MatchPrediction.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionSubmissionRepository extends JpaRepository<PredictionSubmission, Long> {
    int countByStudent(Student student);
}
