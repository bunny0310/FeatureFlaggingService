package com.ishaan.featureflagservice.domain.usecase;

import com.ishaan.featureflagservice.data.repository.ExperimentRepository;
import com.ishaan.featureflagservice.domain.exception.ExperimentNotFoundException;

public class DeleteExperimentUseCase {

    private ExperimentRepository repository;

    public DeleteExperimentUseCase(ExperimentRepository repository) {
        this.repository = repository;
    }

    public Boolean invoke(String experimentName) throws ExperimentNotFoundException {
        return repository.deleteExperiment(experimentName);
    }
}
