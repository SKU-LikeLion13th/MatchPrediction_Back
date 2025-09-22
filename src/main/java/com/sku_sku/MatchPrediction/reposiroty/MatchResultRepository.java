package com.sku_sku.MatchPrediction.reposiroty;

import com.sku_sku.MatchPrediction.domain.MatchResult;
import com.sku_sku.MatchPrediction.enums.SportType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {

    Optional<MatchResult> findBySportType(SportType sportType);
}
