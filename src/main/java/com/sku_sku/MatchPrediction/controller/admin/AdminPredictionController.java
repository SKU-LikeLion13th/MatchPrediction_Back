package com.sku_sku.MatchPrediction.controller.admin;

import com.sku_sku.MatchPrediction.dto.ReqGrade;
import com.sku_sku.MatchPrediction.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/prediction")
public class AdminPredictionController {

    private final PredictionService predictionService;

    @PostMapping("/grade")
    public ResponseEntity<String> gradeMatch(@RequestBody ReqGrade reqGrade) {
        predictionService.gradeMatchBySport(reqGrade);
        return ResponseEntity.status(HttpStatus.OK).body("채점 완료");
    }
}

