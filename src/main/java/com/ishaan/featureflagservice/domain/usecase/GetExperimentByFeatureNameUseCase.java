package com.ishaan.featureflagservice.domain.usecase;

import com.ishaan.featureflagservice.data.model.Experiment;
import com.ishaan.featureflagservice.data.repository.ExperimentRepository;
import com.ishaan.featureflagservice.domain.exception.ExperimentNotFoundException;

public class GetExperimentByFeatureNameUseCase {

    private ExperimentRepository repository;

    public GetExperimentByFeatureNameUseCase(ExperimentRepository repository) {
        this.repository = repository;
    }

    public Experiment invoke(String featureName) throws ExperimentNotFoundException {
        return repository.getExperimentByFeatureName(featureName);
    }
}
