package com.sku_sku.MatchPrediction.controller;

import com.sku_sku.MatchPrediction.domain.Student;
import com.sku_sku.MatchPrediction.dto.PredictionStatisticsBySportRes;
import com.sku_sku.MatchPrediction.dto.ReqSubmitPrediction;
import com.sku_sku.MatchPrediction.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/prediction")
public class PredictionController {

    private final PredictionService predictionService;

    @PostMapping("/submit")
    public ResponseEntity<String> submitPrediction(@AuthenticationPrincipal Student student, @RequestBody ReqSubmitPrediction reqSubmitPrediction) {
        predictionService.submitPrediction(student, reqSubmitPrediction);
        return ResponseEntity.status(HttpStatus.CREATED).body("응모 완료");
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<PredictionStatisticsBySportRes>> getPredictionStatisticsBySport() {
        return ResponseEntity.status(HttpStatus.OK).body(predictionService.getPredictionStatisticsBySport());
    }
}

