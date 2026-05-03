package com.ishaan.featureflagservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ishaan.featureflagservice.data.model.Experiment;
import com.ishaan.featureflagservice.domain.exception.ExperimentAlreadyExistsException;
import com.ishaan.featureflagservice.domain.exception.ExperimentNotFoundException;
import com.ishaan.featureflagservice.domain.exception.FeatureAlreadyLinkedToAnExperimentException;
import com.ishaan.featureflagservice.domain.usecase.DeleteExperimentUseCase;
import com.ishaan.featureflagservice.domain.usecase.GetExperimentByFeatureNameUseCase;
import com.ishaan.featureflagservice.domain.usecase.NewExperimentUseCase;
import com.ishaan.featureflagservice.domain.usecase.UpdateExperimentUseCase;

@DisplayName("ExperimentController Tests")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExperimentControllerTests {

    @Mock
    private ObjectProvider<NewExperimentUseCase> newExperimentUseCaseProvider;

    @Mock
    private ObjectProvider<GetExperimentByFeatureNameUseCase> getExperimentsByFeatureIdUseCaseProvider;

    @Mock
    private ObjectProvider<DeleteExperimentUseCase> deleteExperimentUseCaseProvider;

    @Mock
    private ObjectProvider<UpdateExperimentUseCase> updateExperimentUseCaseProvider;

    @Mock
    private NewExperimentUseCase newExperimentUseCase;

    @Mock
    private GetExperimentByFeatureNameUseCase getExperimentsByFeatureIdUseCase;

    @Mock
    private DeleteExperimentUseCase deleteExperimentUseCase;

    @Mock
    private UpdateExperimentUseCase updateExperimentUseCase;

    private ExperimentController controller;

    @BeforeEach
    void setUp() {
        controller = new ExperimentController(
            newExperimentUseCaseProvider,
            getExperimentsByFeatureIdUseCaseProvider,
            deleteExperimentUseCaseProvider,
            updateExperimentUseCaseProvider
        );

        when(newExperimentUseCaseProvider.getObject()).thenReturn(newExperimentUseCase);
        when(getExperimentsByFeatureIdUseCaseProvider.getObject()).thenReturn(getExperimentsByFeatureIdUseCase);
        when(deleteExperimentUseCaseProvider.getObject()).thenReturn(deleteExperimentUseCase);
        when(updateExperimentUseCaseProvider.getObject()).thenReturn(updateExperimentUseCase);
    }

    @Test
    @DisplayName("Should create experiment successfully")
    void testCreateExperiment() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException {
        Experiment entity = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);
        when(newExperimentUseCase.invoke(entity)).thenReturn(true);

        ResponseEntity<Object> response = controller.create(entity);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
        verify(newExperimentUseCase).invoke(entity);
    }

    @Test
    @DisplayName("Should return conflict when feature is already linked to an experiment")
    @SuppressWarnings("unchecked")
    void testCreateExperimentConflict() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException {
        Experiment entity = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);
        when(newExperimentUseCase.invoke(entity)).thenThrow(new FeatureAlreadyLinkedToAnExperimentException());

        ResponseEntity<Object> response = controller.create(entity);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("An experiment already exists for this feature.", body.get("message"));
    }

    @Test
    @DisplayName("Should return conflict when experiment name already exists")
    @SuppressWarnings("unchecked")
    void testCreateExperimentNameAlreadyExistsConflict() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException {
        Experiment entity = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);
        when(newExperimentUseCase.invoke(entity)).thenThrow(new ExperimentAlreadyExistsException());

        ResponseEntity<Object> response = controller.create(entity);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("An experiment with this already exists.", body.get("message"));
    }

    @Test
    @DisplayName("Should get experiment for feature")
    void testGetExperimentByFeature() throws ExperimentNotFoundException {
        Experiment entity = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);
        when(getExperimentsByFeatureIdUseCase.invoke("dark-mode")).thenReturn(entity);

        ResponseEntity<Object> response = controller.getExperimentByFeatureName("dark-mode");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("exp-1", ((Experiment) response.getBody()).name());
    }

    @Test
    @DisplayName("Should return not found when getting unknown experiment by feature")
    @SuppressWarnings("unchecked")
    void testGetExperimentByFeatureNotFound() throws ExperimentNotFoundException {
        when(getExperimentsByFeatureIdUseCase.invoke("dark-mode")).thenThrow(new ExperimentNotFoundException());

        ResponseEntity<Object> response = controller.getExperimentByFeatureName("dark-mode");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Experiment not found.", body.get("message"));
    }

    @Test
    @DisplayName("Should delete experiment successfully")
    void testDeleteExperiment() throws ExperimentNotFoundException {
        when(deleteExperimentUseCase.invoke("exp-1")).thenReturn(true);

        ResponseEntity<Object> response = controller.deleteExperiment("exp-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
    }

    @Test
    @DisplayName("Should return not found when deleting unknown experiment")
    @SuppressWarnings("unchecked")
    void testDeleteExperimentNotFound() throws ExperimentNotFoundException {
        when(deleteExperimentUseCase.invoke("exp-1")).thenThrow(new ExperimentNotFoundException());

        ResponseEntity<Object> response = controller.deleteExperiment("exp-1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Experiment not found.", body.get("message"));
    }

    @Test
    @DisplayName("Should update experiment successfully")
    void testUpdateExperiment() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentNotFoundException {
        Experiment entity = new Experiment("body-id", "dark-mode", 10.0, 50.0, 40.0, false);
        when(updateExperimentUseCase.invoke(
            "exp-1",
            new Experiment("exp-1", "dark-mode", 10.0, 50.0, 40.0, false)
        )).thenReturn(true);

        ResponseEntity<Object> response = controller.updateExperiment("exp-1", entity);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
    }

    @Test
    @DisplayName("Should return not found when updating unknown experiment")
    @SuppressWarnings("unchecked")
    void testUpdateExperimentNotFound() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentNotFoundException {
        Experiment entity = new Experiment("body-id", "dark-mode", 10.0, 50.0, 40.0, false);
        when(updateExperimentUseCase.invoke(
            "exp-1",
            new Experiment("exp-1", "dark-mode", 10.0, 50.0, 40.0, false)
        )).thenThrow(new ExperimentNotFoundException());

        ResponseEntity<Object> response = controller.updateExperiment("exp-1", entity);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Experiment not found.", body.get("message"));
    }

    @Test
    @DisplayName("Should return conflict when update violates active experiment rule")
    @SuppressWarnings("unchecked")
    void testUpdateExperimentConflict() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentNotFoundException {
        Experiment entity = new Experiment("body-id", "dark-mode", 10.0, 50.0, 40.0, true);
        when(updateExperimentUseCase.invoke(
            "exp-1",
            new Experiment("exp-1", "dark-mode", 10.0, 50.0, 40.0, true)
        )).thenThrow(new FeatureAlreadyLinkedToAnExperimentException());

        ResponseEntity<Object> response = controller.updateExperiment("exp-1", entity);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(body.containsKey("message"));
        assertEquals("An active experiment already exists for this feature.", body.get("message"));
    }
}
