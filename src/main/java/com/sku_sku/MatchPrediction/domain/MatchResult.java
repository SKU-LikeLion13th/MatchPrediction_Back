package com.sku_sku.MatchPrediction.domain;

import com.sku_sku.MatchPrediction.enums.PredictionResult;
import com.sku_sku.MatchPrediction.enums.SportType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class MatchResult {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private SportType sportType;

    @Enumerated(EnumType.STRING)
    private PredictionResult gameResult = PredictionResult.BEFORE_THE_GAME;

    public MatchResult(SportType sportType) {
        this.sportType = sportType;
    }

    public void gradeMatch(PredictionResult result) {
        this.gameResult = result;
    }
}

