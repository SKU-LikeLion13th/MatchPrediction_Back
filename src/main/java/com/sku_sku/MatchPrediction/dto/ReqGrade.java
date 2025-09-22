package com.sku_sku.MatchPrediction.dto;

import com.sku_sku.MatchPrediction.enums.PredictionResult;
import com.sku_sku.MatchPrediction.enums.SportType;

public record ReqGrade(SportType sportType, PredictionResult predictionResult) {
}
