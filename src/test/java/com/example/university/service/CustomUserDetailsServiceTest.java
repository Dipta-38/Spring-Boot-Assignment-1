package com.example.university.service;

import com.example.university.entity.Role;
import com.example.university.entity.User;
import com.example.university.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User studentUser;
    private User teacherUser;
    private User adminUser;
    private User disabledUser;

    @BeforeEach
    void setUp() {
        // Create test users
        studentUser = new User();
        studentUser.setId(1L);
        studentUser.setUsername("john.doe");
        studentUser.setPassword("password123");
        studentUser.setEmail("john@university.com");
        studentUser.setRole(Role.ROLE_STUDENT);
        studentUser.setEnabled(true);

        teacherUser = new User();
        teacherUser.setId(2L);
        teacherUser.setUsername("dr.smith");
        teacherUser.setPassword("password123");
        teacherUser.setEmail("smith@university.com");
        teacherUser.setRole(Role.ROLE_TEACHER);
        teacherUser.setEnabled(true);

        adminUser = new User();
        adminUser.setId(3L);
        adminUser.setUsername("admin");
        adminUser.setPassword("admin123");
        adminUser.setEmail("admin@university.com");
        adminUser.setRole(Role.ROLE_ADMIN);
        adminUser.setEnabled(true);

        disabledUser = new User();
        disabledUser.setId(4L);
        disabledUser.setUsername("disabled.user");
        disabledUser.setPassword("password123");
        disabledUser.setEmail("disabled@university.com");
        disabledUser.setRole(Role.ROLE_STUDENT);
        disabledUser.setEnabled(false);
    }

    @Test
    void loadUserByUsername_WithValidStudent_ShouldReturnUserDetails() {
        // Given
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(studentUser));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("john.doe");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("john.doe");
        assertThat(userDetails.getPassword()).isEqualTo("password123");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_STUDENT");
        assertThat(userDetails.isEnabled()).isTrue();

        verify(userRepository, times(1)).findByUsername("john.doe");
    }

    @Test
    void loadUserByUsername_WithValidTeacher_ShouldReturnUserDetails() {
        // Given
        when(userRepository.findByUsername("dr.smith")).thenReturn(Optional.of(teacherUser));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("dr.smith");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("dr.smith");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_TEACHER");

        verify(userRepository, times(1)).findByUsername("dr.smith");
    }

    @Test
    void loadUserByUsername_WithValidAdmin_ShouldReturnUserDetails() {
        // Given
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("admin");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");

        verify(userRepository, times(1)).findByUsername("admin");
    }

    @Test
    void loadUserByUsername_WithDisabledUser_ShouldReturnDisabledUserDetails() {
        // Given
        when(userRepository.findByUsername("disabled.user")).thenReturn(Optional.of(disabledUser));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("disabled.user");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("disabled.user");
        assertThat(userDetails.isEnabled()).isFalse();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();

        verify(userRepository, times(1)).findByUsername("disabled.user");
    }

    @Test
    void loadUserByUsername_WithNonExistentUsername_ShouldThrowException() {
        // Given
        String nonExistentUsername = "nonexistent";
        when(userRepository.findByUsername(nonExistentUsername)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(nonExistentUsername))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: " + nonExistentUsername);

        verify(userRepository, times(1)).findByUsername(nonExistentUsername);
    }

    @Test
    void loadUserByUsername_WithNullUsername_ShouldThrowException() {
        // Given
        when(userRepository.findByUsername(null)).thenThrow(new IllegalArgumentException("Username cannot be null"));

        // When & Then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void loadUserByUsername_WithEmptyUsername_ShouldThrowException() {
        // Given
        String emptyUsername = "";
        when(userRepository.findByUsername(emptyUsername)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(emptyUsername))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: ");

        verify(userRepository, times(1)).findByUsername(emptyUsername);
    }
}