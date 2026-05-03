package com.ishaan.featureflagservice.domain.usecase;

import com.ishaan.featureflagservice.data.model.Experiment;
import com.ishaan.featureflagservice.data.repository.ExperimentRepository;
import com.ishaan.featureflagservice.domain.exception.ExperimentAlreadyExistsException;
import com.ishaan.featureflagservice.domain.exception.FeatureAlreadyLinkedToAnExperimentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("NewExperimentUseCase Tests")
class NewExperimentUseCaseTests {

    @Test
    @DisplayName("Should save experiment successfully")
    void testSaveExperiment() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException {
        ExperimentRepository repository = mock(ExperimentRepository.class);
        NewExperimentUseCase useCase = new NewExperimentUseCase(repository);
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);

        when(repository.save(experiment)).thenReturn(true);

        Boolean result = useCase.invoke(experiment);

        assertTrue(result);
        verify(repository).save(experiment);
    }

    @Test
    @DisplayName("Should throw FeatureAlreadyLinkedToAnExperimentException when feature is already linked")
    void testThrowFeatureAlreadyLinkedToAnExperimentException()
        throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException {
        ExperimentRepository repository = mock(ExperimentRepository.class);
        NewExperimentUseCase useCase = new NewExperimentUseCase(repository);
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);

        when(repository.save(experiment)).thenThrow(new FeatureAlreadyLinkedToAnExperimentException());

        assertThrows(FeatureAlreadyLinkedToAnExperimentException.class, () -> useCase.invoke(experiment));
    }

    @Test
    @DisplayName("Should throw ExperimentAlreadyExistsException when experiment name already exists")
    void testThrowExperimentAlreadyExistsException()
        throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException {
        ExperimentRepository repository = mock(ExperimentRepository.class);
        NewExperimentUseCase useCase = new NewExperimentUseCase(repository);
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);

        when(repository.save(experiment)).thenThrow(new ExperimentAlreadyExistsException());

        assertThrows(ExperimentAlreadyExistsException.class, () -> useCase.invoke(experiment));
    }
}
