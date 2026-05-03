package com.ishaan.featureflagservice.domain.usecase;

import com.ishaan.featureflagservice.data.model.Experiment;
import com.ishaan.featureflagservice.data.model.Feature;
import com.ishaan.featureflagservice.data.repository.ExperimentRepository;
import com.ishaan.featureflagservice.data.repository.FeatureRepository;
import com.ishaan.featureflagservice.domain.exception.FeatureNotFoundException;
import com.ishaan.featureflagservice.domain.exception.InvalidExperimentConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("OnDemandFeatureValueUseCase Tests")
@ExtendWith(MockitoExtension.class)
class OnDemandFeatureValueUseCaseTests {

    @Mock
    private FeatureRepository featureRepository;

    @Mock
    private ExperimentRepository experimentRepository;

    @Mock
    private ObjectProvider<GetExperimentHashBucketUseCase> getExperimentHashBucketUseCaseProvider;

    @Mock
    private ObjectProvider<GetExperimentValueUseCase> getExperimentValueUseCaseProvider;

    @Mock
    private GetExperimentHashBucketUseCase getExperimentHashBucketUseCase;

    @Mock
    private GetExperimentValueUseCase getExperimentValueUseCase;

    private OnDemandFeatureValueUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new OnDemandFeatureValueUseCase(
            featureRepository,
            experimentRepository,
            getExperimentHashBucketUseCaseProvider,
            getExperimentValueUseCaseProvider
        );

