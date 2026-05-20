package com.ishaan.featureflagservice.domain.usecase;

import com.ishaan.featureflagservice.data.repository.FeatureRepository;
import com.ishaan.featureflagservice.domain.model.Feature;;

public class NewFeatureUseCase {

    private FeatureRepository repository;

    public NewFeatureUseCase(FeatureRepository repository){
        this.repository = repository;
    }
    
    public Boolean invoke(Feature feature) {
        repository.save(feature);
        return true;
    }

}
