package com.example.university.service;

import com.example.university.entity.Course;
import com.example.university.entity.Department;
import com.example.university.entity.Teacher;
import com.example.university.repository.CourseRepository;
import com.example.university.repository.DepartmentRepository;
import com.example.university.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private CourseService courseService;

    private Department department;
    private Teacher teacher;
    private Course course;

    @BeforeEach
    void setUp() {
        // Create test department
        department = new Department();
        department.setId(1L);
        department.setName("Computer Science");
        department.setCode("CS");

        // Create test teacher
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Smith");
        teacher.setTeacherId("TCH001");

        // Create test course
        course = new Course();
        course.setId(1L);
        course.setName("Java Programming");
        course.setCode("CS101");
        course.setCredits(3);
        course.setDepartment(department);
        course.setTeacher(teacher);
    }

    @Test
    void getAllCourses_ShouldReturnAllCourses() {
        // Given
        List<Course> expectedCourses = Arrays.asList(course);
        when(courseRepository.findAll()).thenReturn(expectedCourses);

        // When
        List<Course> actualCourses = courseService.getAllCourses();

        // Then
        assertThat(actualCourses).hasSize(1);
        assertThat(actualCourses.get(0).getName()).isEqualTo("Java Programming");
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void getCourseById_WhenCourseExists_ShouldReturnCourse() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        // When
        Course foundCourse = courseService.getCourseById(1L);

        // Then
        assertThat(foundCourse).isNotNull();
        assertThat(foundCourse.getName()).isEqualTo("Java Programming");
        assertThat(foundCourse.getCode()).isEqualTo("CS101");
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    void getCourseById_WhenCourseDoesNotExist_ShouldThrowException() {
        // Given
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> courseService.getCourseById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Course not found");

        verify(courseRepository, times(1)).findById(99L);
    }

    @Test
    void createCourse_WithValidData_ShouldCreateCourse() {
        // Given
        Course newCourse = new Course();
        newCourse.setName("Python Programming");
        newCourse.setCode("CS102");
        newCourse.setCredits(3);

        when(courseRepository.existsByName("Python Programming")).thenReturn(false);
        when(courseRepository.existsByCode("CS102")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> {
            Course saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        // When
        Course created = courseService.createCourse(newCourse, 1L, 1L);

        // Then
        assertThat(created.getId()).isEqualTo(2L);
        assertThat(created.getName()).isEqualTo("Python Programming");
        assertThat(created.getDepartment()).isEqualTo(department);
        assertThat(created.getTeacher()).isEqualTo(teacher);

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void createCourse_WithExistingName_ShouldThrowException() {
        // Given
        Course newCourse = new Course();
        newCourse.setName("Java Programming");
        newCourse.setCode("CS101");

        when(courseRepository.existsByName("Java Programming")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> courseService.createCourse(newCourse, 1L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Course name already exists");

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void createCourse_WithExistingCode_ShouldThrowException() {
        // Given
        Course newCourse = new Course();
        newCourse.setName("Python Programming");
        newCourse.setCode("CS101");

        when(courseRepository.existsByName("Python Programming")).thenReturn(false);
        when(courseRepository.existsByCode("CS101")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> courseService.createCourse(newCourse, 1L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Course code already exists");

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void createCourse_WithInvalidDepartment_ShouldThrowException() {
        // Given
        Course newCourse = new Course();
        newCourse.setName("Python Programming");
        newCourse.setCode("CS102");

        when(courseRepository.existsByName("Python Programming")).thenReturn(false);
        when(courseRepository.existsByCode("CS102")).thenReturn(false);
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> courseService.createCourse(newCourse, 99L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Department not found");
    }

    @Test
    void updateCourse_WithValidData_ShouldUpdateCourse() {
        // Given
        Course updatedDetails = new Course();
        updatedDetails.setName("Advanced Java");
        updatedDetails.setCode("CS201");
        updatedDetails.setDescription("Advanced Java Course");
        updatedDetails.setCredits(4);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Course updated = courseService.updateCourse(1L, updatedDetails, null, null);

        // Then
        assertThat(updated.getName()).isEqualTo("Advanced Java");
        assertThat(updated.getCode()).isEqualTo("CS201");
        assertThat(updated.getDescription()).isEqualTo("Advanced Java Course");
        assertThat(updated.getCredits()).isEqualTo(4);

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void updateCourse_WithNewDepartment_ShouldUpdateDepartment() {
        // Given
        Course updatedDetails = new Course();
        updatedDetails.setName("Java Programming");

        Department newDepartment = new Department();
        newDepartment.setId(2L);
        newDepartment.setName("Information Technology");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(newDepartment));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Course updated = courseService.updateCourse(1L, updatedDetails, 2L, null);

        // Then
        assertThat(updated.getDepartment().getId()).isEqualTo(2L);
        assertThat(updated.getDepartment().getName()).isEqualTo("Information Technology");
    }

    @Test
    void updateCourse_WithNewTeacher_ShouldUpdateTeacher() {
        // Given
        Course updatedDetails = new Course();
        updatedDetails.setName("Java Programming");

        Teacher newTeacher = new Teacher();
        newTeacher.setId(2L);
        newTeacher.setFirstName("Jane");
        newTeacher.setLastName("Doe");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(teacherRepository.findById(2L)).thenReturn(Optional.of(newTeacher));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Course updated = courseService.updateCourse(1L, updatedDetails, null, 2L);

        // Then
        assertThat(updated.getTeacher().getId()).isEqualTo(2L);
        assertThat(updated.getTeacher().getFirstName()).isEqualTo("Jane");
    }

    @Test
    void deleteCourse_ShouldCallRepository() {
        // Given
        doNothing().when(courseRepository).deleteById(1L);

        // When
        courseService.deleteCourse(1L);

        // Then
        verify(courseRepository, times(1)).deleteById(1L);
    }

    @Test
    void getCoursesByDepartment_ShouldReturnCourses() {
        // Given
        List<Course> expectedCourses = Arrays.asList(course);
        when(courseRepository.findByDepartmentId(1L)).thenReturn(expectedCourses);

        // When
        List<Course> actualCourses = courseService.getCoursesByDepartment(1L);

        // Then
        assertThat(actualCourses).hasSize(1);
        assertThat(actualCourses.get(0).getName()).isEqualTo("Java Programming");
        verify(courseRepository, times(1)).findByDepartmentId(1L);
    }

    @Test
    void getCoursesByTeacher_ShouldReturnCourses() {
        // Given
        List<Course> expectedCourses = Arrays.asList(course);
        when(courseRepository.findByTeacherId(1L)).thenReturn(expectedCourses);

        // When
        List<Course> actualCourses = courseService.getCoursesByTeacher(1L);

        // Then
        assertThat(actualCourses).hasSize(1);
        assertThat(actualCourses.get(0).getName()).isEqualTo("Java Programming");
        verify(courseRepository, times(1)).findByTeacherId(1L);
    }
}