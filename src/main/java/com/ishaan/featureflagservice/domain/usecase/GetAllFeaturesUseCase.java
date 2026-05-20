package com.ishaan.featureflagservice.domain.usecase;

import java.util.List;

import com.ishaan.featureflagservice.data.repository.FeatureRepository;
import com.ishaan.featureflagservice.domain.model.Feature;

public class GetAllFeaturesUseCase {

    private FeatureRepository repository;

    public GetAllFeaturesUseCase(FeatureRepository repository){
        this.repository = repository;
    }
    
    public List<Feature> invoke() {
        return this.repository.fetch();
    }

}
