package com.ishaan.featureflagservice.domain.model;

public record Experiment(
    String name, 
    String featureName, 
    Double holdoutPercentage,
    Double enabledPercentage,
    Double disabledPercentage,
    Boolean isActive
) {}
