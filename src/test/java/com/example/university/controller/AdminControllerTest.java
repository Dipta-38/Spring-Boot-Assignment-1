package com.example.university.controller;

import com.example.university.entity.*;
import com.example.university.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private DepartmentService departmentService;

    @Mock
    private CourseService courseService;

    @Mock
    private TeacherService teacherService;

    @Mock
    private StudentService studentService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Model model;

    @InjectMocks
    private AdminController adminController;

    private Department department;
    private Course course;
    private Teacher teacher;
    private Student student;
    private List<Department> departments;
    private List<Course> courses;
    private List<Teacher> teachers;
    private List<Student> students;

    @BeforeEach
    void setUp() {
        // Initialize test data
        department = new Department();
        department.setId(1L);
        department.setName("Computer Science");
        department.setCode("CS");

        course = new Course();
        course.setId(1L);
        course.setName("Data Structures");
        course.setCode("CS201");
        course.setCredits(3);

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setEmail("john.doe@university.com");
        teacher.setTeacherId("T1001");
        teacher.setQualification("PhD");
        teacher.setDepartment(department);

        student = new Student();
        student.setId(1L);
        student.setFirstName("Jane");
        student.setLastName("Smith");
        student.setEmail("jane.smith@university.com");
        student.setStudentId("S2001");

        // Use Set for departments
        Set<Department> departmentSet = new HashSet<>();
        departmentSet.add(department);
        student.setDepartments(departmentSet);

        departments = Collections.singletonList(department);
        courses = Collections.singletonList(course);
        teachers = Collections.singletonList(teacher);
        students = Collections.singletonList(student);
    }

    @Test
    void adminDashboard() {
        // Arrange
        when(departmentService.getAllDepartments()).thenReturn(departments);
        when(courseService.getAllCourses()).thenReturn(courses);
        when(teacherService.getAllTeachers()).thenReturn(teachers);
        when(studentService.getAllStudents()).thenReturn(students);

        // Act
        String viewName = adminController.adminDashboard(model);

        // Assert
        assertEquals("admin/dashboard", viewName);
        verify(model).addAttribute("departments", departments);
        verify(model).addAttribute("courses", courses);
        verify(model).addAttribute("teachers", teachers);
        verify(model).addAttribute("students", students);
    }

    @Test
    void listDepartments() {
        // Arrange
        when(departmentService.getAllDepartments()).thenReturn(departments);

        // Act
        String viewName = adminController.listDepartments(model);

        // Assert
        assertEquals("admin/departments", viewName);
        verify(model).addAttribute("departments", departments);
        verify(model).addAttribute(eq("department"), any(Department.class));
    }

    @Test
    void createDepartment() {
        // Arrange
        Department newDepartment = new Department();
        newDepartment.setName("Mathematics");
        newDepartment.setCode("MATH");

        // Act
        String viewName = adminController.createDepartment(newDepartment);

        // Assert
        assertEquals("redirect:/admin/departments", viewName);
        verify(departmentService).createDepartment(newDepartment);
    }

    @Test
    void editDepartmentForm() {
        // Arrange
        Long departmentId = 1L;
        when(departmentService.getDepartmentById(departmentId)).thenReturn(department);

        // Act
        String viewName = adminController.editDepartmentForm(departmentId, model);

        // Assert
        assertEquals("admin/edit-department", viewName);
        verify(model).addAttribute("department", department);
    }

    @Test
    void updateDepartment() {
        // Arrange
        Long departmentId = 1L;
        Department updatedDepartment = new Department();
        updatedDepartment.setName("Updated CS");
        updatedDepartment.setCode("UCS");

        // Act
        String viewName = adminController.updateDepartment(departmentId, updatedDepartment);

        // Assert
        assertEquals("redirect:/admin/departments", viewName);
        verify(departmentService).updateDepartment(departmentId, updatedDepartment);
    }

    @Test
    void deleteDepartment() {
        // Arrange
        Long departmentId = 1L;

        // Act
        String viewName = adminController.deleteDepartment(departmentId);

        // Assert
        assertEquals("redirect:/admin/departments", viewName);
        verify(departmentService).deleteDepartment(departmentId);
    }

    @Test
    void listCourses() {
        // Arrange
        when(courseService.getAllCourses()).thenReturn(courses);
        when(departmentService.getAllDepartments()).thenReturn(departments);
        when(teacherService.getAllTeachers()).thenReturn(teachers);

        // Act
        String viewName = adminController.listCourses(model);

        // Assert
        assertEquals("admin/courses", viewName);
        verify(model).addAttribute("courses", courses);
        verify(model).addAttribute(eq("course"), any(Course.class));
        verify(model).addAttribute("departments", departments);
        verify(model).addAttribute("teachers", teachers);
    }

    @Test
    void createCourse() {
        // Arrange
        Course newCourse = new Course();
        newCourse.setName("Algorithms");
        newCourse.setCode("CS301");
        newCourse.setCredits(4);
        Long departmentId = 1L;
        Long teacherId = 1L;

        // Act
        String viewName = adminController.createCourse(newCourse, departmentId, teacherId);

        // Assert
        assertEquals("redirect:/admin/courses", viewName);
        verify(courseService).createCourse(newCourse, departmentId, teacherId);
    }

    @Test
    void editCourseForm() {
        // Arrange
        Long courseId = 1L;
        when(courseService.getCourseById(courseId)).thenReturn(course);
        when(departmentService.getAllDepartments()).thenReturn(departments);
        when(teacherService.getAllTeachers()).thenReturn(teachers);

        // Act
        String viewName = adminController.editCourseForm(courseId, model);

        // Assert
        assertEquals("admin/edit-course", viewName);
        verify(model).addAttribute("course", course);
        verify(model).addAttribute("departments", departments);
        verify(model).addAttribute("teachers", teachers);
    }

    @Test
    void updateCourse() {
        // Arrange
        Long courseId = 1L;
        Course updatedCourse = new Course();
        updatedCourse.setName("Advanced Algorithms");
        updatedCourse.setCode("CS401");
        updatedCourse.setCredits(4);
        Long departmentId = 1L;
        Long teacherId = 1L;

        // Act
        String viewName = adminController.updateCourse(courseId, updatedCourse, departmentId, teacherId);

        // Assert
        assertEquals("redirect:/admin/courses", viewName);
        verify(courseService).updateCourse(courseId, updatedCourse, departmentId, teacherId);
    }

    @Test
    void deleteCourse() {
        // Arrange
        Long courseId = 1L;

        // Act
        String viewName = adminController.deleteCourse(courseId);

        // Assert
        assertEquals("redirect:/admin/courses", viewName);
        verify(courseService).deleteCourse(courseId);
    }

    @Test
    void listTeachers() {
        // Arrange
        when(teacherService.getAllTeachers()).thenReturn(teachers);
        when(departmentService.getAllDepartments()).thenReturn(departments);

        // Act
        String viewName = adminController.listTeachers(model);

        // Assert
        assertEquals("admin/teachers", viewName);
        verify(model).addAttribute("teachers", teachers);
        verify(model).addAttribute("departments", departments);
    }

    @Test
    void editTeacherForm() {
        // Arrange
        Long teacherId = 1L;
        when(teacherService.getTeacherById(teacherId)).thenReturn(teacher);
        when(departmentService.getAllDepartments()).thenReturn(departments);

        // Act
        String viewName = adminController.editTeacherForm(teacherId, model);

        // Assert
        assertEquals("admin/edit-teacher", viewName);
        verify(model).addAttribute("teacher", teacher);
        verify(model).addAttribute("departments", departments);
    }

    @Test
    void updateTeacher_withoutPasswordChange() {
        // Arrange
        Long teacherId = 1L;
        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setFirstName("John Updated");
        updatedTeacher.setLastName("Doe Updated");
        updatedTeacher.setEmail("john.updated@university.com");
        updatedTeacher.setTeacherId("T1002");
        updatedTeacher.setQualification("Master's");

        when(teacherService.getTeacherById(teacherId)).thenReturn(teacher);
        when(departmentService.getDepartmentById(1L)).thenReturn(department);

        // Act
        String viewName = adminController.updateTeacher(teacherId, updatedTeacher, null, 1L);

        // Assert
        assertEquals("redirect:/admin/teachers", viewName);
        verify(teacherService).updateTeacher(teacher);
        verify(passwordEncoder, never()).encode(anyString());
        assertEquals("John Updated", teacher.getFirstName());
        assertEquals("Doe Updated", teacher.getLastName());
        assertEquals("john.updated@university.com", teacher.getEmail());
        assertEquals("T1002", teacher.getTeacherId());
        assertEquals("Master's", teacher.getQualification());
        assertEquals(department, teacher.getDepartment());
    }

    @Test
    void updateTeacher_withPasswordChange() {
        // Arrange
        Long teacherId = 1L;
        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setFirstName("John");
        updatedTeacher.setLastName("Doe");
        updatedTeacher.setEmail("john.doe@university.com");
        updatedTeacher.setTeacherId("T1001");
        updatedTeacher.setQualification("PhD");

        String newPassword = "newPassword123";
        String encodedPassword = "encodedPassword123";

        when(teacherService.getTeacherById(teacherId)).thenReturn(teacher);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(departmentService.getDepartmentById(1L)).thenReturn(department);

        // Act
        String viewName = adminController.updateTeacher(teacherId, updatedTeacher, newPassword, 1L);

        // Assert
        assertEquals("redirect:/admin/teachers", viewName);
        verify(teacherService).updateTeacher(teacher);
        verify(passwordEncoder).encode(newPassword);
        assertEquals(encodedPassword, teacher.getPassword());
    }

    @Test
    void updateTeacher_withNullDepartment() {
        // Arrange
        Long teacherId = 1L;
        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setFirstName("John");
        updatedTeacher.setLastName("Doe");
        updatedTeacher.setEmail("john.doe@university.com");
        updatedTeacher.setTeacherId("T1001");
        updatedTeacher.setQualification("PhD");

        when(teacherService.getTeacherById(teacherId)).thenReturn(teacher);

        // Act
        String viewName = adminController.updateTeacher(teacherId, updatedTeacher, null, null);

        // Assert
        assertEquals("redirect:/admin/teachers", viewName);
        verify(teacherService).updateTeacher(teacher);
        assertNull(teacher.getDepartment());
    }

    @Test
    void deleteTeacher() {
        // Arrange
        Long teacherId = 1L;

        // Act
        String viewName = adminController.deleteTeacher(teacherId);

        // Assert
        assertEquals("redirect:/admin/teachers", viewName);
        verify(teacherService).deleteTeacher(teacherId);
    }

    @Test
    void viewTeacher() {
        // Arrange
        Long teacherId = 1L;
        when(teacherService.getTeacherById(teacherId)).thenReturn(teacher);
        when(courseService.getCoursesByTeacher(teacherId)).thenReturn(courses);

        // Act
        String viewName = adminController.viewTeacher(teacherId, model);

        // Assert
        assertEquals("admin/view-teacher", viewName);
        verify(model).addAttribute("teacher", teacher);
        verify(model).addAttribute("courses", courses);
    }

    @Test
    void listStudents() {
        // Arrange
        when(studentService.getAllStudents()).thenReturn(students);
        when(departmentService.getAllDepartments()).thenReturn(departments);

        // Act
        String viewName = adminController.listStudents(model);

        // Assert
        assertEquals("admin/students", viewName);
        verify(model).addAttribute("students", students);
        verify(model).addAttribute("departments", departments);
    }

    @Test
    void editStudentForm() {
        // Arrange
        Long studentId = 1L;
        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(departmentService.getAllDepartments()).thenReturn(departments);

        // Act
        String viewName = adminController.editStudentForm(studentId, model);

        // Assert
        assertEquals("admin/edit-student", viewName);
        verify(model).addAttribute("student", student);
        verify(model).addAttribute("departments", departments);
        verify(model).addAttribute(eq("studentDepartmentIds"), anyList());
    }

    @Test
    void updateStudent_withoutPasswordChange() {
        // Arrange
        Long studentId = 1L;
        Student updatedStudent = new Student();
        updatedStudent.setFirstName("Jane Updated");
        updatedStudent.setLastName("Smith Updated");
        updatedStudent.setEmail("jane.updated@university.com");
        updatedStudent.setStudentId("S2002");

        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(departmentService.getDepartmentById(1L)).thenReturn(department);

        // Act
        String viewName = adminController.updateStudent(studentId, updatedStudent, null, Collections.singletonList(1L));

        // Assert
        assertEquals("redirect:/admin/students?success=true", viewName);
        verify(studentService).updateStudent(student);
        verify(passwordEncoder, never()).encode(anyString());
        assertEquals("Jane Updated", student.getFirstName());
        assertEquals("Smith Updated", student.getLastName());
        assertEquals("jane.updated@university.com", student.getEmail());
        assertEquals("S2002", student.getStudentId());

        // Verify departments were set correctly (as Set)
        Set<Department> expectedDepartments = new HashSet<>();
        expectedDepartments.add(department);
        assertEquals(expectedDepartments, student.getDepartments());
    }

    @Test
    void updateStudent_withPasswordChange() {
        // Arrange
        Long studentId = 1L;
        Student updatedStudent = new Student();
        updatedStudent.setFirstName("Jane");
        updatedStudent.setLastName("Smith");
        updatedStudent.setEmail("jane.smith@university.com");
        updatedStudent.setStudentId("S2001");

        String newPassword = "newPassword123";
        String encodedPassword = "encodedPassword123";

        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(departmentService.getDepartmentById(1L)).thenReturn(department);

        // Act
        String viewName = adminController.updateStudent(studentId, updatedStudent, newPassword, Collections.singletonList(1L));

        // Assert
        assertEquals("redirect:/admin/students?success=true", viewName);
        verify(studentService).updateStudent(student);
        verify(passwordEncoder).encode(newPassword);
        assertEquals(encodedPassword, student.getPassword());

        // Verify departments were set correctly (as Set)
        Set<Department> expectedDepartments = new HashSet<>();
        expectedDepartments.add(department);
        assertEquals(expectedDepartments, student.getDepartments());
    }

    @Test
    void updateStudent_withMultipleDepartments() {
        // Arrange
        Long studentId = 1L;
        Student updatedStudent = new Student();
        updatedStudent.setFirstName("Jane");
        updatedStudent.setLastName("Smith");
        updatedStudent.setEmail("jane.smith@university.com");
        updatedStudent.setStudentId("S2001");

        Department department2 = new Department();
        department2.setId(2L);
        department2.setName("Mathematics");
        department2.setCode("MATH");

        List<Long> departmentIds = Arrays.asList(1L, 2L);

        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(departmentService.getDepartmentById(1L)).thenReturn(department);
        when(departmentService.getDepartmentById(2L)).thenReturn(department2);

        // Act
        String viewName = adminController.updateStudent(studentId, updatedStudent, null, departmentIds);

        // Assert
        assertEquals("redirect:/admin/students?success=true", viewName);
        verify(studentService).updateStudent(student);

        // Verify both departments were added
        Set<Department> expectedDepartments = new HashSet<>();
        expectedDepartments.add(department);
        expectedDepartments.add(department2);
        assertEquals(expectedDepartments, student.getDepartments());
        assertEquals(2, student.getDepartments().size());
    }

    @Test
    void updateStudent_withNoDepartments() {
        // Arrange
        Long studentId = 1L;
        Student updatedStudent = new Student();
        updatedStudent.setFirstName("Jane");
        updatedStudent.setLastName("Smith");
        updatedStudent.setEmail("jane.smith@university.com");
        updatedStudent.setStudentId("S2001");

        when(studentService.getStudentById(studentId)).thenReturn(student);

        // Act
        String viewName = adminController.updateStudent(studentId, updatedStudent, null, null);

        // Assert
        assertEquals("redirect:/admin/students?success=true", viewName);
        verify(studentService).updateStudent(student);
        assertTrue(student.getDepartments().isEmpty());
    }

    @Test
    void deleteStudent() {
        // Arrange
        Long studentId = 1L;

        // Act
        String viewName = adminController.deleteStudent(studentId);

        // Assert
        assertEquals("redirect:/admin/students", viewName);
        verify(studentService).deleteStudent(studentId);
    }

    @Test
    void viewStudent() {
        // Arrange
        Long studentId = 1L;
        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(studentService.getEnrolledCourses(studentId)).thenReturn(courses);

        // Act
        String viewName = adminController.viewStudent(studentId, model);

        // Assert
        assertEquals("admin/view-student", viewName);
        verify(model).addAttribute("student", student);
        verify(model).addAttribute("enrolledCourses", courses);
    }
}