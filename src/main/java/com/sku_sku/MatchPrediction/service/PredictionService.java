package com.sku_sku.MatchPrediction.service;

import com.sku_sku.MatchPrediction.domain.MatchResult;
import com.sku_sku.MatchPrediction.domain.Prediction;
import com.sku_sku.MatchPrediction.domain.PredictionSubmission;
import com.sku_sku.MatchPrediction.domain.Student;
import com.sku_sku.MatchPrediction.dto.PredictionStatisticsBySportRes;
import com.sku_sku.MatchPrediction.dto.PredictionStatisticsRes;
import com.sku_sku.MatchPrediction.dto.ReqGrade;
import com.sku_sku.MatchPrediction.dto.ReqSubmitPrediction;
import com.sku_sku.MatchPrediction.enums.FeeStatus;
import com.sku_sku.MatchPrediction.enums.PredictionResult;
import com.sku_sku.MatchPrediction.enums.SportType;
import com.sku_sku.MatchPrediction.exception.InvalidSportTypeException;
import com.sku_sku.MatchPrediction.exception.InvalidStudentPKException;
import com.sku_sku.MatchPrediction.exception.LimitSubmissionException;
import com.sku_sku.MatchPrediction.reposiroty.MatchResultRepository;
import com.sku_sku.MatchPrediction.reposiroty.PredictionRepository;
import com.sku_sku.MatchPrediction.reposiroty.PredictionSubmissionRepository;
import com.sku_sku.MatchPrediction.reposiroty.StudentReposiroty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PredictionService {

    private final StudentReposiroty studentReposiroty;
    private final PredictionRepository predictionRepository;
    private final PredictionSubmissionRepository predictionSubmissionRepository;
    private final MatchResultRepository matchResultRepository;

    @Transactional
    public PredictionSubmission submitPrediction(ReqSubmitPrediction reqSubmitPrediction) {
        Student student = studentReposiroty.findById(reqSubmitPrediction.id())
                .orElseThrow(InvalidStudentPKException::new);

        int limit = (student.getFeeStatus() == FeeStatus.PAID) ? 2 : 1;
        int submissionCount = predictionSubmissionRepository.countByStudent(student);

        if (submissionCount >= limit) {
            throw new LimitSubmissionException();
        }

        PredictionSubmission submission = PredictionSubmission.createSubmission(
                student,
                reqSubmitPrediction.sportTypeList(),
                reqSubmitPrediction.predictionResultList()
        );

        return predictionSubmissionRepository.save(submission);
    }

    @Transactional
    public void gradeMatchBySport(ReqGrade reqGrade) {
        // 1) Match 결과 업데이트
        MatchResult matchResult = matchResultRepository.findBySportType(reqGrade.sportType())
                .orElseGet(() -> new MatchResult(reqGrade.sportType())); // 없으면 생성
        matchResult.gradeMatch(reqGrade.predictionResult());
        matchResultRepository.save(matchResult);

        // 2) Prediction 채점
        List<Prediction> predictionList = predictionRepository.findBySportType(reqGrade.sportType());

        if (predictionList.isEmpty()) {
            throw new InvalidSportTypeException();
        }

        predictionList.forEach(p -> p.checkCorrect(reqGrade.predictionResult()));
        predictionRepository.saveAll(predictionList);

        // 3) PredictionSubmission 단위로 최종 채점 처리
        predictionList.stream()
                .map(Prediction::getPredictionSubmission)
                .distinct()
                .forEach(submission -> {
                    if (submission.getPredictionList().stream().allMatch(p -> p.getCorrect() != null)) {
                        submission.finalizeResult();
                        predictionSubmissionRepository.save(submission);
                    }
                });
    }


    public List<PredictionStatisticsBySportRes> getPredictionStatisticsBySport() {
        List<SportType> sportTypeList = Arrays.asList(SportType.values());

        return sportTypeList.stream()
                .map(sport -> {
                    List<Prediction> predictions = predictionRepository.findBySportType(sport);

                    long total = predictions.size();
                    if (total == 0) total = 1; // 0 나누기 방지

                    double teamAPercentage = predictions.stream()
                            .filter(p -> p.getPredictionResult() == PredictionResult.TEAM_A)
                            .count() * 100.0 / total;

                    double teamBPercentage = predictions.stream()
                            .filter(p -> p.getPredictionResult() == PredictionResult.TEAM_B)
                            .count() * 100.0 / total;

                    List<PredictionStatisticsRes> predictionList = List.of(
                            new PredictionStatisticsRes("TEAM_A", teamAPercentage),
                            new PredictionStatisticsRes("TEAM_B", teamBPercentage)
                    );

                    // ✅ 경기 결과 Match에서 가져오기
                    PredictionResult gameResult = matchResultRepository.findBySportType(sport)
                            .map(MatchResult::getGameResult)
                            .orElse(PredictionResult.BEFORE_THE_GAME);

                    return new PredictionStatisticsBySportRes(sport.name(), gameResult, predictionList);
                })
                .toList();
    }

}
