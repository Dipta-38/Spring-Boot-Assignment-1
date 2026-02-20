package com.example.university.controller;

import com.example.university.entity.*;
import com.example.university.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Model model;

    @InjectMocks
    private AuthController authController;

    @Test
    void home_ShouldRedirectToLogin() {
        assertEquals("redirect:/login", authController.home());
    }

    @Test
    void login_WithError_ShouldShowErrorMessage() {
        String viewName = authController.login("error", null, model);
        assertEquals("login", viewName);
        verify(model).addAttribute("error", "Invalid username or password!");
    }

    @Test
    void login_WithLogout_ShouldShowLogoutMessage() {
        String viewName = authController.login(null, "logout", model);
        assertEquals("login", viewName);
        verify(model).addAttribute("message", "You have been logged out successfully.");
    }

    @Test
    void showRegistrationForm_ShouldReturnRegisterView() {
        assertEquals("register", authController.showRegistrationForm(model));
    }

    @Test
    void registerStudent_WithValidData_ShouldRedirectToLogin() {
        String username = "john";
        String email = "john@test.com";
        String studentId = "S123";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(studentRepository.existsByStudentId(studentId)).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

        String result = authController.registerUser(username, email, "pass", "STUDENT",
                studentId, null, "John", "Doe", null, model);

        assertEquals("redirect:/login?success", result);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void registerTeacher_WithValidData_ShouldRedirectToLogin() {
        String username = "jane";
        String email = "jane@test.com";
        String teacherId = "T123";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(teacherRepository.existsByTeacherId(teacherId)).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(teacherRepository.save(any(Teacher.class))).thenAnswer(i -> i.getArgument(0));

        String result = authController.registerUser(username, email, "pass", "TEACHER",
                null, teacherId, "Jane", "Smith", "PhD", model);

        assertEquals("redirect:/login?success", result);
        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    void registerAdmin_WithValidData_ShouldRedirectToLogin() {
        String username = "admin";
        String email = "admin@test.com";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        String result = authController.registerUser(username, email, "pass", "ADMIN",
                null, null, null, null, null, model);

        assertEquals("redirect:/login?success", result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_WithExistingUsername_ShouldShowError() {
        when(userRepository.existsByUsername("john")).thenReturn(true);

        String result = authController.registerUser("john", "john@test.com", "pass", "STUDENT",
                "S123", null, "John", "Doe", null, model);

        assertEquals("register", result);
        verify(model).addAttribute("error", "Username already exists");
    }

    @Test
    void registerStudent_WithoutStudentId_ShouldShowError() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);

        String result = authController.registerUser("john", "john@test.com", "pass", "STUDENT",
                null, null, "John", "Doe", null, model);

        assertEquals("register", result);
        verify(model).addAttribute("error", "Student ID is required");
    }

    @Test
    void register_WithInvalidRole_ShouldShowError() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);

        String result = authController.registerUser("john", "john@test.com", "pass", "INVALID",
                null, null, null, null, null, model);

        assertEquals("register", result);
        verify(model).addAttribute("error", "Invalid role selected");
    }
}