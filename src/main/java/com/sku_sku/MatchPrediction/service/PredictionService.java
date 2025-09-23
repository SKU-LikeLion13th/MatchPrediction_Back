package com.sku_sku.MatchPrediction.service;

import com.sku_sku.MatchPrediction.domain.MatchResult;
import com.sku_sku.MatchPrediction.domain.Prediction;
import com.sku_sku.MatchPrediction.domain.PredictionSubmission;
import com.sku_sku.MatchPrediction.domain.Student;
import com.sku_sku.MatchPrediction.dto.PredictionStatisticsBySportRes;
import com.sku_sku.MatchPrediction.dto.ReqGrade;
import com.sku_sku.MatchPrediction.dto.ReqSubmitPrediction;
import com.sku_sku.MatchPrediction.enums.FeeStatus;
import com.sku_sku.MatchPrediction.enums.PredictionResult;
import com.sku_sku.MatchPrediction.enums.SportType;
import com.sku_sku.MatchPrediction.exception.InvalidSportTypeException;
import com.sku_sku.MatchPrediction.exception.LimitSubmissionException;
import com.sku_sku.MatchPrediction.reposiroty.MatchResultRepository;
import com.sku_sku.MatchPrediction.reposiroty.PredictionRepository;
import com.sku_sku.MatchPrediction.reposiroty.PredictionSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final PredictionSubmissionRepository predictionSubmissionRepository;
    private final MatchResultRepository matchResultRepository;

    @Transactional
    public PredictionSubmission submitPrediction(Student student, ReqSubmitPrediction reqSubmitPrediction) {
//        Student student = studentReposiroty.findById(reqSubmitPrediction.id())
//                .orElseThrow(InvalidStudentPKException::new);

        int limit = (student.getFeeStatus() == FeeStatus.PAID) ? 2 : 1;
        int submissionCount = predictionSubmissionRepository.countByStudent(student);

        if (submissionCount >= limit) {
            throw new LimitSubmissionException();
        }

        List<SportType> sportTypeList = reqSubmitPrediction.predictionRequestList().stream()
                .map(ReqSubmitPrediction.PredictionRequest::sportType)
                .toList();

        List<PredictionResult> predictionResultList = reqSubmitPrediction.predictionRequestList().stream()
                .map(ReqSubmitPrediction.PredictionRequest::predictionResult)
                .toList();

        PredictionSubmission submission = PredictionSubmission.createSubmission(
                student,
                sportTypeList,
                predictionResultList
        );

        return predictionSubmissionRepository.save(submission);
    }


    @Transactional
    public void gradeMatchBySport(ReqGrade reqGrade) {
        MatchResult matchResult = matchResultRepository.findBySportType(reqGrade.sportType())
                .orElseGet(() -> new MatchResult(reqGrade.sportType()));
        matchResult.gradeMatch(reqGrade.predictionResult());
        matchResultRepository.save(matchResult);

        List<Prediction> predictionList = predictionRepository.findBySportType(reqGrade.sportType());

        if (predictionList.isEmpty()) {
            throw new InvalidSportTypeException();
        }

        predictionList.forEach(p -> p.checkCorrect(reqGrade.predictionResult()));
        predictionRepository.saveAll(predictionList);

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
                    if (total == 0) total = 1;

                    long teamACount = predictions.stream()
                            .filter(p -> p.getPredictionResult() == PredictionResult.TEAM_A)
                            .count();

                    int teamAPercentage = Math.toIntExact(Math.round(teamACount * 100.0 / total));
                    int teamBPercentage = 100 - teamAPercentage;

                    List<PredictionStatisticsBySportRes.PredictionResWrapper> predictionList = List.of(
                            new PredictionStatisticsBySportRes.PredictionResWrapper("TEAM_A", teamAPercentage),
                            new PredictionStatisticsBySportRes.PredictionResWrapper("TEAM_B", teamBPercentage)
                    );

                    PredictionResult gameResult = matchResultRepository.findBySportType(sport)
                            .map(MatchResult::getGameResult)
                            .orElse(PredictionResult.BEFORE_THE_GAME);

                    return new PredictionStatisticsBySportRes(sport.name(), gameResult, predictionList);
                })
                .toList();
    }
}
