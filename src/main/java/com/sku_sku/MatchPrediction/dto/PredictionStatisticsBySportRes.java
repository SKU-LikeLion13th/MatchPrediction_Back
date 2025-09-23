package com.sku_sku.MatchPrediction.dto;

import com.sku_sku.MatchPrediction.enums.PredictionResult;

import java.util.List;

public record PredictionStatisticsBySportRes(
        String sportType,
        PredictionResult gameResult,
        List<PredictionResWrapper> predictions
) {
    // 종목별 통계 안의 개별 예측
    public record PredictionResWrapper(
            String predictionResult,
            int percentage
    ) {}
}
