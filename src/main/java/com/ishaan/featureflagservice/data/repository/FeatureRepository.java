package com.ishaan.featureflagservice.data.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ishaan.featureflagservice.data.model.Feature;

public class FeatureRepository {
    
    private Map<String, Feature> features = new HashMap<String, Feature>();


    public Boolean save(Feature feature) {
        features.put(feature.name(), feature);
        return true;
    }

    public List<Feature> fetch() {
        return new ArrayList<Feature>(features.values());
    }

    public Feature getFeature(String name) {
        return features.get(name);
    }

    public Boolean getFeatureValue(String name) {
        Feature feature = features.get(name);
        return (feature != null) ? feature.value() : false;
    }

}
