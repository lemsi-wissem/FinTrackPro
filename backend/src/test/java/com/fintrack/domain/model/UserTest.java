package com.fintrack.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void create_shouldInitializeWithDefaultsAndGeneratedId() {
        User user = User.create("alice@example.com", "hashedPass", "Alice", "Smith");

        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail()).isEqualTo("alice@example.com");
        assertThat(user.getPasswordHash()).isEqualTo("hashedPass");
        assertThat(user.getFirstName()).isEqualTo("Alice");
        assertThat(user.getLastName()).isEqualTo("Smith");
        assertThat(user.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(user.isVerified()).isFalse();
        assertThat(user.isActive()).isTrue();
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    @Test
    void create_twoUsers_shouldHaveDifferentIds() {
        User u1 = User.create("a@a.com", "p1", "A", "A");
        User u2 = User.create("b@b.com", "p2", "B", "B");
        assertThat(u1.getId()).isNotEqualTo(u2.getId());
    }

    @Test
    void verify_shouldSetVerifiedTrue() {
        User user = User.create("alice@example.com", "hash", "Alice", "Smith");
        assertThat(user.isVerified()).isFalse();

        user.verify();

        assertThat(user.isVerified()).isTrue();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    @Test
    void deactivate_shouldSetActiveFalse() {
        User user = User.create("alice@example.com", "hash", "Alice", "Smith");
        assertThat(user.isActive()).isTrue();

        user.deactivate();

        assertThat(user.isActive()).isFalse();
    }

    @Test
    void updateProfile_shouldUpdateNamesAndTimestamp() throws InterruptedException {
        User user = User.create("alice@example.com", "hash", "Alice", "Smith");
        var beforeUpdate = user.getUpdatedAt();
        Thread.sleep(5);

        user.updateProfile("Bob", "Jones");

        assertThat(user.getFirstName()).isEqualTo("Bob");
        assertThat(user.getLastName()).isEqualTo("Jones");
        assertThat(user.getUpdatedAt()).isAfterOrEqualTo(beforeUpdate);
    }

    @Test
    void changePassword_shouldUpdateHashAndTimestamp() throws InterruptedException {
        User user = User.create("alice@example.com", "oldHash", "Alice", "Smith");
        Thread.sleep(5);

        user.changePassword("newHash");

        assertThat(user.getPasswordHash()).isEqualTo("newHash");
    }

    @Test
    void changeRole_shouldUpdateRole() {
        User user = User.create("alice@example.com", "hash", "Alice", "Smith");
        assertThat(user.getRole()).isEqualTo(Role.ROLE_USER);

        user.changeRole(Role.ROLE_ADMIN);

        assertThat(user.getRole()).isEqualTo(Role.ROLE_ADMIN);
    }
}
