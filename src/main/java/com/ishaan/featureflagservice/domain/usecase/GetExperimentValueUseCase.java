package com.ishaan.featureflagservice.domain.usecase;

import com.ishaan.featureflagservice.data.model.Experiment;
import com.ishaan.featureflagservice.domain.exception.InvalidExperimentConfigurationException;

public class GetExperimentValueUseCase {
    
    public Boolean invoke(Experiment experiment, int hashbucket) throws InvalidExperimentConfigurationException {
        if(experiment.enabledPercentage() + experiment.disabledPercentage() + experiment.holdoutPercentage() != 100) {
            throw new InvalidExperimentConfigurationException();
        }
        Double enabledBucket = experiment.enabledPercentage() * 100;
        Double disabledBucket = (experiment.enabledPercentage() + experiment.disabledPercentage()) * 100;
        if (hashbucket < enabledBucket) {
            return true;
        } else if(hashbucket < disabledBucket) {
            return false;
        } else {
            return false; // holdout
        }
    }

}
