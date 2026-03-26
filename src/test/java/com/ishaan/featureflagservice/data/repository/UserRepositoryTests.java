package com.ishaan.featureflagservice.data.repository;

import com.ishaan.featureflagservice.data.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserRepository Tests")
class UserRepositoryTests {

    private UserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new UserRepository();
    }

    @Test
    @DisplayName("Should return null for non-existent user")
    void testGetNonExistentUser() {
        User user = repository.getUser("999");

        assertNull(user);
    }

    @Test
    @DisplayName("Should return null for null user ID")
    void testGetNullUserId() {
        User user = repository.getUser(null);

        assertNull(user);
    }

    @Test
    @DisplayName("Should return null for empty user ID")
    void testGetEmptyUserId() {
        User user = repository.getUser("");

        assertNull(user);
    }

    @Test
    @DisplayName("Should handle user ID as string")
    void testUserIdAsString() {
        User user1 = repository.getUser("1");
        User user2 = repository.getUser("2");

        assertNotSame(user1, user2);
        assertNotEquals(user1.userId(), user2.userId());
    }

    @Test
    @DisplayName("Should return same user instance on multiple calls")
    void testUserInstanceConsistency() {
        User user1 = repository.getUser("1");
        User user2 = repository.getUser("1");

        assertEquals(user1, user2);
    }
}
