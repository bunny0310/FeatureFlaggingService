package com.ishaan.featureflagservice.domain.usecase;

import com.ishaan.featureflagservice.data.repository.ExperimentRepository;
import com.ishaan.featureflagservice.domain.exception.ExperimentAlreadyExistsException;
import com.ishaan.featureflagservice.domain.exception.FeatureAlreadyLinkedToAnExperimentException;
import com.ishaan.featureflagservice.domain.model.Experiment;

public class NewExperimentUseCase {

    private ExperimentRepository repository;

    public NewExperimentUseCase(ExperimentRepository repository) {
        this.repository = repository;
    }

    public Boolean invoke(Experiment experiment)
        throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException {
        repository.save(experiment);
        return true;
    }
}
