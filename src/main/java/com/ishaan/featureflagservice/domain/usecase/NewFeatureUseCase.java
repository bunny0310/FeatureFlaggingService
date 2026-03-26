package com.ishaan.featureflagservice.domain.usecase;

import com.ishaan.featureflagservice.data.model.Feature;
import com.ishaan.featureflagservice.data.repository.FeatureRepository;;

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
