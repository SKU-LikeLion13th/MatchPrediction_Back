package com.sku_sku.MatchPrediction.dto;

import com.sku_sku.MatchPrediction.enums.FeeStatus;
import com.sku_sku.MatchPrediction.enums.RoleType;

public record LoginInfoRes(String email, String major, String studentId, String name, String phoneNum, FeeStatus feeStatus, RoleType roleType) {
}
