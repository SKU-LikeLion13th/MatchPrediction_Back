package com.sku_sku.MatchPrediction.reposiroty;

import com.sku_sku.MatchPrediction.domain.PaidStudent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaidStudentRepository extends JpaRepository<PaidStudent, Long> {

    boolean existsByStudentId(String studentId);
}
