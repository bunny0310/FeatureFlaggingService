package com.ishaan.featureflagservice.domain.usecase;

import com.ishaan.featureflagservice.data.model.Experiment;
import com.ishaan.featureflagservice.data.repository.ExperimentRepository;
import com.ishaan.featureflagservice.domain.exception.ExperimentNotFoundException;
import com.ishaan.featureflagservice.domain.exception.FeatureAlreadyLinkedToAnExperimentException;

public class UpdateExperimentUseCase {

    private ExperimentRepository repository;

    public UpdateExperimentUseCase(ExperimentRepository repository) {
        this.repository = repository;
    }

    public Boolean invoke(String experimentName, Experiment experiment)
        throws FeatureAlreadyLinkedToAnExperimentException, ExperimentNotFoundException {
        return repository.updateExperiment(experimentName, experiment);
    }
}
