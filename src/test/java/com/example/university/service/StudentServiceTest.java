package com.example.university.service;

import com.example.university.entity.Course;
import com.example.university.entity.Role;
import com.example.university.entity.Student;
import com.example.university.repository.CourseRepository;
import com.example.university.repository.StudentRepository;
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
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student1;
    private Student student2;
    private Course course1;
    private Course course2;
    private Course course3;

    @BeforeEach
    void setUp() {
        // Create test students
        student1 = new Student();
        student1.setId(1L);
        student1.setUsername("john.doe");
        student1.setEmail("john@university.com");
        student1.setPassword("password");
        student1.setRole(Role.ROLE_STUDENT);
        student1.setStudentId("STU001");
        student1.setFirstName("John");
        student1.setLastName("Doe");
        student1.setEnrolledCourses(new HashSet<>());

        student2 = new Student();
        student2.setId(2L);
        student2.setUsername("jane.smith");
        student2.setEmail("jane@university.com");
        student2.setPassword("password");
        student2.setRole(Role.ROLE_STUDENT);
        student2.setStudentId("STU002");
        student2.setFirstName("Jane");
        student2.setLastName("Smith");
        student2.setEnrolledCourses(new HashSet<>());

        // Create test courses
        course1 = new Course();
        course1.setId(101L);
        course1.setName("Java Programming");
        course1.setCode("CS101");
        course1.setCredits(3);

        course2 = new Course();
        course2.setId(102L);
        course2.setName("Data Structures");
        course2.setCode("CS201");
        course2.setCredits(4);

        course3 = new Course();
        course3.setId(103L);
        course3.setName("Database Systems");
        course3.setCode("CS301");
        course3.setCredits(3);
    }

    @Test
    void getAllStudents_ShouldReturnAllStudents() {
        // Given
        List<Student> expectedStudents = Arrays.asList(student1, student2);
        when(studentRepository.findAll()).thenReturn(expectedStudents);

        // When
        List<Student> actualStudents = studentService.getAllStudents();

        // Then
        assertThat(actualStudents).hasSize(2);
        assertThat(actualStudents.get(0).getUsername()).isEqualTo("john.doe");
        assertThat(actualStudents.get(1).getUsername()).isEqualTo("jane.smith");
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void getStudentById_WhenStudentExists_ShouldReturnStudent() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));

        // When
        Student foundStudent = studentService.getStudentById(1L);

        // Then
        assertThat(foundStudent).isNotNull();
        assertThat(foundStudent.getUsername()).isEqualTo("john.doe");
        assertThat(foundStudent.getStudentId()).isEqualTo("STU001");
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    void getStudentById_WhenStudentDoesNotExist_ShouldThrowException() {
        // Given
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.getStudentById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Student not found");

        verify(studentRepository, times(1)).findById(99L);
    }

    @Test
    void getStudentByUsername_WhenStudentExists_ShouldReturnStudent() {
        // Given
        when(studentRepository.findByUsername("john.doe")).thenReturn(Optional.of(student1));

        // When
        Student foundStudent = studentService.getStudentByUsername("john.doe");

        // Then
        assertThat(foundStudent).isNotNull();
        assertThat(foundStudent.getStudentId()).isEqualTo("STU001");
        verify(studentRepository, times(1)).findByUsername("john.doe");
    }

    @Test
    void getStudentByUsername_WhenStudentDoesNotExist_ShouldThrowException() {
        // Given
        when(studentRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.getStudentByUsername("nonexistent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Student not found");

        verify(studentRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void updateStudent_ShouldUpdateAndReturnStudent() {
        // Given
        Student updatedDetails = new Student();
        updatedDetails.setId(1L);
        updatedDetails.setUsername("john.doe.updated");
        updatedDetails.setEmail("john.updated@university.com");
        updatedDetails.setFirstName("Johnathan");
        updatedDetails.setLastName("Doe-Smith");

        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Student updated = studentService.updateStudent(updatedDetails);

        // Then
        assertThat(updated.getUsername()).isEqualTo("john.doe.updated");
        assertThat(updated.getEmail()).isEqualTo("john.updated@university.com");
        assertThat(updated.getFirstName()).isEqualTo("Johnathan");
        assertThat(updated.getLastName()).isEqualTo("Doe-Smith");

        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void deleteStudent_ShouldCallRepository() {
        // Given
        doNothing().when(studentRepository).deleteById(1L);

        // When
        studentService.deleteStudent(1L);

        // Then
        verify(studentRepository, times(1)).deleteById(1L);
    }

    @Test
    void enrollInCourse_WithValidIds_ShouldEnrollStudent() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(courseRepository.findById(101L)).thenReturn(Optional.of(course1));
        when(studentRepository.save(any(Student.class))).thenReturn(student1);

        // When
        studentService.enrollInCourse(1L, 101L);

        // Then
        assertThat(student1.getEnrolledCourses()).contains(course1);
        verify(studentRepository, times(1)).save(student1);
    }

    @Test
    void enrollInCourse_WithInvalidStudent_ShouldThrowException() {
        // Given
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.enrollInCourse(99L, 101L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Student not found");

        verify(courseRepository, never()).findById(anyLong());
        verify(studentRepository, never()).save(any());
    }

    @Test
    void enrollInCourse_WithInvalidCourse_ShouldThrowException() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.enrollInCourse(1L, 999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Course not found");

        verify(studentRepository, never()).save(any());
    }

    @Test
    void enrollInCourse_WhenAlreadyEnrolled_ShouldStillWork() {
        // Given
        student1.getEnrolledCourses().add(course1); // Already enrolled

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(courseRepository.findById(101L)).thenReturn(Optional.of(course1));
        when(studentRepository.save(any(Student.class))).thenReturn(student1);

        // When
        studentService.enrollInCourse(1L, 101L);

        // Then
        assertThat(student1.getEnrolledCourses()).hasSize(1);
        assertThat(student1.getEnrolledCourses()).contains(course1);
        verify(studentRepository, times(1)).save(student1);
    }

    @Test
    void unenrollFromCourse_WithValidIds_ShouldUnenrollStudent() {
        // Given
        student1.getEnrolledCourses().add(course1); // Enroll first

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(courseRepository.findById(101L)).thenReturn(Optional.of(course1));
        when(studentRepository.save(any(Student.class))).thenReturn(student1);

        // When
        studentService.unenrollFromCourse(1L, 101L);

        // Then
        assertThat(student1.getEnrolledCourses()).doesNotContain(course1);
        verify(studentRepository, times(1)).save(student1);
    }

    @Test
    void unenrollFromCourse_WithInvalidStudent_ShouldThrowException() {
        // Given
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.unenrollFromCourse(99L, 101L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Student not found");

        verify(studentRepository, never()).save(any());
    }

    @Test
    void getEnrolledCourses_WhenStudentHasCourses_ShouldReturnCourses() {
        // Given
        student1.getEnrolledCourses().add(course1);
        student1.getEnrolledCourses().add(course2);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));

        // When
        List<Course> enrolledCourses = studentService.getEnrolledCourses(1L);

        // Then
        assertThat(enrolledCourses).hasSize(2);
        assertThat(enrolledCourses).extracting(Course::getName)
                .containsExactlyInAnyOrder("Java Programming", "Data Structures");
    }

    @Test
    void getEnrolledCourses_WhenStudentHasNoCourses_ShouldReturnEmptyList() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));

        // When
        List<Course> enrolledCourses = studentService.getEnrolledCourses(1L);

        // Then
        assertThat(enrolledCourses).isEmpty();
    }

    @Test
    void getAvailableCourses_ShouldReturnCoursesNotEnrolled() {
        // Given
        student1.getEnrolledCourses().add(course1); // Student enrolled in course1

        List<Course> allCourses = Arrays.asList(course1, course2, course3);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(courseRepository.findAll()).thenReturn(allCourses);

        // When
        List<Course> availableCourses = studentService.getAvailableCourses(1L);

        // Then
        assertThat(availableCourses).hasSize(2);
        assertThat(availableCourses).extracting(Course::getName)
                .containsExactlyInAnyOrder("Data Structures", "Database Systems");
        assertThat(availableCourses).doesNotContain(course1);
    }

    @Test
    void getAvailableCourses_WhenStudentEnrolledInAll_ShouldReturnEmptyList() {
        // Given
        student1.getEnrolledCourses().add(course1);
        student1.getEnrolledCourses().add(course2);
        student1.getEnrolledCourses().add(course3);

        List<Course> allCourses = Arrays.asList(course1, course2, course3);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(courseRepository.findAll()).thenReturn(allCourses);

        // When
        List<Course> availableCourses = studentService.getAvailableCourses(1L);

        // Then
        assertThat(availableCourses).isEmpty();
    }

    @Test
    void getAvailableCourses_WhenNoCoursesExist_ShouldReturnEmptyList() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(courseRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Course> availableCourses = studentService.getAvailableCourses(1L);

        // Then
        assertThat(availableCourses).isEmpty();
    }
}