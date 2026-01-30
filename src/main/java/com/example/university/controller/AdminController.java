package com.example.university.controller;

import com.example.university.entity.*;
import com.example.university.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DepartmentService departmentService;
    private final CourseService courseService;
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(DepartmentService departmentService,
                           CourseService courseService,
                           TeacherService teacherService,
                           StudentService studentService,
                           PasswordEncoder passwordEncoder) {
        this.departmentService = departmentService;
        this.courseService = courseService;
        this.teacherService = teacherService;
        this.studentService = studentService;
        this.passwordEncoder = passwordEncoder;
    }

    // Dashboard
    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("students", studentService.getAllStudents());
        return "admin/dashboard";
    }

    // ========== DEPARTMENT CRUD ==========
    @GetMapping("/departments")
    public String listDepartments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("department", new Department());
        return "admin/departments";
    }

    @PostMapping("/departments")
    public String createDepartment(@ModelAttribute Department department) {
        departmentService.createDepartment(department);
        return "redirect:/admin/departments";
    }

    @GetMapping("/departments/{id}/edit")
    public String editDepartmentForm(@PathVariable Long id, Model model) {
        model.addAttribute("department", departmentService.getDepartmentById(id));
        return "admin/edit-department";
    }

    @PostMapping("/departments/{id}/edit")
    public String updateDepartment(@PathVariable Long id, @ModelAttribute Department department) {
        departmentService.updateDepartment(id, department);
        return "redirect:/admin/departments";
    }

    @PostMapping("/departments/{id}/delete")
    public String deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return "redirect:/admin/departments";
    }

    // ========== COURSE CRUD ==========
    @GetMapping("/courses")
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("course", new Course());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "admin/courses";
    }

    @PostMapping("/courses")
    public String createCourse(@ModelAttribute Course course,
                               @RequestParam Long departmentId,
                               @RequestParam Long teacherId) {
        courseService.createCourse(course, departmentId, teacherId);
        return "redirect:/admin/courses";
    }

    @GetMapping("/courses/{id}/edit")
    public String editCourseForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "admin/edit-course";
    }

    @PostMapping("/courses/{id}/edit")
    public String updateCourse(@PathVariable Long id,
                               @ModelAttribute Course course,
                               @RequestParam Long departmentId,
                               @RequestParam Long teacherId) {
        courseService.updateCourse(id, course, departmentId, teacherId);
        return "redirect:/admin/courses";
    }

    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/admin/courses";
    }

    // ========== TEACHER MANAGEMENT ==========
    @GetMapping("/teachers")
    public String listTeachers(Model model) {
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "admin/teachers";
    }

    @GetMapping("/teachers/{id}/edit")
    public String editTeacherForm(@PathVariable Long id, Model model) {
        Teacher teacher = teacherService.getTeacherById(id);
        model.addAttribute("teacher", teacher);
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "admin/edit-teacher";
    }

    @PostMapping("/teachers/{id}/edit")
    public String updateTeacher(@PathVariable Long id,
                                @ModelAttribute Teacher teacher,
                                @RequestParam(required = false) String newPassword,
                                @RequestParam(required = false) Long departmentId) {
        Teacher existingTeacher = teacherService.getTeacherById(id);

        // Update basic info
        existingTeacher.setFirstName(teacher.getFirstName());
        existingTeacher.setLastName(teacher.getLastName());
        existingTeacher.setEmail(teacher.getEmail());
        existingTeacher.setTeacherId(teacher.getTeacherId());
        existingTeacher.setQualification(teacher.getQualification());

        // Update password if provided
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            existingTeacher.setPassword(passwordEncoder.encode(newPassword));
        }

        // Update department
        if (departmentId != null) {
            Department department = departmentService.getDepartmentById(departmentId);
            existingTeacher.setDepartment(department);
        } else {
            existingTeacher.setDepartment(null);
        }

        teacherService.updateTeacher(existingTeacher);
        return "redirect:/admin/teachers";
    }

    @PostMapping("/teachers/{id}/delete")
    public String deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return "redirect:/admin/teachers";
    }

    @GetMapping("/teachers/{id}")
    public String viewTeacher(@PathVariable Long id, Model model) {
        Teacher teacher = teacherService.getTeacherById(id);
        List<Course> teacherCourses = courseService.getCoursesByTeacher(id);

        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", teacherCourses);
        return "admin/view-teacher";
    }

    // ========== STUDENT MANAGEMENT ==========
    @GetMapping("/students")
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "admin/students";
    }

    @GetMapping("/students/{id}/edit")
    public String editStudentForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);

        // Extract department IDs for easier template handling
        List<Long> studentDepartmentIds = student.getDepartments().stream()
                .map(Department::getId)
                .collect(Collectors.toList());

        model.addAttribute("student", student);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("studentDepartmentIds", studentDepartmentIds);
        return "admin/edit-student";
    }

    @PostMapping("/students/{id}/edit")
    public String updateStudent(@PathVariable Long id,
                                @ModelAttribute Student student,
                                @RequestParam(required = false) String newPassword,
                                @RequestParam(required = false) List<Long> departmentIds) {
        Student existingStudent = studentService.getStudentById(id);

        // Update basic info
        existingStudent.setFirstName(student.getFirstName());
        existingStudent.setLastName(student.getLastName());
        existingStudent.setEmail(student.getEmail());
        existingStudent.setStudentId(student.getStudentId());

        // Update password if provided
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            existingStudent.setPassword(passwordEncoder.encode(newPassword));
        }

        // Update departments
        existingStudent.getDepartments().clear();
        if (departmentIds != null && !departmentIds.isEmpty()) {
            for (Long deptId : departmentIds) {
                Department department = departmentService.getDepartmentById(deptId);
                existingStudent.getDepartments().add(department);
            }
        }

        studentService.updateStudent(existingStudent);
        return "redirect:/admin/students?success=true";
    }

    @PostMapping("/students/{id}/delete")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/admin/students";
    }

    @GetMapping("/students/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        List<Course> enrolledCourses = studentService.getEnrolledCourses(id);

        model.addAttribute("student", student);
        model.addAttribute("enrolledCourses", enrolledCourses);
        return "admin/view-student";
    }
}