package com.example.university.repository;

import com.example.university.entity.Role;
import com.example.university.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsername() {
        String uniqueUsername = "user" + System.currentTimeMillis();

        User user = new User();
        user.setUsername(uniqueUsername);
        user.setEmail(uniqueUsername + "@test.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_STUDENT);
        user.setEnabled(true);
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername(uniqueUsername);
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(uniqueUsername);
    }

    @Test
    void testFindByEmail() {
        String uniqueUsername = "user" + System.currentTimeMillis();
        String uniqueEmail = uniqueUsername + "@test.com";

        User user = new User();
        user.setUsername(uniqueUsername);
        user.setEmail(uniqueEmail);
        user.setPassword("password");
        user.setRole(Role.ROLE_STUDENT);
        user.setEnabled(true);
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail(uniqueEmail);
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(uniqueEmail);
    }

    @Test
    void testExistsByUsername() {
        String uniqueUsername = "user" + System.currentTimeMillis();

        User user = new User();
        user.setUsername(uniqueUsername);
        user.setEmail(uniqueUsername + "@test.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_STUDENT);
        user.setEnabled(true);
        userRepository.save(user);

        boolean exists = userRepository.existsByUsername(uniqueUsername);
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByEmail() {
        String uniqueUsername = "user" + System.currentTimeMillis();
        String uniqueEmail = uniqueUsername + "@test.com";

        User user = new User();
        user.setUsername(uniqueUsername);
        user.setEmail(uniqueEmail);
        user.setPassword("password");
        user.setRole(Role.ROLE_STUDENT);
        user.setEnabled(true);
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail(uniqueEmail);
        assertThat(exists).isTrue();
    }
}