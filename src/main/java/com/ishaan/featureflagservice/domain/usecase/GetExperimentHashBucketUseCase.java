package com.ishaan.featureflagservice.domain.usecase;

import java.util.Objects;

public class GetExperimentHashBucketUseCase {
    
    public int invoke(String experimentName, String userId) {
        int hash = Math.abs(Objects.hash(experimentName, userId));
        return hash % 10000;
    }

}
