package com.ishaan.featureflagservice.domain.usecase;

import org.springframework.beans.factory.ObjectProvider;

import com.ishaan.featureflagservice.data.model.Experiment;
import com.ishaan.featureflagservice.data.model.Feature;
import com.ishaan.featureflagservice.data.repository.ExperimentRepository;
import com.ishaan.featureflagservice.data.repository.FeatureRepository;
import com.ishaan.featureflagservice.domain.exception.FeatureNotFoundException;
import com.ishaan.featureflagservice.domain.exception.InvalidExperimentConfigurationException;

public class OnDemandFeatureValueUseCase {
    
    private FeatureRepository featureRepository;
    private ExperimentRepository experimentRepository;
    private final ObjectProvider<GetExperimentHashBucketUseCase> getExperimentHashBucketUseCase;
    private final ObjectProvider<GetExperimentValueUseCase> getExperimentValueUseCase;

    public OnDemandFeatureValueUseCase(
        FeatureRepository featureRepository,
        ExperimentRepository experimentRepository,
        ObjectProvider<GetExperimentHashBucketUseCase> getExperimentHashBucketUseCase,
        ObjectProvider<GetExperimentValueUseCase> getExperimentValueUseCase
    ){
        this.featureRepository = featureRepository;
        this.experimentRepository = experimentRepository;
        this.getExperimentHashBucketUseCase = getExperimentHashBucketUseCase;
        this.getExperimentValueUseCase = getExperimentValueUseCase;
    }

    public Boolean invoke(String name, String userId) throws FeatureNotFoundException {
        Feature feature = featureRepository.getFeature(name);
        if (feature == null) {
            throw new FeatureNotFoundException();
        }
        try {
            Experiment experiment = experimentRepository.getExperiment(name);
            if(experiment == null) {
                return feature.value(); // assuming the feature value is the same for all users for the MVP.
            }
            int hashedBucket = getExperimentHashBucketUseCase.getObject().invoke(experiment.name(), userId);
            return getExperimentValueUseCase.getObject().invoke(experiment, hashedBucket);
            
        } catch(InvalidExperimentConfigurationException exception) {
            // add tracking
            return feature.value();
        }
    }

}
