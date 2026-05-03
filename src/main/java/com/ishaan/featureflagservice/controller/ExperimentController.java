package com.ishaan.featureflagservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ishaan.featureflagservice.data.model.Experiment;
import com.ishaan.featureflagservice.domain.exception.ExperimentAlreadyExistsException;
import com.ishaan.featureflagservice.domain.exception.ExperimentNotFoundException;
import com.ishaan.featureflagservice.domain.exception.FeatureAlreadyLinkedToAnExperimentException;
import com.ishaan.featureflagservice.domain.usecase.DeleteExperimentUseCase;
import com.ishaan.featureflagservice.domain.usecase.GetExperimentByFeatureNameUseCase;
import com.ishaan.featureflagservice.domain.usecase.NewExperimentUseCase;
import com.ishaan.featureflagservice.domain.usecase.UpdateExperimentUseCase;

@RestController
@RequestMapping("/experiments")
public class ExperimentController {

    private final ObjectProvider<NewExperimentUseCase> newExperimentUseCase;
    private final ObjectProvider<GetExperimentByFeatureNameUseCase> getExperimentByFeatureNameUseCase;
    private final ObjectProvider<DeleteExperimentUseCase> deleteExperimentUseCase;
    private final ObjectProvider<UpdateExperimentUseCase> updateExperimentUseCase;

    public ExperimentController(
        ObjectProvider<NewExperimentUseCase> newExperimentUseCase,
        ObjectProvider<GetExperimentByFeatureNameUseCase> getExperimentByFeatureNameUseCase,
        ObjectProvider<DeleteExperimentUseCase> deleteExperimentUseCase,
        ObjectProvider<UpdateExperimentUseCase> updateExperimentUseCase
    ) {
        this.newExperimentUseCase = newExperimentUseCase;
        this.getExperimentByFeatureNameUseCase = getExperimentByFeatureNameUseCase;
        this.deleteExperimentUseCase = deleteExperimentUseCase;
        this.updateExperimentUseCase = updateExperimentUseCase;
    }

    @PostMapping("/")
    public ResponseEntity<Object> create(@RequestBody Experiment entity) {
        try {
            newExperimentUseCase.getObject().invoke(entity);
            return ResponseEntity.ok(true);
        } catch (ExperimentAlreadyExistsException exception) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("message", "An experiment with this already exists.");
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(map);
        } catch (FeatureAlreadyLinkedToAnExperimentException exception) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("message", "An experiment already exists for this feature.");
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(map);
        }
    }

    @GetMapping("/{featureName}")
    public ResponseEntity<Object> getExperimentByFeatureName(@PathVariable String featureName) {
        try {
            Experiment experiment = getExperimentByFeatureNameUseCase.getObject().invoke(featureName);
            return ResponseEntity.ok(experiment);
        } catch (ExperimentNotFoundException exception) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("message", "Experiment not found.");
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(map);
        }
    }

    @DeleteMapping("/{experimentName}")
    public ResponseEntity<Object> deleteExperiment(@PathVariable String experimentName) {
        try {
            deleteExperimentUseCase.getObject().invoke(experimentName);
            return ResponseEntity.ok(true);
        } catch (ExperimentNotFoundException exception) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("message", "Experiment not found.");
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(map);
        }
    }

    @PutMapping("/{experimentName}")
    public ResponseEntity<Object> updateExperiment(
        @PathVariable String experimentName,
        @RequestBody Experiment entity
    ) {
        Experiment updatedExperiment = new Experiment(
            experimentName,
            entity.featureName(),
            entity.holdoutPercentage(),
            entity.enabledPercentage(),
            entity.disabledPercentage(),
            entity.isActive()
        );

        try {
            updateExperimentUseCase.getObject().invoke(experimentName, updatedExperiment);
            return ResponseEntity.ok(true);
        } catch (ExperimentNotFoundException exception) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("message", "Experiment not found.");
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(map);
        } catch (FeatureAlreadyLinkedToAnExperimentException exception) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("message", "An active experiment already exists for this feature.");
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(map);
        }
    }
}
