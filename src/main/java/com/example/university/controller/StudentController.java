package com.example.university.controller;

import com.example.university.entity.Course;
import com.example.university.entity.Student;
import com.example.university.service.CourseService;
import com.example.university.service.StudentService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final PasswordEncoder passwordEncoder;

    public StudentController(StudentService studentService,
                             CourseService courseService,
                             PasswordEncoder passwordEncoder) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String studentDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Student student = studentService.getStudentByUsername(username);
        List<Course> enrolledCourses = studentService.getEnrolledCourses(student.getId());

        model.addAttribute("student", student);
        model.addAttribute("enrolledCourses", enrolledCourses);
        return "student/dashboard";
    }

    @GetMapping("/courses")
    public String listCourses(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Student student = studentService.getStudentByUsername(username);
        List<Course> availableCourses = studentService.getAvailableCourses(student.getId());
        List<Course> enrolledCourses = studentService.getEnrolledCourses(student.getId());

        model.addAttribute("availableCourses", availableCourses);
        model.addAttribute("enrolledCourses", enrolledCourses);
        model.addAttribute("student", student);
        return "student/courses";
    }

    @PostMapping("/courses/{courseId}/enroll")
    public String enrollInCourse(@PathVariable Long courseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Student student = studentService.getStudentByUsername(username);
        studentService.enrollInCourse(student.getId(), courseId);

        return "redirect:/student/courses";
    }

    @PostMapping("/courses/{courseId}/unenroll")
    public String unenrollFromCourse(@PathVariable Long courseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Student student = studentService.getStudentByUsername(username);
        studentService.unenrollFromCourse(student.getId(), courseId);

        return "redirect:/student/courses";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Student student = studentService.getStudentByUsername(username);
        List<Course> enrolledCourses = studentService.getEnrolledCourses(student.getId());

        model.addAttribute("student", student);
        model.addAttribute("enrolledCourses", enrolledCourses);
        return "student/profile";
    }

    // NEW: Get edit profile form
    @GetMapping("/profile/edit")
    public String editProfileForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Student student = studentService.getStudentByUsername(username);
        model.addAttribute("student", student);
        return "student/edit-profile";
    }

    // NEW: Update profile
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Student student,
                                @RequestParam(required = false) String newPassword,
                                @RequestParam String currentPassword,
                                Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            Student existingStudent = studentService.getStudentByUsername(username);

            // Verify current password
            if (!passwordEncoder.matches(currentPassword, existingStudent.getPassword())) {
                model.addAttribute("error", "Current password is incorrect");
                model.addAttribute("student", existingStudent);
                return "student/edit-profile";
            }

            // Update basic info
            existingStudent.setFirstName(student.getFirstName());
            existingStudent.setLastName(student.getLastName());
            existingStudent.setEmail(student.getEmail());

            // Update password if provided
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                existingStudent.setPassword(passwordEncoder.encode(newPassword));
            }

            studentService.updateStudent(existingStudent);
            model.addAttribute("success", "Profile updated successfully!");
            return "redirect:/student/profile?success";

        } catch (Exception e) {
            model.addAttribute("error", "Error updating profile: " + e.getMessage());
            return "student/edit-profile";
        }
    }
}