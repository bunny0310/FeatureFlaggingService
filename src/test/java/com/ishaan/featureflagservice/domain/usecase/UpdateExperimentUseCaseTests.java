package com.ishaan.featureflagservice.domain.usecase;

import com.ishaan.featureflagservice.data.model.Experiment;
import com.ishaan.featureflagservice.data.repository.ExperimentRepository;
import com.ishaan.featureflagservice.domain.exception.ExperimentNotFoundException;
import com.ishaan.featureflagservice.domain.exception.FeatureAlreadyLinkedToAnExperimentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("UpdateExperimentUseCase Tests")
class UpdateExperimentUseCaseTests {

    @Test
    @DisplayName("Should update experiment successfully")
    void testUpdateExperiment() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentNotFoundException {
        ExperimentRepository repository = mock(ExperimentRepository.class);
        UpdateExperimentUseCase useCase = new UpdateExperimentUseCase(repository);
        Experiment updated = new Experiment("exp-1", "dark-mode", 10.0, 50.0, 40.0, false);

        when(repository.updateExperiment("exp-1", updated)).thenReturn(true);

        Boolean result = useCase.invoke("exp-1", updated);

        assertTrue(result);
        verify(repository).updateExperiment("exp-1", updated);
    }

    @Test
    @DisplayName("Should throw ExperimentNotFoundException when experiment is not found")
    void testUpdateExperimentNotFound() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentNotFoundException {
        ExperimentRepository repository = mock(ExperimentRepository.class);
        UpdateExperimentUseCase useCase = new UpdateExperimentUseCase(repository);
        Experiment updated = new Experiment("exp-1", "dark-mode", 10.0, 50.0, 40.0, false);

        when(repository.updateExperiment("exp-1", updated)).thenThrow(new ExperimentNotFoundException());

        assertThrows(ExperimentNotFoundException.class, () -> useCase.invoke("exp-1", updated));
    }

    @Test
    @DisplayName("Should throw ActiveExperimentException when update violates active rule")
    void testThrowActiveExperimentException() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentNotFoundException {
        ExperimentRepository repository = mock(ExperimentRepository.class);
        UpdateExperimentUseCase useCase = new UpdateExperimentUseCase(repository);
        Experiment updated = new Experiment("exp-1", "dark-mode", 10.0, 50.0, 40.0, true);

        when(repository.updateExperiment("exp-1", updated)).thenThrow(new FeatureAlreadyLinkedToAnExperimentException());

        assertThrows(FeatureAlreadyLinkedToAnExperimentException.class, () -> useCase.invoke("exp-1", updated));
    }
}
