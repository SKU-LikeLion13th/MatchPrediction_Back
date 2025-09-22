package com.sku_sku.MatchPrediction.controller;

import com.sku_sku.MatchPrediction.dto.PredictionStatisticsBySportRes;
import com.sku_sku.MatchPrediction.dto.ReqGrade;
import com.sku_sku.MatchPrediction.dto.ReqSubmitPrediction;
import com.sku_sku.MatchPrediction.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/prediction")
public class PredictionController {

    private final PredictionService predictionService;

    @PostMapping("/submit")
    public ResponseEntity<String> submitPrediction(@RequestBody ReqSubmitPrediction reqSubmitPrediction) {
            predictionService.submitPrediction(reqSubmitPrediction);
            return ResponseEntity.status(HttpStatus.CREATED).body("응모 완료");
    }

    @PostMapping("/grade")
    public ResponseEntity<String> gradeMatch(@RequestBody ReqGrade reqGrade) {
        predictionService.gradeMatchBySport(reqGrade);
        return ResponseEntity.status(HttpStatus.OK).body("채점 완료");
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<PredictionStatisticsBySportRes>> getPredictionStatisticsBySport() {
        return ResponseEntity.status(HttpStatus.OK).body(predictionService.getPredictionStatisticsBySport());
    }
}

