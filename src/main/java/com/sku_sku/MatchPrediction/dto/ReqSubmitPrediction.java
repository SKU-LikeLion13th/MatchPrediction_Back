package com.sku_sku.MatchPrediction.dto;

import com.sku_sku.MatchPrediction.enums.PredictionResult;
import com.sku_sku.MatchPrediction.enums.SportType;

import java.util.List;

public record ReqSubmitPrediction(Long id, List<SportType> sportTypeList, List<PredictionResult> predictionResultList) {
}
