package com.ishaan.featureflagservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.ishaan.featureflagservice.data.model.Feature;
import com.ishaan.featureflagservice.data.repository.FeatureRepository;
import com.ishaan.featureflagservice.domain.exception.FeatureNotFoundException;
import com.ishaan.featureflagservice.domain.usecase.GetAllFeaturesUseCase;
import com.ishaan.featureflagservice.domain.usecase.NewFeatureUseCase;
import com.ishaan.featureflagservice.domain.usecase.OnDemandFeatureValueUseCase;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/features")
public class FeatureController {

  private FeatureRepository featureRepository;
  private ObjectProvider<OnDemandFeatureValueUseCase> onDemandFeatureValueUseCase;

  public FeatureController(
    FeatureRepository featureRepository,
    ObjectProvider<OnDemandFeatureValueUseCase> onDemandFeatureValueUseCase
  ){
      this.featureRepository = featureRepository;
      this.onDemandFeatureValueUseCase = onDemandFeatureValueUseCase;
    }

  @PostMapping("/")
  public ResponseEntity<Boolean> create(@RequestBody Feature entity) {
    new NewFeatureUseCase(featureRepository).invoke(entity);
    return ResponseEntity.ok(true);
  }

  @GetMapping("/")
  public ResponseEntity<List<Feature>> getFeatures() {
      List<Feature> features = new GetAllFeaturesUseCase(featureRepository).invoke();
      return ResponseEntity.ok(features);
  }

    @GetMapping("/{name}")
  public ResponseEntity<Object> getFeatureValue(
    @PathVariable String name,
    @RequestParam String userId
  ) {
      OnDemandFeatureValueUseCase useCase = onDemandFeatureValueUseCase.getObject();
      try {
        return ResponseEntity.ok(useCase.invoke(name, userId));
      } catch(FeatureNotFoundException exception) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", "Feature not found.");
        return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(map);
      }
  }
  

}