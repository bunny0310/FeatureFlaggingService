package com.ishaan.featureflagservice.domain.usecase;

import com.ishaan.featureflagservice.data.repository.ExperimentRepository;
import com.ishaan.featureflagservice.domain.exception.ExperimentNotFoundException;
import com.ishaan.featureflagservice.domain.model.Experiment;

public class GetExperimentByFeatureNameUseCase {

    private ExperimentRepository repository;

    public GetExperimentByFeatureNameUseCase(ExperimentRepository repository) {
        this.repository = repository;
    }

    public Experiment invoke(String featureName) throws ExperimentNotFoundException {
        return repository.getExperimentByFeatureName(featureName);
    }
}