        lenient().when(getExperimentHashBucketUseCaseProvider.getObject()).thenReturn(getExperimentHashBucketUseCase);
        lenient().when(getExperimentValueUseCaseProvider.getObject()).thenReturn(getExperimentValueUseCase);
    }

    @Test
    @DisplayName("Should throw FeatureNotFoundException when feature not found")
    void testThrowFeatureNotFoundExceptionWhenFeatureNotExists() {
        when(featureRepository.getFeature("non-existent")).thenReturn(null);

        assertThrows(FeatureNotFoundException.class, () -> useCase.invoke("non-existent", "user-1"));
    }

    @Test
    @DisplayName("Should return feature value when no experiment exists")
    void testReturnFeatureValueWhenNoExperiment() throws Exception {
        Feature feature = new Feature("dark-mode", true);
        when(featureRepository.getFeature("dark-mode")).thenReturn(feature);
        when(experimentRepository.getExperimentByFeatureName("dark-mode")).thenReturn(null);

        Boolean result = useCase.invoke("dark-mode", "user-1");

        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false feature value when no experiment exists")
    void testReturnFalseFeatureValueWhenNoExperiment() throws Exception {
        Feature feature = new Feature("beta-ui", false);
        when(featureRepository.getFeature("beta-ui")).thenReturn(feature);
        when(experimentRepository.getExperimentByFeatureName("beta-ui")).thenReturn(null);

        Boolean result = useCase.invoke("beta-ui", "user-1");

        assertFalse(result);
    }

    @Test
    @DisplayName("Should use experiment value when experiment exists")
    void testReturnExperimentValueWhenExperimentExists() throws Exception {
        Feature feature = new Feature("dark-mode", true);
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 30.0, true);

        when(featureRepository.getFeature("dark-mode")).thenReturn(feature);
        when(experimentRepository.getExperimentByFeatureName("dark-mode")).thenReturn(experiment);
        when(getExperimentHashBucketUseCase.invoke("exp-1", "user-1")).thenReturn(2500);
        when(getExperimentValueUseCase.invoke(experiment, 2500)).thenReturn(true);

        Boolean result = useCase.invoke("dark-mode", "user-1");

        assertTrue(result);
        verify(getExperimentHashBucketUseCase).invoke("exp-1", "user-1");
        verify(getExperimentValueUseCase).invoke(experiment, 2500);
    }

    @Test
    @DisplayName("Should calculate hash bucket with experiment name and user ID")
    void testCalculateHashBucketWithExperimentAndUser() throws Exception {
        Feature feature = new Feature("dark-mode", true);
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 30.0, true);

        when(featureRepository.getFeature("dark-mode")).thenReturn(feature);
        when(experimentRepository.getExperimentByFeatureName("dark-mode")).thenReturn(experiment);
        when(getExperimentHashBucketUseCase.invoke("exp-1", "user-1")).thenReturn(5000);
        when(getExperimentValueUseCase.invoke(experiment, 5000)).thenReturn(false);

        useCase.invoke("dark-mode", "user-1");

        verify(getExperimentHashBucketUseCase).invoke("exp-1", "user-1");
    }

    @Test
    @DisplayName("Should fall back to feature value on InvalidExperimentConfigurationException")
    void testFallbackToFeatureValueOnInvalidConfiguration() throws Exception {
        Feature feature = new Feature("dark-mode", false);
        Experiment experiment = new Experiment("exp-1", "dark-mode", 10.0, 50.0, 30.0, true);

        when(featureRepository.getFeature("dark-mode")).thenReturn(feature);
        when(experimentRepository.getExperimentByFeatureName("dark-mode")).thenReturn(experiment);
        when(getExperimentHashBucketUseCase.invoke("exp-1", "user-1")).thenReturn(2500);
        when(getExperimentValueUseCase.invoke(experiment, 2500))
            .thenThrow(new InvalidExperimentConfigurationException());

        Boolean result = useCase.invoke("dark-mode", "user-1");

        assertFalse(result);
    }

    @Test
    @DisplayName("Should fetch feature from repository")
    void testFetchFeatureFromRepository() throws Exception {
        Feature feature = new Feature("dark-mode", true);
        when(featureRepository.getFeature("dark-mode")).thenReturn(feature);
        when(experimentRepository.getExperimentByFeatureName("dark-mode")).thenReturn(null);

        useCase.invoke("dark-mode", "user-1");

        verify(featureRepository).getFeature("dark-mode");
    }

    @Test
    @DisplayName("Should fetch experiment from repository")
    void testFetchExperimentFromRepository() throws Exception {
        Feature feature = new Feature("dark-mode", true);
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 30.0, true);

        when(featureRepository.getFeature("dark-mode")).thenReturn(feature);
        when(experimentRepository.getExperimentByFeatureName("dark-mode")).thenReturn(experiment);
        when(getExperimentHashBucketUseCase.invoke("exp-1", "user-1")).thenReturn(2500);
        when(getExperimentValueUseCase.invoke(experiment, 2500)).thenReturn(true);

        useCase.invoke("dark-mode", "user-1");

        verify(experimentRepository).getExperimentByFeatureName("dark-mode");
    }

    @Test
    @DisplayName("Should handle different user IDs consistently")
    void testHandleDifferentUserIds() throws Exception {
        Feature feature = new Feature("dark-mode", true);
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 30.0, true);

        when(featureRepository.getFeature("dark-mode")).thenReturn(feature);
        when(experimentRepository.getExperimentByFeatureName("dark-mode")).thenReturn(experiment);
        when(getExperimentHashBucketUseCase.invoke("exp-1", "user-1")).thenReturn(2500);
        when(getExperimentHashBucketUseCase.invoke("exp-1", "user-2")).thenReturn(3000);
        when(getExperimentValueUseCase.invoke(experiment, 2500)).thenReturn(true);
        when(getExperimentValueUseCase.invoke(experiment, 3000)).thenReturn(true);

        useCase.invoke("dark-mode", "user-1");
        useCase.invoke("dark-mode", "user-2");

        verify(getExperimentHashBucketUseCase).invoke("exp-1", "user-1");
        verify(getExperimentHashBucketUseCase).invoke("exp-1", "user-2");
    }

    @Test
    @DisplayName("Should pass correct hash bucket to getExperimentValue")
    void testPassCorrectHashBucketToExperimentValue() throws Exception {
        Feature feature = new Feature("dark-mode", true);
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 30.0, true);

        when(featureRepository.getFeature("dark-mode")).thenReturn(feature);
        when(experimentRepository.getExperimentByFeatureName("dark-mode")).thenReturn(experiment);
        when(getExperimentHashBucketUseCase.invoke("exp-1", "user-1")).thenReturn(7500);
        when(getExperimentValueUseCase.invoke(experiment, 7500)).thenReturn(false);

        useCase.invoke("dark-mode", "user-1");

        verify(getExperimentValueUseCase).invoke(experiment, 7500);
    }

    @Test
    @DisplayName("Should use experiment name from experiment object")
    void testUseExperimentNameFromExperimentObject() throws Exception {
        Feature feature = new Feature("dark-mode", true);
        Experiment experiment = new Experiment("my-experiment", "dark-mode", 20.0, 50.0, 30.0, true);

        when(featureRepository.getFeature("dark-mode")).thenReturn(feature);
        when(experimentRepository.getExperimentByFeatureName("dark-mode")).thenReturn(experiment);
        when(getExperimentHashBucketUseCase.invoke("my-experiment", "user-1")).thenReturn(2500);
        when(getExperimentValueUseCase.invoke(experiment, 2500)).thenReturn(true);

        useCase.invoke("dark-mode", "user-1");

        verify(getExperimentHashBucketUseCase).invoke("my-experiment", "user-1");
    }

    @Test
    @DisplayName("Should return experiment value when available")
    void testReturnExperimentValue() throws Exception {
        Feature feature = new Feature("dark-mode", true);
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 30.0, true);

        when(featureRepository.getFeature("dark-mode")).thenReturn(feature);
        when(experimentRepository.getExperimentByFeatureName("dark-mode")).thenReturn(experiment);
        when(getExperimentHashBucketUseCase.invoke("exp-1", "user-1")).thenReturn(6000);
        when(getExperimentValueUseCase.invoke(experiment, 6000)).thenReturn(false);

        Boolean result = useCase.invoke("dark-mode", "user-1");

        assertFalse(result);
    }

    @Test
    @DisplayName("Should work with multiple features")
    void testWorkWithMultipleFeatures() throws Exception {
        Feature feature1 = new Feature("feature-1", true);
        Feature feature2 = new Feature("feature-2", false);

        when(featureRepository.getFeature("feature-1")).thenReturn(feature1);
        when(featureRepository.getFeature("feature-2")).thenReturn(feature2);
        when(experimentRepository.getExperimentByFeatureName("feature-1")).thenReturn(null);
        when(experimentRepository.getExperimentByFeatureName("feature-2")).thenReturn(null);

        Boolean result1 = useCase.invoke("feature-1", "user-1");
        Boolean result2 = useCase.invoke("feature-2", "user-1");

        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    @DisplayName("Should handle feature name case-sensitively")
    void testFeatureNameCaseSensitive() throws Exception {
        Feature feature = new Feature("dark-mode", true);

        when(featureRepository.getFeature("dark-mode")).thenReturn(feature);
        when(featureRepository.getFeature("Dark-Mode")).thenReturn(null);
        when(experimentRepository.getExperimentByFeatureName("dark-mode")).thenReturn(null);

        Boolean result = useCase.invoke("dark-mode", "user-1");
        assertTrue(result);

        assertThrows(FeatureNotFoundException.class, () -> useCase.invoke("Dark-Mode", "user-1"));
    }

    @Test
    @DisplayName("Should provide correct order of operations")
    void testCorrectOrderOfOperations() throws Exception {
        Feature feature = new Feature("dark-mode", true);
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 30.0, true);

        when(featureRepository.getFeature("dark-mode")).thenReturn(feature);
        when(experimentRepository.getExperimentByFeatureName("dark-mode")).thenReturn(experiment);
        when(getExperimentHashBucketUseCase.invoke("exp-1", "user-1")).thenReturn(2500);
        when(getExperimentValueUseCase.invoke(experiment, 2500)).thenReturn(false);

        useCase.invoke("dark-mode", "user-1");

        // Verify order: feature fetch, experiment fetch, hash bucket, experiment value
        inOrder(
            featureRepository,
            experimentRepository,
            getExperimentHashBucketUseCase,
            getExperimentValueUseCase
        ).verify(featureRepository).getFeature("dark-mode");
    }

    @Test
    @DisplayName("Should handle null feature name")
    void testNullFeatureName() {
        when(featureRepository.getFeature(null)).thenReturn(null);

        assertThrows(FeatureNotFoundException.class, () -> useCase.invoke(null, "user-1"));
    }

    @Test
    @DisplayName("Should handle empty feature name")
    void testEmptyFeatureName() {
        when(featureRepository.getFeature("")).thenReturn(null);

        assertThrows(FeatureNotFoundException.class, () -> useCase.invoke("", "user-1"));
    }

    @Test
    @DisplayName("Should support feature with special characters")
    void testFeatureWithSpecialCharacters() throws Exception {
        Feature feature = new Feature("feature-with_special.chars", true);

        when(featureRepository.getFeature("feature-with_special.chars")).thenReturn(feature);
        when(experimentRepository.getExperimentByFeatureName("feature-with_special.chars")).thenReturn(null);

        Boolean result = useCase.invoke("feature-with_special.chars", "user-1");

        assertTrue(result);
    }

    @Test
    @DisplayName("Should support numeric user IDs")
    void testNumericUserIds() throws Exception {
        Feature feature = new Feature("dark-mode", true);
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 30.0, true);

        when(featureRepository.getFeature("dark-mode")).thenReturn(feature);
        when(experimentRepository.getExperimentByFeatureName("dark-mode")).thenReturn(experiment);
        when(getExperimentHashBucketUseCase.invoke("exp-1", "123")).thenReturn(2500);
        when(getExperimentValueUseCase.invoke(experiment, 2500)).thenReturn(true);

        Boolean result = useCase.invoke("dark-mode", "123");

        assertTrue(result);
        verify(getExperimentHashBucketUseCase).invoke("exp-1", "123");
    }

    @Test
    @DisplayName("Should verify InvalidExperimentConfigurationException handling")
    void testInvalidExperimentConfigurationExceptionHandling() throws Exception {
        Feature feature = new Feature("dark-mode", true);
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 30.0, true);

        when(featureRepository.getFeature("dark-mode")).thenReturn(feature);
        when(experimentRepository.getExperimentByFeatureName("dark-mode")).thenReturn(experiment);
        when(getExperimentHashBucketUseCase.invoke("exp-1", "user-1")).thenReturn(2500);
        when(getExperimentValueUseCase.invoke(experiment, 2500))
            .thenThrow(new InvalidExperimentConfigurationException());

        Boolean result = useCase.invoke("dark-mode", "user-1");

        assertTrue(result); // Falls back to feature value (true)
    }
}
