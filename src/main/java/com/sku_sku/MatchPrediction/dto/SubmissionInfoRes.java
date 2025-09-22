package com.sku_sku.MatchPrediction.dto;

import com.sku_sku.MatchPrediction.enums.PredictionResult;
import com.sku_sku.MatchPrediction.enums.SportType;

import java.util.List;

public record SubmissionInfoRes(
        int remainingTickets,
        List<PredictionResWrapper> submissions
) {
    public record PredictionResWrapper(
            List<PredictionRes> predictions
    ) {}

    public record PredictionRes(
            SportType sportType,
            PredictionResult predictionResult
    ) {}
}
