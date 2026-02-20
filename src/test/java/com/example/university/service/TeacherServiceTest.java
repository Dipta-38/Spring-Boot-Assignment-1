package com.example.university.service;

import com.example.university.entity.Course;
import com.example.university.entity.Department;
import com.example.university.entity.Role;
import com.example.university.entity.Teacher;
import com.example.university.repository.CourseRepository;
import com.example.university.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Department department;
    private Teacher teacher1;
    private Teacher teacher2;
    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        // Create test department
        department = new Department();
        department.setId(1L);
        department.setName("Computer Science");
        department.setCode("CS");

        // Create test teachers
        teacher1 = new Teacher();
        teacher1.setId(1L);
        teacher1.setUsername("dr.smith");
        teacher1.setEmail("smith@university.com");
        teacher1.setPassword("password");
        teacher1.setRole(Role.ROLE_TEACHER);
        teacher1.setTeacherId("TCH001");
        teacher1.setFirstName("John");
        teacher1.setLastName("Smith");
        teacher1.setQualification("PhD in Computer Science");
        teacher1.setDepartment(department);
        teacher1.setCourses(new HashSet<>());

        teacher2 = new Teacher();
        teacher2.setId(2L);
        teacher2.setUsername("dr.johnson");
        teacher2.setEmail("johnson@university.com");
        teacher2.setPassword("password");
        teacher2.setRole(Role.ROLE_TEACHER);
        teacher2.setTeacherId("TCH002");
        teacher2.setFirstName("Jane");
        teacher2.setLastName("Johnson");
        teacher2.setQualification("PhD in Mathematics");
        teacher2.setDepartment(department);
        teacher2.setCourses(new HashSet<>());

        // Create test courses
        course1 = new Course();
        course1.setId(101L);
        course1.setName("Java Programming");
        course1.setCode("CS101");
        course1.setCredits(3);
        course1.setTeacher(teacher1);

        course2 = new Course();
        course2.setId(102L);
        course2.setName("Data Structures");
        course2.setCode("CS201");
        course2.setCredits(4);
        course2.setTeacher(teacher1);
    }

    @Test
    void getAllTeachers_ShouldReturnAllTeachers() {
        // Given
        List<Teacher> expectedTeachers = Arrays.asList(teacher1, teacher2);
        when(teacherRepository.findAll()).thenReturn(expectedTeachers);

        // When
        List<Teacher> actualTeachers = teacherService.getAllTeachers();

        // Then
        assertThat(actualTeachers).hasSize(2);
        assertThat(actualTeachers.get(0).getUsername()).isEqualTo("dr.smith");
        assertThat(actualTeachers.get(1).getUsername()).isEqualTo("dr.johnson");
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    void getTeacherById_WhenTeacherExists_ShouldReturnTeacher() {
        // Given
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));

        // When
        Teacher foundTeacher = teacherService.getTeacherById(1L);

        // Then
        assertThat(foundTeacher).isNotNull();
        assertThat(foundTeacher.getUsername()).isEqualTo("dr.smith");
        assertThat(foundTeacher.getTeacherId()).isEqualTo("TCH001");
        assertThat(foundTeacher.getQualification()).isEqualTo("PhD in Computer Science");
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    void getTeacherById_WhenTeacherDoesNotExist_ShouldThrowException() {
        // Given
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> teacherService.getTeacherById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Teacher not found");

        verify(teacherRepository, times(1)).findById(99L);
    }

    @Test
    void getTeacherByUsername_WhenTeacherExists_ShouldReturnTeacher() {
        // Given
        when(teacherRepository.findByUsername("dr.smith")).thenReturn(Optional.of(teacher1));

        // When
        Teacher foundTeacher = teacherService.getTeacherByUsername("dr.smith");

        // Then
        assertThat(foundTeacher).isNotNull();
        assertThat(foundTeacher.getTeacherId()).isEqualTo("TCH001");
        assertThat(foundTeacher.getFirstName()).isEqualTo("John");
        verify(teacherRepository, times(1)).findByUsername("dr.smith");
    }

    @Test
    void getTeacherByUsername_WhenTeacherDoesNotExist_ShouldThrowException() {
        // Given
        when(teacherRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> teacherService.getTeacherByUsername("nonexistent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Teacher not found");

        verify(teacherRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void updateTeacher_ShouldUpdateAndReturnTeacher() {
        // Given
        Teacher updatedDetails = new Teacher();
        updatedDetails.setId(1L);
        updatedDetails.setUsername("dr.smith.updated");
        updatedDetails.setEmail("smith.updated@university.com");
        updatedDetails.setFirstName("Johnathan");
        updatedDetails.setLastName("Smithson");
        updatedDetails.setQualification("PhD in Artificial Intelligence");

        when(teacherRepository.save(any(Teacher.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Teacher updated = teacherService.updateTeacher(updatedDetails);

        // Then
        assertThat(updated.getUsername()).isEqualTo("dr.smith.updated");
        assertThat(updated.getEmail()).isEqualTo("smith.updated@university.com");
        assertThat(updated.getFirstName()).isEqualTo("Johnathan");
        assertThat(updated.getLastName()).isEqualTo("Smithson");
        assertThat(updated.getQualification()).isEqualTo("PhD in Artificial Intelligence");

        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }

    @Test
    void deleteTeacher_ShouldCallRepository() {
        // Given
        doNothing().when(teacherRepository).deleteById(1L);

        // When
        teacherService.deleteTeacher(1L);

        // Then
        verify(teacherRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTeacher_WithNonExistentId_ShouldStillCallRepository() {
        // Given
        doNothing().when(teacherRepository).deleteById(99L);

        // When
        teacherService.deleteTeacher(99L);

        // Then
        verify(teacherRepository, times(1)).deleteById(99L);
    }

    @Test
    void getCoursesByTeacher_WhenTeacherHasCourses_ShouldReturnCourses() {
        // Given
        teacher1.getCourses().add(course1);
        teacher1.getCourses().add(course2);

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));

        // When
        List<Course> courses = teacherService.getCoursesByTeacher(1L);

        // Then
        assertThat(courses).hasSize(2);
        assertThat(courses).extracting(Course::getName)
                .containsExactlyInAnyOrder("Java Programming", "Data Structures");
        assertThat(courses.get(0).getCode()).isIn("CS101", "CS201");
    }

    @Test
    void getCoursesByTeacher_WhenTeacherHasNoCourses_ShouldReturnEmptyList() {
        // Given
        when(teacherRepository.findById(2L)).thenReturn(Optional.of(teacher2));

        // When
        List<Course> courses = teacherService.getCoursesByTeacher(2L);

        // Then
        assertThat(courses).isEmpty();
    }

    @Test
    void getCoursesByTeacher_WhenTeacherDoesNotExist_ShouldThrowException() {
        // Given
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> teacherService.getCoursesByTeacher(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Teacher not found");

        verify(teacherRepository, times(1)).findById(99L);
    }

    @Test
    void getTeacherById_ShouldIncludeDepartmentInfo() {
        // Given
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));

        // When
        Teacher foundTeacher = teacherService.getTeacherById(1L);

        // Then
        assertThat(foundTeacher.getDepartment()).isNotNull();
        assertThat(foundTeacher.getDepartment().getName()).isEqualTo("Computer Science");
        assertThat(foundTeacher.getDepartment().getCode()).isEqualTo("CS");
    }
}