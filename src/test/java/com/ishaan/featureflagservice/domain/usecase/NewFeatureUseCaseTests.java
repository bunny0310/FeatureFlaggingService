package com.ishaan.featureflagservice.domain.usecase;

import com.ishaan.featureflagservice.data.model.Feature;
import com.ishaan.featureflagservice.data.repository.FeatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("NewFeatureUseCase Tests")
@ExtendWith(MockitoExtension.class)
class NewFeatureUseCaseTests {

    @Mock
    private FeatureRepository repository;

    private NewFeatureUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new NewFeatureUseCase(repository);
    }

    @Test
    @DisplayName("Should invoke repository save method")
    void testInvokeCallsRepositorySave() {
        Feature feature = new Feature("dark-mode", true);
        when(repository.save(feature)).thenReturn(true);

        useCase.invoke(feature);

        verify(repository, times(1)).save(feature);
    }

    @Test
    @DisplayName("Should return true when feature save succeeds")
    void testInvokeReturnsTrueOnSuccess() {
        Feature feature = new Feature("dark-mode", true);
        when(repository.save(feature)).thenReturn(true);

        Boolean result = useCase.invoke(feature);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should pass feature to repository save")
    void testInvokePassesFeatureToRepository() {
        Feature feature = new Feature("beta-ui", false);
        when(repository.save(feature)).thenReturn(true);

        useCase.invoke(feature);

        ArgumentCaptor<Feature> captor = ArgumentCaptor.forClass(Feature.class);
        verify(repository).save(captor.capture());
        assertEquals(feature, captor.getValue());
    }

    @Test
    @DisplayName("Should save feature with enabled value")
    void testInvokeSaveEnabledFeature() {
        Feature feature = new Feature("dark-mode", true);
        when(repository.save(feature)).thenReturn(true);

        useCase.invoke(feature);

        ArgumentCaptor<Feature> captor = ArgumentCaptor.forClass(Feature.class);
        verify(repository).save(captor.capture());
        assertTrue(captor.getValue().value());
    }

    @Test
    @DisplayName("Should save feature with disabled value")
    void testInvokeSaveDisabledFeature() {
        Feature feature = new Feature("beta-ui", false);
        when(repository.save(feature)).thenReturn(true);

        useCase.invoke(feature);

        ArgumentCaptor<Feature> captor = ArgumentCaptor.forClass(Feature.class);
        verify(repository).save(captor.capture());
        assertFalse(captor.getValue().value());
    }

    @Test
    @DisplayName("Should preserve feature name")
    void testInvokePreservesFeatureName() {
        Feature feature = new Feature("my-feature-name", true);
        when(repository.save(feature)).thenReturn(true);

        useCase.invoke(feature);

        ArgumentCaptor<Feature> captor = ArgumentCaptor.forClass(Feature.class);
        verify(repository).save(captor.capture());
        assertEquals("my-feature-name", captor.getValue().name());
    }

    @Test
    @DisplayName("Should save feature with special characters in name")
    void testInvokeSaveFeatureWithSpecialCharacters() {
        Feature feature = new Feature("feature-with_special.chars", true);
        when(repository.save(feature)).thenReturn(true);

        useCase.invoke(feature);

        ArgumentCaptor<Feature> captor = ArgumentCaptor.forClass(Feature.class);
        verify(repository).save(captor.capture());
        assertEquals("feature-with_special.chars", captor.getValue().name());
    }

    @Test
    @DisplayName("Should save multiple different features")
    void testInvokeSaveMultipleFeatures() {
        Feature feature1 = new Feature("dark-mode", true);
        Feature feature2 = new Feature("beta-ui", false);
        Feature feature3 = new Feature("new-api", true);

        when(repository.save(feature1)).thenReturn(true);
        when(repository.save(feature2)).thenReturn(true);
        when(repository.save(feature3)).thenReturn(true);

        useCase.invoke(feature1);
        useCase.invoke(feature2);
        useCase.invoke(feature3);

        verify(repository, times(1)).save(feature1);
        verify(repository, times(1)).save(feature2);
        verify(repository, times(1)).save(feature3);
    }

    @Test
    @DisplayName("Should handle null feature")
    void testInvokeWithNullFeature() {
        when(repository.save(null)).thenReturn(true);

        Boolean result = useCase.invoke(null);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should return result from repository")
    void testInvokeReturnsRepositoryResult() {
        Feature feature = new Feature("dark-mode", true);
        when(repository.save(feature)).thenReturn(true);

        Boolean result = useCase.invoke(feature);

        assertEquals(true, result);
        verify(repository).save(feature);
    }

    @Test
    @DisplayName("Should call repository once per invoke")
    void testInvokeCallsRepositoryOncePerCall() {
        Feature feature = new Feature("dark-mode", true);
        when(repository.save(feature)).thenReturn(true);

        useCase.invoke(feature);
        useCase.invoke(feature);
        useCase.invoke(feature);

        verify(repository, times(3)).save(feature);
    }

    @Test
    @DisplayName("Should save feature exactly as provided")
    void testInvokeSaveExactFeature() {
        Feature feature = new Feature("test-feature", true);
        when(repository.save(feature)).thenReturn(true);

        useCase.invoke(feature);

        ArgumentCaptor<Feature> captor = ArgumentCaptor.forClass(Feature.class);
        verify(repository).save(captor.capture());
        Feature savedFeature = captor.getValue();

        assertEquals("test-feature", savedFeature.name());
        assertTrue(savedFeature.value());
    }

    @Test
    @DisplayName("Should handle feature with null name")
    void testInvokeWithFeatureNullName() {
        Feature feature = new Feature(null, true);
        when(repository.save(feature)).thenReturn(true);

        useCase.invoke(feature);

        ArgumentCaptor<Feature> captor = ArgumentCaptor.forClass(Feature.class);
        verify(repository).save(captor.capture());
        assertNull(captor.getValue().name());
    }

    @Test
    @DisplayName("Should delegate entirely to repository")
    void testInvokeFullDelegation() {
        Feature feature = new Feature("feature-1", false);
        when(repository.save(feature)).thenReturn(true);

        Boolean result = useCase.invoke(feature);

        assertTrue(result);
        verify(repository, times(1)).save(feature);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should handle repository returning false")
    void testInvokeWhenRepositoryReturnsFalse() {
        Feature feature = new Feature("dark-mode", true);
        when(repository.save(feature)).thenReturn(false);

        Boolean result = useCase.invoke(feature);

        // UseCase always returns true, regardless of repository result
        assertTrue(result);
    }

    @Test
    @DisplayName("Should pass exact feature instance to repository")
    void testInvokePassesExactFeatureInstance() {
        Feature feature = new Feature("exact-feature", true);
        when(repository.save(feature)).thenReturn(true);

        useCase.invoke(feature);

        ArgumentCaptor<Feature> captor = ArgumentCaptor.forClass(Feature.class);
        verify(repository).save(captor.capture());
        assertSame(feature, captor.getValue());
    }
}
