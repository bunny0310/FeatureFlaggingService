package com.ishaan.featureflagservice.domain.usecase;

import com.ishaan.featureflagservice.data.model.Feature;
import com.ishaan.featureflagservice.data.repository.FeatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GetAllFeaturesUseCase Tests")
@ExtendWith(MockitoExtension.class)
class GetAllFeaturesUseCaseTests {

    @Mock
    private FeatureRepository repository;

    private GetAllFeaturesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetAllFeaturesUseCase(repository);
    }

    @Test
    @DisplayName("Should invoke repository fetch method")
    void testInvokeCallsRepositoryFetch() {
        List<Feature> mockFeatures = new ArrayList<>();
        when(repository.fetch()).thenReturn(mockFeatures);

        useCase.invoke();

        verify(repository, times(1)).fetch();
    }

    @Test
    @DisplayName("Should return empty list when repository has no features")
    void testInvokeReturnEmptyList() {
        when(repository.fetch()).thenReturn(new ArrayList<>());

        List<Feature> result = useCase.invoke();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should return features from repository")
    void testInvokeReturnFeatures() {
        List<Feature> mockFeatures = new ArrayList<>();
        mockFeatures.add(new Feature("dark-mode", true));
        mockFeatures.add(new Feature("beta-ui", false));

        when(repository.fetch()).thenReturn(mockFeatures);

        List<Feature> result = useCase.invoke();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(f -> f.name().equals("dark-mode")));
        assertTrue(result.stream().anyMatch(f -> f.name().equals("beta-ui")));
    }

    @Test
    @DisplayName("Should return single feature from repository")
    void testInvokeReturnSingleFeature() {
        List<Feature> mockFeatures = new ArrayList<>();
        mockFeatures.add(new Feature("dark-mode", true));

        when(repository.fetch()).thenReturn(mockFeatures);

        List<Feature> result = useCase.invoke();

        assertEquals(1, result.size());
        assertEquals("dark-mode", result.get(0).name());
    }

    @Test
    @DisplayName("Should return multiple features in order")
    void testInvokeReturnMultipleFeaturesInOrder() {
        List<Feature> mockFeatures = new ArrayList<>();
        Feature f1 = new Feature("feature-1", true);
        Feature f2 = new Feature("feature-2", false);
        Feature f3 = new Feature("feature-3", true);
        mockFeatures.add(f1);
        mockFeatures.add(f2);
        mockFeatures.add(f3);

        when(repository.fetch()).thenReturn(mockFeatures);

        List<Feature> result = useCase.invoke();

        assertEquals(3, result.size());
        assertEquals(f1, result.get(0));
        assertEquals(f2, result.get(1));
        assertEquals(f3, result.get(2));
    }

    @Test
    @DisplayName("Should handle repository returning null")
    void testInvokeWithNullRepository() {
        when(repository.fetch()).thenReturn(null);

        List<Feature> result = useCase.invoke();

        assertNull(result);
    }

    @Test
    @DisplayName("Should call repository only once")
    void testInvokeCallsRepositoryOncePerCall() {
        List<Feature> mockFeatures = new ArrayList<>();
        when(repository.fetch()).thenReturn(mockFeatures);

        useCase.invoke();
        useCase.invoke();

        verify(repository, times(2)).fetch();
    }

    @Test
    @DisplayName("Should preserve feature values from repository")
    void testInvokePreservesFeatureValues() {
        List<Feature> mockFeatures = new ArrayList<>();
        Feature enabledFeature = new Feature("enabled-feature", true);
        Feature disabledFeature = new Feature("disabled-feature", false);
        mockFeatures.add(enabledFeature);
        mockFeatures.add(disabledFeature);

        when(repository.fetch()).thenReturn(mockFeatures);

        List<Feature> result = useCase.invoke();

        assertEquals(true, result.stream().filter(f -> f.name().equals("enabled-feature")).findFirst().get().value());
        assertEquals(false, result.stream().filter(f -> f.name().equals("disabled-feature")).findFirst().get().value());
    }

    @Test
    @DisplayName("Should handle large number of features")
    void testInvokeWithManyFeatures() {
        List<Feature> mockFeatures = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            mockFeatures.add(new Feature("feature-" + i, i % 2 == 0));
        }

        when(repository.fetch()).thenReturn(mockFeatures);

        List<Feature> result = useCase.invoke();

        assertEquals(100, result.size());
    }

    @Test
    @DisplayName("Should delegate entirely to repository")
    void testInvokeValueDelegation() {
        Feature feature1 = new Feature("test-1", true);
        Feature feature2 = new Feature("test-2", false);
        List<Feature> expected = List.of(feature1, feature2);

        when(repository.fetch()).thenReturn(expected);

        List<Feature> result = useCase.invoke();

        assertEquals(expected, result);
        verify(repository).fetch();
    }
}
