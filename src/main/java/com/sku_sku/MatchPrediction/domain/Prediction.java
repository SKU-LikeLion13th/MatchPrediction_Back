package com.sku_sku.MatchPrediction.domain;

import com.sku_sku.MatchPrediction.enums.PredictionResult;
import com.sku_sku.MatchPrediction.enums.SportType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Prediction {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PredictionSubmission predictionSubmission;

    @Enumerated(EnumType.STRING)
    private SportType sportType;

    @Enumerated(EnumType.STRING)
    private PredictionResult predictionResult;

    private Boolean correct;

    public Prediction(SportType sportType, PredictionResult predictionResult) {
        this.sportType = sportType;
        this.predictionResult = predictionResult;
    }

    void setPredictionSubmission(PredictionSubmission submission) {
        this.predictionSubmission = submission;
    }

    public void checkCorrect(PredictionResult predictionResult) {
        this.correct = (this.predictionResult == predictionResult);
    }
}
