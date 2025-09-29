package com.sku_sku.MatchPrediction.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class PaidStudent {

    @Id
    @GeneratedValue
    private Long id;

    private String studentId;
}
