package com.ishaan.featureflagservice.di;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.ishaan.featureflagservice.data.repository.ExperimentRepository;
import com.ishaan.featureflagservice.data.repository.FeatureRepository;
import com.ishaan.featureflagservice.data.repository.UserRepository;
import com.ishaan.featureflagservice.domain.usecase.GetExperimentHashBucketUseCase;
import com.ishaan.featureflagservice.domain.usecase.GetExperimentValueUseCase;
import com.ishaan.featureflagservice.domain.usecase.OnDemandFeatureValueUseCase;

@Configuration
public class AppConfig {

    @Bean
    public FeatureRepository featureRepository() {
        return new FeatureRepository();
    }

    @Bean
    public ExperimentRepository experimentRepository() {
        return new ExperimentRepository();
    }

    @Bean
    public UserRepository userRepository() {
        return new UserRepository();
    }

    @Bean
    @Scope("prototype")
    public GetExperimentHashBucketUseCase getExperimentHashBucketUseCase() {
        return new GetExperimentHashBucketUseCase();
    }

    @Bean
    @Scope("prototype")
    public GetExperimentValueUseCase getExperimentValueUseCase() {
        return new GetExperimentValueUseCase();
    }

    @Bean
    @Scope("prototype")
    public OnDemandFeatureValueUseCase onDemandFeatureValueUseCase(
        FeatureRepository featureRepository,
        ExperimentRepository experimentRepository,
        ObjectProvider<GetExperimentHashBucketUseCase> getExperimentHashBucketUseCase,
        ObjectProvider<GetExperimentValueUseCase> getExperimentValueUseCase

    ) {
        return new OnDemandFeatureValueUseCase(
            featureRepository,
            experimentRepository,
            getExperimentHashBucketUseCase, 
            getExperimentValueUseCase
        );
    }
    
}
