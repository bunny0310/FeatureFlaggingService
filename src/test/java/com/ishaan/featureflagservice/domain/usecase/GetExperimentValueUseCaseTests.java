package com.ishaan.featureflagservice.domain.usecase;

import com.ishaan.featureflagservice.data.model.Experiment;
import com.ishaan.featureflagservice.domain.exception.InvalidExperimentConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GetExperimentValueUseCase Tests")
class GetExperimentValueUseCaseTests {

    private GetExperimentValueUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetExperimentValueUseCase();
    }

    @Test
    @DisplayName("Should return true when hash bucket in enabled percentage range")
    void testReturnTrueForEnabledBucket() throws InvalidExperimentConfigurationException {
        // enabledPercentage = 50%, so buckets 0-4999 should be enabled
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 30.0, true);

        Boolean result = useCase.invoke(experiment, 2500);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when hash bucket in disabled percentage range")
    void testReturnFalseForDisabledBucket() throws InvalidExperimentConfigurationException {
        // enabledPercentage = 50%, disabledPercentage = 30%
        // Disabled range: 5000-7999
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 30.0, true);

        Boolean result = useCase.invoke(experiment, 6500);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when hash bucket in holdout percentage range")
    void testReturnFalseForHoldoutBucket() throws InvalidExperimentConfigurationException {
        // enabledPercentage = 50%, disabledPercentage = 30%, holdoutPercentage = 20%
        // Holdout range: 8000-9999
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 30.0, true);

        Boolean result = useCase.invoke(experiment, 9000);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should validate total percentages equal 100")
    void testThrowExceptionForInvalidPercentages() {
        // Total = 50 + 30 + 10 = 90 (not 100)
        Experiment experiment = new Experiment("exp-1", "dark-mode", 10.0, 50.0, 30.0, true);

        assertThrows(InvalidExperimentConfigurationException.class, () -> useCase.invoke(experiment, 5000));
    }

    @Test
    @DisplayName("Should throw exception when percentages sum above 100")
    void testThrowExceptionForPercentagesAbove100() {
        // Total = 50 + 40 + 20 = 110 (exceeds 100)
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 40.0, true);

        assertThrows(InvalidExperimentConfigurationException.class, () -> useCase.invoke(experiment, 5000));
    }

    @Test
    @DisplayName("Should allow 0% in any category")
    void testZeroPercentageAllowed() throws InvalidExperimentConfigurationException {
        // 100% enabled, 0% disabled, 0% holdout
        Experiment experiment = new Experiment("exp-1", "dark-mode", 0.0, 100.0, 0.0, true);

        Boolean result = useCase.invoke(experiment, 5000);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should work with all zero except holdout")
    void testOnlyHoldoutPercentage() throws InvalidExperimentConfigurationException {
        // 0% enabled, 0% disabled, 100% holdout
        Experiment experiment = new Experiment("exp-1", "dark-mode", 100.0, 0.0, 0.0, true);

        Boolean result = useCase.invoke(experiment, 5000);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should work with decimal percentages")
    void testDecimalPercentages() throws InvalidExperimentConfigurationException {
        // 33.33% + 33.33% + 33.34% = 100%
        Experiment experiment = new Experiment("exp-1", "dark-mode", 33.34, 33.33, 33.33, true);

        Boolean result1 = useCase.invoke(experiment, 1000);  // In enabled range
        Boolean result2 = useCase.invoke(experiment, 4000);  // In disabled range
        Boolean result3 = useCase.invoke(experiment, 8000);  // In holdout range

        assertTrue(result1);
        assertFalse(result2);
        assertFalse(result3);
    }

    @Test
    @DisplayName("Should handle hash bucket at boundary 0")
    void testHashBucketAtZeroBoundary() throws InvalidExperimentConfigurationException {
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 30.0, true);

        Boolean result = useCase.invoke(experiment, 0);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should handle hash bucket at max boundary 9999")
    void testHashBucketAtMaxBoundary() throws InvalidExperimentConfigurationException {
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 30.0, true);

        Boolean result = useCase.invoke(experiment, 9999);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should calculate enabled bucket boundary correctly")
    void testEnabledBucketBoundary() throws InvalidExperimentConfigurationException {
        // enabledPercentage = 40%, boundaries at 0-3999
        Experiment experiment = new Experiment("exp-1", "dark-mode", 30.0, 40.0, 30.0, true);

        Boolean result1 = useCase.invoke(experiment, 3999);  // Last enabled
        Boolean result2 = useCase.invoke(experiment, 4000);  // First disabled

        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    @DisplayName("Should calculate disabled bucket boundary correctly")
    void testDisabledBucketBoundary() throws InvalidExperimentConfigurationException {
        // enabledPercentage = 40%, disabledPercentage = 35%, disabled range = 4000-7499
        Experiment experiment = new Experiment("exp-1", "dark-mode", 25.0, 40.0, 35.0, true);

        Boolean result1 = useCase.invoke(experiment, 4000);   // First disabled
        Boolean result2 = useCase.invoke(experiment, 7499);   // Last disabled
        Boolean result3 = useCase.invoke(experiment, 7500);   // First holdout

        assertFalse(result1);
        assertFalse(result2);
        assertFalse(result3);
    }

    @Test
    @DisplayName("Should handle experiment with 100% enabled")
    void testAllEnabledPercentage() throws InvalidExperimentConfigurationException {
        Experiment experiment = new Experiment("exp-1", "dark-mode", 0.0, 100.0, 0.0, true);

        Boolean result0 = useCase.invoke(experiment, 0);
        Boolean result5000 = useCase.invoke(experiment, 5000);
        Boolean result9999 = useCase.invoke(experiment, 9999);

        assertTrue(result0);
        assertTrue(result5000);
        assertTrue(result9999);
    }

    @Test
    @DisplayName("Should handle experiment with 100% disabled")
    void testAllDisabledPercentage() throws InvalidExperimentConfigurationException {
        Experiment experiment = new Experiment("exp-1", "dark-mode", 0.0, 0.0, 100.0, true);

        Boolean result0 = useCase.invoke(experiment, 0);
        Boolean result5000 = useCase.invoke(experiment, 5000);
        Boolean result9999 = useCase.invoke(experiment, 9999);

        assertFalse(result0);
        assertFalse(result5000);
        assertFalse(result9999);
    }

    @Test
    @DisplayName("Should work with small percentages")
    void testSmallPercentages() throws InvalidExperimentConfigurationException {
        // 10% enabled, 1% disabled, 89% holdout
        Experiment experiment = new Experiment("exp-1", "dark-mode", 89.0, 10.0, 1.0, true);

        Boolean result = useCase.invoke(experiment, 500);  // In 10% enabled range

        assertTrue(result);
    }

    @Test
    @DisplayName("Should throw exception for null experiment")
    void testNullExperiment() {
        assertThrows(Exception.class, () -> useCase.invoke(null, 5000));
    }

    @Test
    @DisplayName("Should handle isActive flag (should not affect logic)")
    void testIsActiveFlagDoesNotAffectLogic() throws InvalidExperimentConfigurationException {
        Experiment activeExp = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 30.0, true);
        Experiment inactiveExp = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 30.0, false);

        Boolean activeResult = useCase.invoke(activeExp, 2500);
        Boolean inactiveResult = useCase.invoke(inactiveExp, 2500);

        assertEquals(activeResult, inactiveResult);
    }

    @Test
    @DisplayName("Should test multiple hash buckets for 50-30-20 split")
    void testMultipleHashBucketsForPercentageSplit() throws InvalidExperimentConfigurationException {
        Experiment experiment = new Experiment("exp-1", "dark-mode", 20.0, 50.0, 30.0, true);

        // Test enabled range (0-4999)
        for (int i = 0; i < 5000; i += 500) {
            assertTrue(useCase.invoke(experiment, i), "Bucket " + i + " should be enabled");
        }

        // Test disabled range (5000-7999)
        for (int i = 5000; i < 8000; i += 500) {
            assertFalse(useCase.invoke(experiment, i), "Bucket " + i + " should be disabled");
        }

        // Test holdout range (8000-9999)
        for (int i = 8000; i < 10000; i += 500) {
            assertFalse(useCase.invoke(experiment, i), "Bucket " + i + " should be holdout");
        }
    }
}
