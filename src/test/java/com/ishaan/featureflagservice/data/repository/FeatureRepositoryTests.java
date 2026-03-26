package com.ishaan.featureflagservice.data.repository;

import com.ishaan.featureflagservice.data.model.Feature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FeatureRepository Tests")
class FeatureRepositoryTests {

    private FeatureRepository repository;

    @BeforeEach
    void setUp() {
        repository = new FeatureRepository();
    }

    @Test
    @DisplayName("Should save a feature successfully")
    void testSaveFeature() {
        Feature feature = new Feature("dark-mode", true);

        Boolean result = repository.save(feature);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should save multiple features")
    void testSaveMultipleFeatures() {
        Feature feature1 = new Feature("dark-mode", true);
        Feature feature2 = new Feature("beta-ui", false);
        Feature feature3 = new Feature("new-api", true);

        repository.save(feature1);
        repository.save(feature2);
        repository.save(feature3);

        List<Feature> features = repository.fetch();
        assertEquals(3, features.size());
    }

    @Test
    @DisplayName("Should overwrite existing feature with same name")
    void testSaveFeatureOverwrite() {
        Feature feature1 = new Feature("dark-mode", true);
        Feature feature2 = new Feature("dark-mode", false);

        repository.save(feature1);
        repository.save(feature2);

        List<Feature> features = repository.fetch();
        assertEquals(1, features.size());
        assertEquals(false, features.get(0).value());
    }

    @Test
    @DisplayName("Should fetch all features")
    void testFetchAllFeatures() {
        Feature feature1 = new Feature("dark-mode", true);
        Feature feature2 = new Feature("beta-ui", false);

        repository.save(feature1);
        repository.save(feature2);

        List<Feature> features = repository.fetch();

        assertEquals(2, features.size());
        assertTrue(features.stream().anyMatch(f -> f.name().equals("dark-mode")));
        assertTrue(features.stream().anyMatch(f -> f.name().equals("beta-ui")));
    }

    @Test
    @DisplayName("Should return empty list when no features exist")
    void testFetchEmptyRepository() {
        List<Feature> features = repository.fetch();

        assertNotNull(features);
        assertEquals(0, features.size());
    }

    @Test
    @DisplayName("Should return copy of features list to prevent external modification")
    void testFetchReturnsCopy() {
        Feature feature1 = new Feature("dark-mode", true);
        repository.save(feature1);

        List<Feature> features1 = repository.fetch();
        List<Feature> features2 = repository.fetch();

        assertNotSame(features1, features2);
        assertEquals(features1, features2);
    }

    @Test
    @DisplayName("Should get feature by name")
    void testGetFeatureByName() {
        Feature feature = new Feature("dark-mode", true);
        repository.save(feature);

        Feature retrieved = repository.getFeature("dark-mode");

        assertNotNull(retrieved);
        assertEquals("dark-mode", retrieved.name());
        assertTrue(retrieved.value());
    }

    @Test
    @DisplayName("Should return null when feature not found")
    void testGetFeatureNotFound() {
        Feature retrieved = repository.getFeature("non-existent");

        assertNull(retrieved);
    }

    @Test
    @DisplayName("Should get feature value by name")
    void testGetFeatureValue() {
        Feature feature = new Feature("dark-mode", true);
        repository.save(feature);

        Boolean value = repository.getFeatureValue("dark-mode");

        assertTrue(value);
    }

    @Test
    @DisplayName("Should return false when feature not found for getFeatureValue")
    void testGetFeatureValueNotFound() {
        Boolean value = repository.getFeatureValue("non-existent");

        assertFalse(value);
    }

    @Test
    @DisplayName("Should return false when feature value is false")
    void testGetFeatureValueFalse() {
        Feature feature = new Feature("beta-ui", false);
        repository.save(feature);

        Boolean value = repository.getFeatureValue("beta-ui");

        assertFalse(value);
    }

    @Test
    @DisplayName("Should handle null feature name gracefully")
    void testGetFeatureWithNullName() {
        Feature retrieved = repository.getFeature(null);

        assertNull(retrieved);
    }

    @Test
    @DisplayName("Should handle special characters in feature names")
    void testFeatureWithSpecialCharacters() {
        Feature feature = new Feature("feature-with_special.chars", true);
        repository.save(feature);

        Feature retrieved = repository.getFeature("feature-with_special.chars");

        assertNotNull(retrieved);
        assertEquals("feature-with_special.chars", retrieved.name());
    }
}
