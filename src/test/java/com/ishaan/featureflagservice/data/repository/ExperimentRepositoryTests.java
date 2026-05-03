package com.ishaan.featureflagservice.data.repository;

import com.ishaan.featureflagservice.data.model.Experiment;
import com.ishaan.featureflagservice.domain.exception.ExperimentAlreadyExistsException;
import com.ishaan.featureflagservice.domain.exception.ExperimentNotFoundException;
import com.ishaan.featureflagservice.domain.exception.FeatureAlreadyLinkedToAnExperimentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ExperimentRepository Tests")
class ExperimentRepositoryTests {

    private ExperimentRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ExperimentRepository();
    }

    @Test
    @DisplayName("Should save an experiment successfully")
    void testSaveExperiment() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException {
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);

        Boolean result = repository.save(experiment);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should save multiple experiments for different features")
    void testSaveMultipleExperiments() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException {
        Experiment exp1 = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);
        Experiment exp2 = new Experiment("exp-2", "beta-ui", 30.0, 50.0, 20.0, true);

        repository.save(exp1);
        repository.save(exp2);

        List<Experiment> experiments = repository.fetch();
        assertEquals(2, experiments.size());
    }

    @Test
    @DisplayName("Should throw FeatureAlreadyLinkedToAnExperimentException when saving experiment for same feature")
    void testSaveDuplicateActiveExperiment() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException {
        Experiment exp1 = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);
        Experiment exp2 = new Experiment("exp-2", "dark-mode", 25.0, 50.0, 25.0, true);

        repository.save(exp1);

        assertThrows(FeatureAlreadyLinkedToAnExperimentException.class, () -> repository.save(exp2));
    }

    @Test
    @DisplayName("Should not allow saving another experiment when feature is already linked")
    void testSaveActiveExperimentAfterInactive() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException {
        Experiment inactive = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, false);
        Experiment active = new Experiment("exp-2", "dark-mode", 25.0, 50.0, 25.0, true);

        repository.save(inactive);
        assertThrows(FeatureAlreadyLinkedToAnExperimentException.class, () -> repository.save(active));
    }

    @Test
    @DisplayName("Should fetch all experiments")
    void testFetchAllExperiments() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException {
        Experiment exp1 = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);
        Experiment exp2 = new Experiment("exp-2", "beta-ui", 30.0, 50.0, 20.0, true);

        repository.save(exp1);
        repository.save(exp2);

        List<Experiment> experiments = repository.fetch();

        assertEquals(2, experiments.size());
    }

    @Test
    @DisplayName("Should return empty list when no experiments exist")
    void testFetchEmptyRepository() {
        List<Experiment> experiments = repository.fetch();

        assertNotNull(experiments);
        assertEquals(0, experiments.size());
    }

    @Test
    @DisplayName("Should get experiment by feature name")
    void testGetExperimentByFeatureName() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException, ExperimentNotFoundException {
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);
        repository.save(experiment);

        Experiment retrieved = repository.getExperimentByFeatureName("dark-mode");

        assertNotNull(retrieved);
        assertEquals("dark-mode", retrieved.featureName());
        assertEquals("exp-1", retrieved.name());
    }

    @Test
    @DisplayName("Should throw ExperimentNotFoundException when experiment by feature is not found")
    void testGetExperimentNotFound() {
        assertThrows(ExperimentNotFoundException.class, () -> repository.getExperimentByFeatureName("non-existent"));
    }

    @Test
    @DisplayName("Should not overwrite experiment with same feature name")
    void testSaveExperimentOverwrite() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException, ExperimentNotFoundException {
        Experiment exp1 = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, false);
        Experiment exp2 = new Experiment("exp-2", "dark-mode", 25.0, 50.0, 25.0, false);

        repository.save(exp1);
        assertThrows(FeatureAlreadyLinkedToAnExperimentException.class, () -> repository.save(exp2));

        Experiment retrieved = repository.getExperimentByFeatureName("dark-mode");
        assertEquals("exp-1", retrieved.name());
    }

    @Test
    @DisplayName("Should store experiment with correct configuration")
    void testExperimentConfigurationPreserved() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException, ExperimentNotFoundException {
        Experiment experiment = new Experiment("exp-1", "dark-mode", 10.5, 45.2, 44.3, true);
        repository.save(experiment);

        Experiment retrieved = repository.getExperimentByFeatureName("dark-mode");

        assertEquals("exp-1", retrieved.name());
        assertEquals("dark-mode", retrieved.featureName());
        assertEquals(10.5, retrieved.holdoutPercentage());
        assertEquals(45.2, retrieved.enabledPercentage());
        assertEquals(44.3, retrieved.disabledPercentage());
        assertTrue(retrieved.isActive());
    }

    @Test
    @DisplayName("Should handle inline active check for multiple features")
    void testMultipleFeaturesWithOneActive() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException {
        Experiment inactiveExp = new Experiment("exp-1", "feature-a", 20.0, 40.0, 40.0, false);
        Experiment activeExp = new Experiment("exp-2", "feature-b", 20.0, 40.0, 40.0, true);

        repository.save(inactiveExp);
        repository.save(activeExp);

        List<Experiment> experiments = repository.fetch();
        assertEquals(2, experiments.size());
    }

    @Test
    @DisplayName("Should return copy of experiments list")
    void testFetchReturnsCopy() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException {
        Experiment exp = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);
        repository.save(exp);

        List<Experiment> list1 = repository.fetch();
        List<Experiment> list2 = repository.fetch();

        assertNotSame(list1, list2);
        assertEquals(list1, list2);
    }

    @Test
    @DisplayName("Should get experiment by experiment name")
    void testGetExperimentByName() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException, ExperimentNotFoundException {
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);
        repository.save(experiment);

        Experiment retrieved = repository.getExperimentByName("exp-1");

        assertNotNull(retrieved);
        assertEquals("dark-mode", retrieved.featureName());
    }

    @Test
    @DisplayName("Should throw ExperimentNotFoundException when experiment name is not found")
    void testGetExperimentByNameNotFound() {
        assertThrows(ExperimentNotFoundException.class, () -> repository.getExperimentByName("non-existent"));
    }

    @Test
    @DisplayName("Should delete experiment by experiment name")
    void testDeleteExperimentByName() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException, ExperimentNotFoundException {
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);
        repository.save(experiment);

        Boolean deleted = repository.deleteExperiment("exp-1");

        assertTrue(deleted);
        assertEquals(0, repository.fetch().size());
    }

    @Test
    @DisplayName("Should throw ExperimentNotFoundException when deleting non-existent experiment by name")
    void testDeleteExperimentByNameNotFound() {
        assertThrows(ExperimentNotFoundException.class, () -> repository.deleteExperiment("non-existent"));
    }

    @Test
    @DisplayName("Should update experiment by experiment name")
    void testUpdateExperimentByName() throws FeatureAlreadyLinkedToAnExperimentException, ExperimentAlreadyExistsException, ExperimentNotFoundException {
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);
        repository.save(experiment);

        Experiment updated = new Experiment("exp-1", "dark-mode", 10.0, 50.0, 40.0, false);
        Boolean result = repository.updateExperiment("exp-1", updated);

        assertTrue(result);

        Experiment retrieved = repository.getExperimentByFeatureName("dark-mode");
        assertNotNull(retrieved);
        assertEquals(10.0, retrieved.holdoutPercentage());
        assertEquals(50.0, retrieved.enabledPercentage());
        assertEquals(40.0, retrieved.disabledPercentage());
        assertFalse(retrieved.isActive());
    }

    @Test
    @DisplayName("Should throw ExperimentNotFoundException when updating non-existent experiment by name")
    void testUpdateExperimentByNameNotFound() throws FeatureAlreadyLinkedToAnExperimentException {
        Experiment updated = new Experiment("exp-1", "dark-mode", 10.0, 50.0, 40.0, false);

        assertThrows(ExperimentNotFoundException.class, () -> repository.updateExperiment("non-existent", updated));
    }
}
