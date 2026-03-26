package com.ishaan.featureflagservice.data.repository;

import com.ishaan.featureflagservice.data.model.Experiment;
import com.ishaan.featureflagservice.domain.exception.ActiveExperimentException;
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
    void testSaveExperiment() throws ActiveExperimentException {
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);

        Boolean result = repository.save(experiment);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should save multiple experiments for different features")
    void testSaveMultipleExperiments() throws ActiveExperimentException {
        Experiment exp1 = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);
        Experiment exp2 = new Experiment("exp-2", "beta-ui", 30.0, 50.0, 20.0, true);

        repository.save(exp1);
        repository.save(exp2);

        List<Experiment> experiments = repository.fetch();
        assertEquals(2, experiments.size());
    }

    @Test
    @DisplayName("Should throw ActiveExperimentException when saving duplicate active experiment")
    void testSaveDuplicateActiveExperiment() throws ActiveExperimentException {
        Experiment exp1 = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);
        Experiment exp2 = new Experiment("exp-2", "dark-mode", 25.0, 50.0, 25.0, true);

        repository.save(exp1);

        assertThrows(ActiveExperimentException.class, () -> repository.save(exp2));
    }

    @Test
    @DisplayName("Should allow saving active experiment when previous is inactive and should overwrite the inactive one")
    void testSaveActiveExperimentAfterInactive() throws ActiveExperimentException {
        Experiment inactive = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, false);
        Experiment active = new Experiment("exp-2", "dark-mode", 25.0, 50.0, 25.0, true);

        repository.save(inactive);
        Boolean result = repository.save(active);

        assertTrue(result);

        assert(repository.fetch().size() == 1);
    }

    @Test
    @DisplayName("Should fetch all experiments")
    void testFetchAllExperiments() throws ActiveExperimentException {
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
    void testGetExperimentByFeatureName() throws ActiveExperimentException {
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);
        repository.save(experiment);

        Experiment retrieved = repository.getExperiment("dark-mode");

        assertNotNull(retrieved);
        assertEquals("dark-mode", retrieved.featureName());
        assertEquals("exp-1", retrieved.name());
    }

    @Test
    @DisplayName("Should return null when experiment not found")
    void testGetExperimentNotFound() {
        Experiment retrieved = repository.getExperiment("non-existent");

        assertNull(retrieved);
    }

    @Test
    @DisplayName("Should overwrite experiment with same feature name")
    void testSaveExperimentOverwrite() throws ActiveExperimentException {
        Experiment exp1 = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, false);
        Experiment exp2 = new Experiment("exp-2", "dark-mode", 25.0, 50.0, 25.0, false);

        repository.save(exp1);
        repository.save(exp2);

        Experiment retrieved = repository.getExperiment("dark-mode");
        assertEquals("exp-2", retrieved.name());
    }

    @Test
    @DisplayName("Should store experiment with correct configuration")
    void testExperimentConfigurationPreserved() throws ActiveExperimentException {
        Experiment experiment = new Experiment("exp-1", "dark-mode", 10.5, 45.2, 44.3, true);
        repository.save(experiment);

        Experiment retrieved = repository.getExperiment("dark-mode");

        assertEquals("exp-1", retrieved.name());
        assertEquals("dark-mode", retrieved.featureName());
        assertEquals(10.5, retrieved.holdoutPercentage());
        assertEquals(45.2, retrieved.enabledPercentage());
        assertEquals(44.3, retrieved.disabledPercentage());
        assertTrue(retrieved.isActive());
    }

    @Test
    @DisplayName("Should handle inline active check for multiple features")
    void testMultipleFeaturesWithOneActive() throws ActiveExperimentException {
        Experiment inactiveExp = new Experiment("exp-1", "feature-a", 20.0, 40.0, 40.0, false);
        Experiment activeExp = new Experiment("exp-2", "feature-b", 20.0, 40.0, 40.0, true);

        repository.save(inactiveExp);
        repository.save(activeExp);

        List<Experiment> experiments = repository.fetch();
        assertEquals(2, experiments.size());
    }

    @Test
    @DisplayName("Should return copy of experiments list")
    void testFetchReturnsCopy() throws ActiveExperimentException {
        Experiment exp = new Experiment("exp-1", "dark-mode", 20.0, 40.0, 40.0, true);
        repository.save(exp);

        List<Experiment> list1 = repository.fetch();
        List<Experiment> list2 = repository.fetch();

        assertNotSame(list1, list2);
        assertEquals(list1, list2);
    }
}
