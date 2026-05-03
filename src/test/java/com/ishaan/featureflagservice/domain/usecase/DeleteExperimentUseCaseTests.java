package com.ishaan.featureflagservice.domain.usecase;

import com.ishaan.featureflagservice.data.repository.ExperimentRepository;
import com.ishaan.featureflagservice.domain.exception.ExperimentNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("DeleteExperimentUseCase Tests")
class DeleteExperimentUseCaseTests {

    @Test
    @DisplayName("Should delete experiment successfully")
    void testDeleteExperiment() throws ExperimentNotFoundException {
        ExperimentRepository repository = mock(ExperimentRepository.class);
        DeleteExperimentUseCase useCase = new DeleteExperimentUseCase(repository);

        when(repository.deleteExperiment("exp-1")).thenReturn(true);

        Boolean result = useCase.invoke("exp-1");

        assertTrue(result);
        verify(repository).deleteExperiment("exp-1");
    }

    @Test
    @DisplayName("Should throw ExperimentNotFoundException when experiment is not found")
    void testDeleteExperimentNotFound() throws ExperimentNotFoundException {
        ExperimentRepository repository = mock(ExperimentRepository.class);
        DeleteExperimentUseCase useCase = new DeleteExperimentUseCase(repository);

        when(repository.deleteExperiment("exp-1")).thenThrow(new ExperimentNotFoundException());

        assertThrows(ExperimentNotFoundException.class, () -> useCase.invoke("exp-1"));
        verify(repository).deleteExperiment("exp-1");
    }
}
