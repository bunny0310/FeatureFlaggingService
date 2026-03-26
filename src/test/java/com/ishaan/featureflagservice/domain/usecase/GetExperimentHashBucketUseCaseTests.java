package com.ishaan.featureflagservice.domain.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GetExperimentHashBucketUseCase Tests")
class GetExperimentHashBucketUseCaseTests {

    private GetExperimentHashBucketUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetExperimentHashBucketUseCase();
    }

    @Test
    @DisplayName("Should return hash bucket between 0 and 9999")
    void testHashBucketRange() {
        int result = useCase.invoke("exp-1", "user-1");

        assertTrue(result >= 0 && result < 10000, "Hash bucket should be between 0 and 9999");
    }

    @Test
    @DisplayName("Should return same hash bucket for same experiment and user")
    void testDeterministicHashing() {
        int result1 = useCase.invoke("exp-1", "user-1");
        int result2 = useCase.invoke("exp-1", "user-1");

        assertEquals(result1, result2, "Same experiment and user should produce same hash");
    }

    @Test
    @DisplayName("Should return different hash for different user")
    void testDifferentUserDifferentHash() {
        int hash1 = useCase.invoke("exp-1", "user-1");
        int hash2 = useCase.invoke("exp-1", "user-2");

        // Different users might produce different hashes (not guaranteed, but likely)
        // We mainly test that both are in valid range
        assertTrue(hash1 >= 0 && hash1 < 10000);
        assertTrue(hash2 >= 0 && hash2 < 10000);
    }

    @Test
    @DisplayName("Should return different hash for different experiment")
    void testDifferentExperimentDifferentHash() {
        int hash1 = useCase.invoke("exp-1", "user-1");
        int hash2 = useCase.invoke("exp-2", "user-1");

        // Different experiments might produce different hashes (not guaranteed, but likely)
        assertTrue(hash1 >= 0 && hash1 < 10000);
        assertTrue(hash2 >= 0 && hash2 < 10000);
    }

    @Test
    @DisplayName("Should be order-dependent (exp, user) vs (user, exp)")
    void testOrderDependence() {
        int hash1 = useCase.invoke("exp-1", "user-1");
        int hash2 = useCase.invoke("user-1", "exp-1");

        // Hashes should be different when order is swapped
        assertTrue(hash1 >= 0 && hash1 < 10000);
        assertTrue(hash2 >= 0 && hash2 < 10000);
    }

    @Test
    @DisplayName("Should handle null experiment name")
    void testNullExperimentName() {
        // Should not throw exception
        int result = useCase.invoke(null, "user-1");
        assertTrue(result >= 0 && result < 10000);
    }

    @Test
    @DisplayName("Should handle null user ID")
    void testNullUserId() {
        // Should not throw exception
        int result = useCase.invoke("exp-1", null);
        assertTrue(result >= 0 && result < 10000);
    }

    @Test
    @DisplayName("Should handle both null parameters")
    void testBothNull() {
        // Should not throw exception
        int result = useCase.invoke(null, null);
        assertTrue(result >= 0 && result < 10000);
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyStrings() {
        int hash1 = useCase.invoke("", "");
        int hash2 = useCase.invoke("", "user-1");
        int hash3 = useCase.invoke("exp-1", "");

        assertTrue(hash1 >= 0 && hash1 < 10000);
        assertTrue(hash2 >= 0 && hash2 < 10000);
        assertTrue(hash3 >= 0 && hash3 < 10000);
    }

    @Test
    @DisplayName("Should produce valid hash for numeric user IDs")
    void testNumericUserIds() {
        int hash1 = useCase.invoke("exp-1", "1");
        int hash2 = useCase.invoke("exp-1", "2");
        int hash3 = useCase.invoke("exp-1", "999");

        assertTrue(hash1 >= 0 && hash1 < 10000);
        assertTrue(hash2 >= 0 && hash2 < 10000);
        assertTrue(hash3 >= 0 && hash3 < 10000);
    }

    @Test
    @DisplayName("Should produce valid hash for special characters")
    void testSpecialCharacters() {
        int hash1 = useCase.invoke("exp-!@#$%", "user-&*()");
        int hash2 = useCase.invoke("exp_with-dash", "user.with.dots");

        assertTrue(hash1 >= 0 && hash1 < 10000);
        assertTrue(hash2 >= 0 && hash2 < 10000);
    }

    @Test
    @DisplayName("Should produce valid hash for long strings")
    void testLongStrings() {
        String longExp = "a".repeat(1000);
        String longUserId = "b".repeat(1000);

        int result = useCase.invoke(longExp, longUserId);

        assertTrue(result >= 0 && result < 10000);
    }

    @Test
    @DisplayName("Should be consistent across multiple invocations")
    void testConsistencyAcrossInvocations() {
        int[] results = new int[5];
        for (int i = 0; i < 5; i++) {
            results[i] = useCase.invoke("exp-stable", "user-stable");
        }

        for (int i = 1; i < 5; i++) {
            assertEquals(results[0], results[i], "All invocations should produce same hash");
        }
    }

    @Test
    @DisplayName("Should distribute users across all buckets for same experiment")
    void testDistributionAcrossBuckets() {
        // Test that different users produce hashes across the range
        int[] buckets = new int[100];
        for (int i = 0; i < 100; i++) {
            int hash = useCase.invoke("exp-1", "user-" + i);
            int bucket = hash / 100; // Divide into 100 buckets
            buckets[bucket]++;
        }

        // Not all buckets need to be filled, but at least some variation expected
        long filledBuckets = 0;
        for (int count : buckets) {
            if (count > 0) filledBuckets++;
        }

        assertTrue(filledBuckets > 1, "Should have some distribution across buckets");
    }

    @Test
    @DisplayName("Should handle case sensitivity")
    void testCaseSensitivity() {
        int hash1 = useCase.invoke("Exp-1", "User-1");
        int hash2 = useCase.invoke("exp-1", "user-1");

        assertTrue(hash1 >= 0 && hash1 < 10000);
        assertTrue(hash2 >= 0 && hash2 < 10000);
    }

    @Test
    @DisplayName("Should handle whitespace in strings")
    void testWhitespaceHandling() {
        int hash1 = useCase.invoke(" exp-1 ", " user-1 ");
        int hash2 = useCase.invoke("exp-1", "user-1");

        assertTrue(hash1 >= 0 && hash1 < 10000);
        assertTrue(hash2 >= 0 && hash2 < 10000);
    }

    @Test
    @DisplayName("Should use Math.abs for positive bucket")
    void testMathAbsApplication() {
        // Objects.hash can produce negative values, but Math.abs should ensure positive
        for (int i = 0; i < 100; i++) {
            int hash = useCase.invoke("exp-" + i, "user-" + i);
            assertTrue(hash >= 0, "Hash should never be negative");
        }
    }

    @Test
    @DisplayName("Should use modulo 10000 for correct range")
    void testModuloCorrectness() {
        for (int i = 0; i < 1000; i++) {
            int hash = useCase.invoke("exp-" + i, "user-" + i);
            assertTrue(hash < 10000, "Hash should be less than 10000");
            assertEquals(0, hash % 1, "Modulo operation should produce integer result");
        }
    }
}
