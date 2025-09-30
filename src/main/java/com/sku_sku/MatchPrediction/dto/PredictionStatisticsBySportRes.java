package com.sku_sku.MatchPrediction.dto;

import com.sku_sku.MatchPrediction.enums.PredictionResult;

import java.util.List;

public record PredictionStatisticsBySportRes(
        String sportType,
        PredictionResult gameResult,
        List<PredictionResWrapper> predictions
) {
    public record PredictionResWrapper(
            String predictionResult,
            int percentage
    ) {}
}
