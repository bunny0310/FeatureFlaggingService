package com.ishaan.featureflagservice.data.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ishaan.featureflagservice.data.model.Experiment;
import com.ishaan.featureflagservice.domain.exception.ActiveExperimentException;

public class ExperimentRepository {
    
    private Map<String, Experiment> experiments = new HashMap<String, Experiment>();


    public Boolean save(Experiment experiment) throws ActiveExperimentException {
        if(experiments.containsKey(experiment.featureName()) && experiments.get(experiment.featureName()).isActive()) {
            throw new ActiveExperimentException();
        }
        experiments.put(experiment.featureName(), experiment);
        return true;
    }

    public List<Experiment> fetch() {
        return new ArrayList<Experiment>(experiments.values());
    }

    public Experiment getExperiment(String featureName) {
        return experiments.get(featureName);
    }

}
