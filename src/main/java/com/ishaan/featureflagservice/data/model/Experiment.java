package com.ishaan.featureflagservice.data.model;

public record Experiment(
    String name, 
    String featureName, 
    Double holdoutPercentage,
    Double enabledPercentage,
    Double disabledPercentage,
    Boolean isActive
) {}
