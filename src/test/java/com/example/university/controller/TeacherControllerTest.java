package com.example.university.controller;

import com.example.university.entity.Course;
import com.example.university.entity.Department;
import com.example.university.entity.Teacher;
import com.example.university.service.CourseService;
import com.example.university.service.DepartmentService;
import com.example.university.service.TeacherService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherControllerTest {

    @Mock
    private CourseService courseService;

    @Mock
    private TeacherService teacherService;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private TeacherController teacherController;

    private final String testUsername = "teacher123";
    private final Long teacherId = 1L;
    private final Long courseId = 1L;
    private final Long departmentId = 1L;
    private Teacher testTeacher;
    private Course testCourse;
    private Department testDepartment;
    private List<Course> teacherCourses;
    private List<Department> departments;

    @Test
    void teacherDashboard() {
        // Setup
        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("Math 101");

        Course course2 = new Course();
        course2.setId(2L);
        course2.setName("Physics 101");

        teacherCourses = Arrays.asList(course1, course2);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(courseService.getCoursesByTeacher(teacherId)).thenReturn(teacherCourses);

        // Act
        String viewName = teacherController.teacherDashboard(model);

        // Assert
        assertEquals("teacher/dashboard", viewName);
        verify(model).addAttribute("teacher", testTeacher);
        verify(model).addAttribute("courses", teacherCourses);
    }

    @Test
    void listCourses() {
        // Setup
        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("Math 101");

        teacherCourses = Arrays.asList(course1);

        testDepartment = new Department();
        testDepartment.setId(departmentId);
        testDepartment.setName("Computer Science");

        departments = Arrays.asList(testDepartment);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(courseService.getCoursesByTeacher(teacherId)).thenReturn(teacherCourses);
        when(departmentService.getAllDepartments()).thenReturn(departments);

        // Act
        String viewName = teacherController.listCourses(model);

        // Assert
        assertEquals("teacher/courses", viewName);
        verify(model).addAttribute("courses", teacherCourses);
        verify(model).addAttribute("teacher", testTeacher);
        verify(model).addAttribute("departments", departments);
        verify(model).addAttribute(eq("course"), any(Course.class));
    }

    @Test
    void createCourse_success() {
        // Setup
        Course newCourse = new Course();
        newCourse.setName("Data Structures");
        newCourse.setCode("CS201");
        newCourse.setCredits(3);

        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);

        // Act
        String viewName = teacherController.createCourse(newCourse, departmentId, model);

        // Assert
        assertEquals("redirect:/teacher/courses?success", viewName);
        verify(courseService).createCourse(newCourse, departmentId, teacherId);
    }

    @Test
    void createCourse_missingDepartment() {
        // Setup
        Course newCourse = new Course();
        newCourse.setName("Data Structures");

        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(courseService.getCoursesByTeacher(teacherId)).thenReturn(new ArrayList<>());
        when(departmentService.getAllDepartments()).thenReturn(new ArrayList<>());

        // Act
        String viewName = teacherController.createCourse(newCourse, null, model);

        // Assert
        assertEquals("teacher/courses", viewName);
        verify(model).addAttribute("error", "Please select a department for the course");
        verify(courseService, never()).createCourse(any(), any(), any());
    }

    @Test
    void createCourse_missingCourseName() {
        // Setup
        Course newCourse = new Course();
        newCourse.setName("");

        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(courseService.getCoursesByTeacher(teacherId)).thenReturn(new ArrayList<>());
        when(departmentService.getAllDepartments()).thenReturn(new ArrayList<>());

        // Act
        String viewName = teacherController.createCourse(newCourse, departmentId, model);

        // Assert
        assertEquals("teacher/courses", viewName);
        verify(model).addAttribute("error", "Course name is required");
        verify(courseService, never()).createCourse(any(), any(), any());
    }

    @Test
    void createCourse_exception() {
        // Setup
        Course newCourse = new Course();
        newCourse.setName("Data Structures");

        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        doThrow(new RuntimeException("Database error")).when(courseService).createCourse(any(), any(), any());
        when(courseService.getCoursesByTeacher(teacherId)).thenReturn(new ArrayList<>());
        when(departmentService.getAllDepartments()).thenReturn(new ArrayList<>());

        // Act
        String viewName = teacherController.createCourse(newCourse, departmentId, model);

        // Assert
        assertEquals("teacher/courses", viewName);
        verify(model).addAttribute(eq("error"), contains("Error creating course"));
    }

    @Test
    void editCourseForm_success() {
        // Setup
        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        testDepartment = new Department();
        testDepartment.setId(departmentId);

        testCourse = new Course();
        testCourse.setId(courseId);
        testCourse.setTeacher(testTeacher);
        testCourse.setDepartment(testDepartment);

        departments = Arrays.asList(testDepartment);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(courseService.getCourseById(courseId)).thenReturn(testCourse);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(departmentService.getAllDepartments()).thenReturn(departments);

        // Act
        String viewName = teacherController.editCourseForm(courseId, model);

        // Assert
        assertEquals("teacher/edit-course", viewName);
        verify(model).addAttribute("course", testCourse);
        verify(model).addAttribute("teacher", testTeacher);
        verify(model).addAttribute("departments", departments);
    }

    @Test
    void editCourseForm_notOwnCourse() {
        // Setup
        Teacher otherTeacher = new Teacher();
        otherTeacher.setId(2L);

        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        testCourse = new Course();
        testCourse.setId(courseId);
        testCourse.setTeacher(otherTeacher);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(courseService.getCourseById(courseId)).thenReturn(testCourse);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(courseService.getCoursesByTeacher(teacherId)).thenReturn(new ArrayList<>());
        when(departmentService.getAllDepartments()).thenReturn(new ArrayList<>());

        // Act
        String viewName = teacherController.editCourseForm(courseId, model);

        // Assert
        assertEquals("teacher/courses", viewName);
        verify(model).addAttribute("error", "You can only edit your own courses");
    }

    @Test
    void updateCourse_success() {
        // Setup
        Course updatedCourse = new Course();
        updatedCourse.setName("Advanced Data Structures");
        updatedCourse.setCode("CS301");
        updatedCourse.setCredits(4);

        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        testCourse = new Course();
        testCourse.setId(courseId);
        testCourse.setTeacher(testTeacher);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(courseService.getCourseById(courseId)).thenReturn(testCourse);

        // Act
        String viewName = teacherController.updateCourse(courseId, updatedCourse, departmentId, model);

        // Assert
        assertEquals("redirect:/teacher/courses?success", viewName);
        verify(courseService).updateCourse(courseId, updatedCourse, departmentId, teacherId);
    }

    @Test
    void updateCourse_notOwnCourse() {
        // Setup
        Teacher otherTeacher = new Teacher();
        otherTeacher.setId(2L);

        Course updatedCourse = new Course();
        updatedCourse.setName("Advanced Data Structures");

        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        testCourse = new Course();
        testCourse.setId(courseId);
        testCourse.setTeacher(otherTeacher);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(courseService.getCourseById(courseId)).thenReturn(testCourse);
        when(courseService.getCoursesByTeacher(teacherId)).thenReturn(new ArrayList<>());
        when(departmentService.getAllDepartments()).thenReturn(new ArrayList<>());

        // Act
        String viewName = teacherController.updateCourse(courseId, updatedCourse, departmentId, model);

        // Assert
        assertEquals("teacher/courses", viewName);
        verify(model).addAttribute("error", "You can only edit your own courses");
        verify(courseService, never()).updateCourse(any(), any(), any(), any());
    }

    @Test
    void updateCourse_missingDepartment() {
        // Setup
        Course updatedCourse = new Course();
        updatedCourse.setName("Advanced Data Structures");

        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        testCourse = new Course();
        testCourse.setId(courseId);
        testCourse.setTeacher(testTeacher);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(courseService.getCourseById(courseId)).thenReturn(testCourse);
        when(courseService.getCoursesByTeacher(teacherId)).thenReturn(new ArrayList<>());
        when(departmentService.getAllDepartments()).thenReturn(new ArrayList<>());

        // Act
        String viewName = teacherController.updateCourse(courseId, updatedCourse, null, model);

        // Assert
        assertEquals("teacher/courses", viewName);
        verify(model).addAttribute("error", "Please select a department for the course");
        verify(courseService, never()).updateCourse(any(), any(), any(), any());
    }

    @Test
    void updateCourse_missingCourseName() {
        // Setup
        Course updatedCourse = new Course();
        updatedCourse.setName("");

        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        testCourse = new Course();
        testCourse.setId(courseId);
        testCourse.setTeacher(testTeacher);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(courseService.getCourseById(courseId)).thenReturn(testCourse);
        when(courseService.getCoursesByTeacher(teacherId)).thenReturn(new ArrayList<>());
        when(departmentService.getAllDepartments()).thenReturn(new ArrayList<>());

        // Act
        String viewName = teacherController.updateCourse(courseId, updatedCourse, departmentId, model);

        // Assert
        assertEquals("teacher/courses", viewName);
        verify(model).addAttribute("error", "Course name is required");
        verify(courseService, never()).updateCourse(any(), any(), any(), any());
    }

    @Test
    void deleteCourse_success() {
        // Setup
        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        testCourse = new Course();
        testCourse.setId(courseId);
        testCourse.setTeacher(testTeacher);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(courseService.getCourseById(courseId)).thenReturn(testCourse);

        // Act
        String viewName = teacherController.deleteCourse(courseId, model);

        // Assert
        assertEquals("redirect:/teacher/courses?deleted", viewName);
        verify(courseService).deleteCourse(courseId);
    }

    @Test
    void deleteCourse_notOwnCourse() {
        // Setup
        Teacher otherTeacher = new Teacher();
        otherTeacher.setId(2L);

        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        testCourse = new Course();
        testCourse.setId(courseId);
        testCourse.setTeacher(otherTeacher);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(courseService.getCourseById(courseId)).thenReturn(testCourse);
        when(courseService.getCoursesByTeacher(teacherId)).thenReturn(new ArrayList<>());
        when(departmentService.getAllDepartments()).thenReturn(new ArrayList<>());

        // Act
        String viewName = teacherController.deleteCourse(courseId, model);

        // Assert
        assertEquals("teacher/courses", viewName);
        verify(model).addAttribute("error", "You can only delete your own courses");
        verify(courseService, never()).deleteCourse(any());
    }

    @Test
    void viewCourseStudents_success() {
        // Setup
        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        testCourse = new Course();
        testCourse.setId(courseId);
        testCourse.setTeacher(testTeacher);
        testCourse.setStudents(new HashSet<>());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(courseService.getCourseById(courseId)).thenReturn(testCourse);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);

        // Act
        String viewName = teacherController.viewCourseStudents(courseId, model);

        // Assert
        assertEquals("teacher/course-students", viewName);
        verify(model).addAttribute("course", testCourse);
        verify(model).addAttribute("students", testCourse.getStudents());
        verify(model).addAttribute("teacher", testTeacher);
    }

    @Test
    void viewCourseStudents_notOwnCourse() {
        // Setup
        Teacher otherTeacher = new Teacher();
        otherTeacher.setId(2L);

        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        testCourse = new Course();
        testCourse.setId(courseId);
        testCourse.setTeacher(otherTeacher);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(courseService.getCourseById(courseId)).thenReturn(testCourse);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(courseService.getCoursesByTeacher(teacherId)).thenReturn(new ArrayList<>());
        when(departmentService.getAllDepartments()).thenReturn(new ArrayList<>());

        // Act
        String viewName = teacherController.viewCourseStudents(courseId, model);

        // Assert
        assertEquals("teacher/courses", viewName);
        verify(model).addAttribute("error", "You can only view students in your own courses");
    }

    @Test
    void viewProfile() {
        // Setup
        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("Math 101");

        teacherCourses = Arrays.asList(course1);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(courseService.getCoursesByTeacher(teacherId)).thenReturn(teacherCourses);

        // Act
        String viewName = teacherController.viewProfile(model);

        // Assert
        assertEquals("teacher/profile", viewName);
        verify(model).addAttribute("teacher", testTeacher);
        verify(model).addAttribute("courses", teacherCourses);
    }

    @Test
    void editProfileForm() {
        // Setup
        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);

        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("Math 101");

        teacherCourses = Arrays.asList(course1);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(courseService.getCoursesByTeacher(teacherId)).thenReturn(teacherCourses);

        // Act
        String viewName = teacherController.editProfileForm(model);

        // Assert
        assertEquals("teacher/edit-profile", viewName);
        verify(model).addAttribute("teacher", testTeacher);
        verify(model).addAttribute("courses", teacherCourses);
    }

    @Test
    void updateProfile_success() {
        // Setup
        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);
        testTeacher.setPassword("encodedCurrentPassword");

        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setFirstName("John");
        updatedTeacher.setLastName("Doe");
        updatedTeacher.setEmail("john.doe@example.com");
        updatedTeacher.setQualification("PhD");

        String currentPassword = "currentPass123";
        String newPassword = "newPass123";
        String encodedNewPassword = "encodedNewPass123";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(passwordEncoder.matches(currentPassword, testTeacher.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

        // Act
        String viewName = teacherController.updateProfile(updatedTeacher, newPassword, currentPassword, model);

        // Assert
        assertEquals("redirect:/teacher/profile?success", viewName);
        verify(teacherService).updateTeacher(testTeacher);
        verify(passwordEncoder).encode(newPassword);
        assertEquals("John", testTeacher.getFirstName());
        assertEquals("Doe", testTeacher.getLastName());
        assertEquals("john.doe@example.com", testTeacher.getEmail());
        assertEquals("PhD", testTeacher.getQualification());
        assertEquals(encodedNewPassword, testTeacher.getPassword());
    }

    @Test
    void updateProfile_withoutPasswordChange() {
        // Setup
        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);
        testTeacher.setPassword("encodedCurrentPassword");

        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setFirstName("John");
        updatedTeacher.setLastName("Doe");
        updatedTeacher.setEmail("john.doe@example.com");
        updatedTeacher.setQualification("PhD");

        String currentPassword = "currentPass123";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(passwordEncoder.matches(currentPassword, testTeacher.getPassword())).thenReturn(true);

        // Act
        String viewName = teacherController.updateProfile(updatedTeacher, null, currentPassword, model);

        // Assert
        assertEquals("redirect:/teacher/profile?success", viewName);
        verify(teacherService).updateTeacher(testTeacher);
        verify(passwordEncoder, never()).encode(anyString());
        assertEquals("John", testTeacher.getFirstName());
        assertEquals("Doe", testTeacher.getLastName());
        assertEquals("john.doe@example.com", testTeacher.getEmail());
        assertEquals("PhD", testTeacher.getQualification());
        assertEquals("encodedCurrentPassword", testTeacher.getPassword());
    }

    @Test
    void updateProfile_withEmptyNewPassword() {
        // Setup
        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);
        testTeacher.setPassword("encodedCurrentPassword");

        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setFirstName("John");
        updatedTeacher.setLastName("Doe");
        updatedTeacher.setEmail("john.doe@example.com");
        updatedTeacher.setQualification("PhD");

        String currentPassword = "currentPass123";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(passwordEncoder.matches(currentPassword, testTeacher.getPassword())).thenReturn(true);

        // Act
        String viewName = teacherController.updateProfile(updatedTeacher, "", currentPassword, model);

        // Assert
        assertEquals("redirect:/teacher/profile?success", viewName);
        verify(teacherService).updateTeacher(testTeacher);
        verify(passwordEncoder, never()).encode(anyString());
        assertEquals("John", testTeacher.getFirstName());
        assertEquals("Doe", testTeacher.getLastName());
        assertEquals("john.doe@example.com", testTeacher.getEmail());
        assertEquals("PhD", testTeacher.getQualification());
        assertEquals("encodedCurrentPassword", testTeacher.getPassword());
    }

    @Test
    void updateProfile_incorrectCurrentPassword() {
        // Setup
        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);
        testTeacher.setPassword("encodedCurrentPassword");

        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setFirstName("John");
        updatedTeacher.setLastName("Doe");
        updatedTeacher.setEmail("john.doe@example.com");

        String currentPassword = "wrongPassword";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(passwordEncoder.matches(currentPassword, testTeacher.getPassword())).thenReturn(false);
        when(courseService.getCoursesByTeacher(teacherId)).thenReturn(new ArrayList<>());

        // Act
        String viewName = teacherController.updateProfile(updatedTeacher, null, currentPassword, model);

        // Assert
        assertEquals("teacher/edit-profile", viewName);
        verify(model).addAttribute("error", "Current password is incorrect");
        verify(model).addAttribute("teacher", testTeacher);
        verify(model).addAttribute("courses", new ArrayList<>());
        verify(teacherService, never()).updateTeacher(any());
    }

    @Test
    void updateProfile_exception() {
        // Setup
        testTeacher = new Teacher();
        testTeacher.setId(teacherId);
        testTeacher.setUsername(testUsername);
        testTeacher.setPassword("encodedCurrentPassword");

        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setFirstName("John");
        updatedTeacher.setLastName("Doe");
        updatedTeacher.setEmail("john.doe@example.com");

        String currentPassword = "currentPass123";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUsername);
        when(teacherService.getTeacherByUsername(testUsername)).thenReturn(testTeacher);
        when(passwordEncoder.matches(currentPassword, testTeacher.getPassword())).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(teacherService).updateTeacher(any());
        when(courseService.getCoursesByTeacher(teacherId)).thenReturn(new ArrayList<>());

        // Act
        String viewName = teacherController.updateProfile(updatedTeacher, null, currentPassword, model);

        // Assert
        assertEquals("teacher/edit-profile", viewName);
        verify(model).addAttribute(eq("error"), contains("Error updating profile"));
        verify(model).addAttribute("teacher", testTeacher);
        verify(model).addAttribute("courses", new ArrayList<>());
    }
}