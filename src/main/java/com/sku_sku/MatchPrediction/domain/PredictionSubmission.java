package com.sku_sku.MatchPrediction.domain;

import com.sku_sku.MatchPrediction.enums.PredictionResult;
import com.sku_sku.MatchPrediction.enums.SportType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Entity
@Getter
@NoArgsConstructor
public class PredictionSubmission {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;

    @OneToMany(mappedBy = "predictionSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prediction> predictionList = new ArrayList<>();

    private boolean grade;
    private boolean correct;

    // 연관관계 편의 메서드
    public void addPrediction(Prediction prediction) {
        predictionList.add(prediction);
        prediction.setPredictionSubmission(this);
    }

    public void finalizeResult() {
        this.grade = true;
        this.correct = predictionList.stream().allMatch(p -> Boolean.TRUE.equals(p.getCorrect()));
    }

    public static PredictionSubmission createSubmission(Student student, List<SportType> sportTypes, List<PredictionResult> predictionResultList) {
        PredictionSubmission submission = new PredictionSubmission();
        submission.student = student;

        IntStream.range(0, sportTypes.size())
                .mapToObj(i -> new Prediction(sportTypes.get(i), predictionResultList.get(i)))
                .forEach(submission::addPrediction);

        return submission;
    }

}

