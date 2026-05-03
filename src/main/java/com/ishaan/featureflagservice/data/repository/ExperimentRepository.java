package com.ishaan.featureflagservice.data.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ishaan.featureflagservice.data.model.Experiment;
import com.ishaan.featureflagservice.domain.exception.ExperimentAlreadyExistsException;
import com.ishaan.featureflagservice.domain.exception.ExperimentNotFoundException;
import com.ishaan.featureflagservice.domain.exception.FeatureAlreadyLinkedToAnExperimentException;

public class ExperimentRepository {
    
    private Map<String, Experiment> experimentsByExperimentName = new HashMap<String, Experiment>();
    private Map<String, Experiment> experimentsByFeatureName;

    public ExperimentRepository() {
        this.experimentsByFeatureName = createExperimentsByFeatureName(experimentsByExperimentName);
    }


    public Boolean save(Experiment experiment) throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException {
        if(
            experimentsByExperimentName.containsKey(experiment.name()) 
        ) {
            throw new ExperimentAlreadyExistsException();
        }
        if(
            experimentsByFeatureName.containsKey(experiment.featureName()) 
        ) {
            throw new FeatureAlreadyLinkedToAnExperimentException();
        }
        experimentsByExperimentName.put(experiment.name(), experiment);
        syncExperimentsByFeatureName();
        return true;
    }

    public List<Experiment> fetch() {
        return new ArrayList<Experiment>(experimentsByFeatureName.values());
    }

    public Experiment getExperimentByFeatureName(String featureName) throws ExperimentNotFoundException {
        Experiment experiment = experimentsByFeatureName.get(featureName);
        if (experiment == null) {
            throw new ExperimentNotFoundException();
        }
        return experiment;
    }

    public Experiment getExperimentByName(String experimentName) throws ExperimentNotFoundException {
        Experiment experiment = experimentsByExperimentName.get(experimentName);
        if (experiment == null) {
            throw new ExperimentNotFoundException();
        }
        return experiment;
    }

    public Boolean deleteExperiment(String experimentName) throws ExperimentNotFoundException {
        Experiment removedExperiment = experimentsByExperimentName.remove(experimentName);
        if (removedExperiment == null) {
            throw new ExperimentNotFoundException();
        }

        syncExperimentsByFeatureName();
        return true;
    }

    public Boolean updateExperiment(String experimentName, Experiment updatedExperiment)
        throws ExperimentNotFoundException, FeatureAlreadyLinkedToAnExperimentException {
        Experiment existingExperiment = experimentsByExperimentName.get(experimentName);
        if (existingExperiment == null) {
            throw new ExperimentNotFoundException();
        }
        
        // If updating to active and feature is linked to another active experiment, throw exception
        if (updatedExperiment.isActive()) {
            Experiment existingFeatureExperiment = experimentsByFeatureName.get(updatedExperiment.featureName());
            if (existingFeatureExperiment != null && 
                !existingFeatureExperiment.name().equals(experimentName) &&
                existingFeatureExperiment.isActive()) {
                throw new FeatureAlreadyLinkedToAnExperimentException();
            }
        }
        
        Experiment newExperiment = new Experiment(
            updatedExperiment.name(),
            updatedExperiment.featureName(),
            updatedExperiment.holdoutPercentage(),
            updatedExperiment.enabledPercentage(),
            updatedExperiment.disabledPercentage(),
            updatedExperiment.isActive()
        );
        experimentsByExperimentName.put(newExperiment.name(), newExperiment);
        syncExperimentsByFeatureName();
        return true;
    }

    private Map<String, Experiment> createExperimentsByFeatureName(Map<String, Experiment> experimentsByExperimentName) {
        Map<String, Experiment> experimentsByFeatureName = new HashMap<String, Experiment>();
        for (Experiment experiment : experimentsByExperimentName.values()) {
            experimentsByFeatureName.put(experiment.featureName(), experiment);
        }
        return experimentsByFeatureName;
    }

    private void syncExperimentsByFeatureName() {
        experimentsByFeatureName = createExperimentsByFeatureName(experimentsByExperimentName);
    }

}
