package com.example.university.controller;

import com.example.university.entity.Course;
import com.example.university.entity.Student;
import com.example.university.service.CourseService;
import com.example.university.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @Mock
    private CourseService courseService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private StudentController studentController;

    private final String testUsername = "student123";
    private final Long studentId = 1L;
    private Student testStudent;
    private List<Course> enrolledCourses;
    private List<Course> availableCourses;

    @Test
    void studentDashboard() {
        // Setup
        testStudent = new Student();
        testStudent.setId(studentId);
        testStudent.setUsername(testUsername);

        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("Math 101");

        Course course2 = new Course();
        course2.setId(2L);
        course2.setName("Physics 101");

        enrolledCourses = Arrays.asList(course1, course2);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(studentService.getStudentByUsername(testUsername)).thenReturn(testStudent);
        when(studentService.getEnrolledCourses(studentId)).thenReturn(enrolledCourses);

        // Act
        String viewName = studentController.studentDashboard(model);

        // Assert
        assertEquals("student/dashboard", viewName);
        verify(model).addAttribute("student", testStudent);
        verify(model).addAttribute("enrolledCourses", enrolledCourses);
    }

    @Test
    void listCourses() {
        // Setup
        testStudent = new Student();
        testStudent.setId(studentId);
        testStudent.setUsername(testUsername);

        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("Math 101");

        Course course2 = new Course();
        course2.setId(2L);
        course2.setName("Physics 101");

        Course course3 = new Course();
        course3.setId(3L);
        course3.setName("Chemistry 101");

        enrolledCourses = Arrays.asList(course1, course2);
        availableCourses = Arrays.asList(course3);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(studentService.getStudentByUsername(testUsername)).thenReturn(testStudent);
        when(studentService.getAvailableCourses(studentId)).thenReturn(availableCourses);
        when(studentService.getEnrolledCourses(studentId)).thenReturn(enrolledCourses);

        // Act
        String viewName = studentController.listCourses(model);

        // Assert
        assertEquals("student/courses", viewName);
        verify(model).addAttribute("availableCourses", availableCourses);
        verify(model).addAttribute("enrolledCourses", enrolledCourses);
        verify(model).addAttribute("student", testStudent);
    }

    @Test
    void enrollInCourse() {
        // Setup
        Long courseId = 1L;
        testStudent = new Student();
        testStudent.setId(studentId);
        testStudent.setUsername(testUsername);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(studentService.getStudentByUsername(testUsername)).thenReturn(testStudent);

        // Act
        String viewName = studentController.enrollInCourse(courseId);

        // Assert
        assertEquals("redirect:/student/courses", viewName);
        verify(studentService).enrollInCourse(studentId, courseId);
    }

    @Test
    void unenrollFromCourse() {
        // Setup
        Long courseId = 1L;
        testStudent = new Student();
        testStudent.setId(studentId);
        testStudent.setUsername(testUsername);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(studentService.getStudentByUsername(testUsername)).thenReturn(testStudent);

        // Act
        String viewName = studentController.unenrollFromCourse(courseId);

        // Assert
        assertEquals("redirect:/student/courses", viewName);
        verify(studentService).unenrollFromCourse(studentId, courseId);
    }

    @Test
    void viewProfile() {
        // Setup
        testStudent = new Student();
        testStudent.setId(studentId);
        testStudent.setUsername(testUsername);

        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("Math 101");

        enrolledCourses = Arrays.asList(course1);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(studentService.getStudentByUsername(testUsername)).thenReturn(testStudent);
        when(studentService.getEnrolledCourses(studentId)).thenReturn(enrolledCourses);

        // Act
        String viewName = studentController.viewProfile(model);

        // Assert
        assertEquals("student/profile", viewName);
        verify(model).addAttribute("student", testStudent);
        verify(model).addAttribute("enrolledCourses", enrolledCourses);
    }

    @Test
    void editProfileForm() {
        // Setup
        testStudent = new Student();
        testStudent.setId(studentId);
        testStudent.setUsername(testUsername);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(studentService.getStudentByUsername(testUsername)).thenReturn(testStudent);

        // Act
        String viewName = studentController.editProfileForm(model);

        // Assert
        assertEquals("student/edit-profile", viewName);
        verify(model).addAttribute("student", testStudent);
    }

    @Test
    void updateProfile_success() {
        // Setup
        testStudent = new Student();
        testStudent.setId(studentId);
        testStudent.setUsername(testUsername);
        testStudent.setPassword("encodedCurrentPassword");

        Student updatedStudent = new Student();
        updatedStudent.setFirstName("John");
        updatedStudent.setLastName("Doe");
        updatedStudent.setEmail("john.doe@example.com");

        String currentPassword = "currentPass123";
        String newPassword = "newPass123";
        String encodedNewPassword = "encodedNewPass123";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(studentService.getStudentByUsername(testUsername)).thenReturn(testStudent);
        when(passwordEncoder.matches(currentPassword, testStudent.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

        // Act
        String viewName = studentController.updateProfile(updatedStudent, newPassword, currentPassword, model);

        // Assert
        assertEquals("redirect:/student/profile?success", viewName);
        verify(studentService).updateStudent(testStudent);
        verify(passwordEncoder).encode(newPassword);
        assertEquals("John", testStudent.getFirstName());
        assertEquals("Doe", testStudent.getLastName());
        assertEquals("john.doe@example.com", testStudent.getEmail());
        assertEquals(encodedNewPassword, testStudent.getPassword());
    }

    @Test
    void updateProfile_withoutPasswordChange() {
        // Setup
        testStudent = new Student();
        testStudent.setId(studentId);
        testStudent.setUsername(testUsername);
        testStudent.setPassword("encodedCurrentPassword");

        Student updatedStudent = new Student();
        updatedStudent.setFirstName("John");
        updatedStudent.setLastName("Doe");
        updatedStudent.setEmail("john.doe@example.com");

        String currentPassword = "currentPass123";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(studentService.getStudentByUsername(testUsername)).thenReturn(testStudent);
        when(passwordEncoder.matches(currentPassword, testStudent.getPassword())).thenReturn(true);

        // Act
        String viewName = studentController.updateProfile(updatedStudent, null, currentPassword, model);

        // Assert
        assertEquals("redirect:/student/profile?success", viewName);
        verify(studentService).updateStudent(testStudent);
        verify(passwordEncoder, never()).encode(anyString());
        assertEquals("John", testStudent.getFirstName());
        assertEquals("Doe", testStudent.getLastName());
        assertEquals("john.doe@example.com", testStudent.getEmail());
        assertEquals("encodedCurrentPassword", testStudent.getPassword());
    }

    @Test
    void updateProfile_withEmptyNewPassword() {
        // Setup
        testStudent = new Student();
        testStudent.setId(studentId);
        testStudent.setUsername(testUsername);
        testStudent.setPassword("encodedCurrentPassword");

        Student updatedStudent = new Student();
        updatedStudent.setFirstName("John");
        updatedStudent.setLastName("Doe");
        updatedStudent.setEmail("john.doe@example.com");

        String currentPassword = "currentPass123";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(studentService.getStudentByUsername(testUsername)).thenReturn(testStudent);
        when(passwordEncoder.matches(currentPassword, testStudent.getPassword())).thenReturn(true);

        // Act
        String viewName = studentController.updateProfile(updatedStudent, "", currentPassword, model);

        // Assert
        assertEquals("redirect:/student/profile?success", viewName);
        verify(studentService).updateStudent(testStudent);
        verify(passwordEncoder, never()).encode(anyString());
        assertEquals("John", testStudent.getFirstName());
        assertEquals("Doe", testStudent.getLastName());
        assertEquals("john.doe@example.com", testStudent.getEmail());
        assertEquals("encodedCurrentPassword", testStudent.getPassword());
    }

    @Test
    void updateProfile_incorrectCurrentPassword() {
        // Setup
        testStudent = new Student();
        testStudent.setId(studentId);
        testStudent.setUsername(testUsername);
        testStudent.setPassword("encodedCurrentPassword");

        Student updatedStudent = new Student();
        updatedStudent.setFirstName("John");
        updatedStudent.setLastName("Doe");
        updatedStudent.setEmail("john.doe@example.com");

        String currentPassword = "wrongPassword";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(studentService.getStudentByUsername(testUsername)).thenReturn(testStudent);
        when(passwordEncoder.matches(currentPassword, testStudent.getPassword())).thenReturn(false);

        // Act
        String viewName = studentController.updateProfile(updatedStudent, null, currentPassword, model);

        // Assert
        assertEquals("student/edit-profile", viewName);
        verify(model).addAttribute("error", "Current password is incorrect");
        verify(model).addAttribute("student", testStudent);
        verify(studentService, never()).updateStudent(any());
    }

    @Test
    void updateProfile_exception() {
        // Setup
        testStudent = new Student();
        testStudent.setId(studentId);
        testStudent.setUsername(testUsername);
        testStudent.setPassword("encodedCurrentPassword");

        Student updatedStudent = new Student();
        updatedStudent.setFirstName("John");
        updatedStudent.setLastName("Doe");
        updatedStudent.setEmail("john.doe@example.com");

        String currentPassword = "currentPass123";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(studentService.getStudentByUsername(testUsername)).thenReturn(testStudent);
        when(passwordEncoder.matches(currentPassword, testStudent.getPassword())).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(studentService).updateStudent(any());

        // Act
        String viewName = studentController.updateProfile(updatedStudent, null, currentPassword, model);

        // Assert
        assertEquals("student/edit-profile", viewName);
        verify(model).addAttribute(eq("error"), contains("Error updating profile"));
    }
}