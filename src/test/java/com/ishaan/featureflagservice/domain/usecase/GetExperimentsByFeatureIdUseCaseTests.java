package com.ishaan.featureflagservice.domain.usecase;

import com.ishaan.featureflagservice.data.model.Experiment;
import com.ishaan.featureflagservice.data.repository.ExperimentRepository;
import com.ishaan.featureflagservice.domain.exception.ExperimentNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("GetExperimentByFeatureNameUseCase Tests")
class GetExperimentsByFeatureIdUseCaseTests {

    @Test
    @DisplayName("Should return experiment for feature")
    void testReturnExperimentByFeature() throws ExperimentNotFoundException {
        ExperimentRepository repository = mock(ExperimentRepository.class);
        GetExperimentByFeatureNameUseCase useCase = new GetExperimentByFeatureNameUseCase(repository);

        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);
        when(repository.getExperimentByFeatureName("dark-mode")).thenReturn(experiment);

        Experiment result = useCase.invoke("dark-mode");

        assertNotNull(result);
        assertEquals("exp-1", result.name());
        verify(repository).getExperimentByFeatureName("dark-mode");
    }
}

