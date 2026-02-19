package com.example.university.controller;

import com.example.university.entity.Course;
import com.example.university.entity.Department;
import com.example.university.entity.Teacher;
import com.example.university.service.CourseService;
import com.example.university.service.DepartmentService;
import com.example.university.service.TeacherService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final CourseService courseService;
    private final TeacherService teacherService;
    private final DepartmentService departmentService;
    private final PasswordEncoder passwordEncoder;

    public TeacherController(CourseService courseService,
                             TeacherService teacherService,
                             DepartmentService departmentService,
                             PasswordEncoder passwordEncoder) {
        this.courseService = courseService;
        this.teacherService = teacherService;
        this.departmentService = departmentService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String teacherDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Teacher teacher = teacherService.getTeacherByUsername(username);
        List<Course> myCourses = courseService.getCoursesByTeacher(teacher.getId());

        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", myCourses);
        return "teacher/dashboard";
    }

    @GetMapping("/courses")
    public String listCourses(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Teacher teacher = teacherService.getTeacherByUsername(username);
        List<Course> myCourses = courseService.getCoursesByTeacher(teacher.getId());


        List<Department> departments = departmentService.getAllDepartments();

        model.addAttribute("courses", myCourses);
        model.addAttribute("teacher", teacher);
        model.addAttribute("departments", departments);
        model.addAttribute("course", new Course()); // Add empty course for the form
        return "teacher/courses";
    }

    @PostMapping("/courses")
    public String createCourse(@ModelAttribute Course course,
                               @RequestParam Long departmentId,
                               Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            Teacher teacher = teacherService.getTeacherByUsername(username);


            if (departmentId == null) {
                model.addAttribute("error", "Please select a department for the course");
                return populateCourseFormModel(model, username, course);
            }


            if (course.getName() == null || course.getName().trim().isEmpty()) {
                model.addAttribute("error", "Course name is required");
                return populateCourseFormModel(model, username, course);
            }

            courseService.createCourse(course, departmentId, teacher.getId());
            return "redirect:/teacher/courses?success";

        } catch (Exception e) {
            model.addAttribute("error", "Error creating course: " + e.getMessage());
            return populateCourseFormModel(model, username, course);
        }
    }

    @GetMapping("/courses/{id}/edit")
    public String editCourseForm(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Course course = courseService.getCourseById(id);
        Teacher teacher = teacherService.getTeacherByUsername(username);
        List<Department> departments = departmentService.getAllDepartments();


        if (course.getTeacher() == null || !course.getTeacher().getId().equals(teacher.getId())) {
            model.addAttribute("error", "You can only edit your own courses");
            return populateCourseFormModel(model, username, new Course());
        }

        model.addAttribute("course", course);
        model.addAttribute("teacher", teacher);
        model.addAttribute("departments", departments);
        return "teacher/edit-course";
    }

    @PostMapping("/courses/{id}/edit")
    public String updateCourse(@PathVariable Long id,
                               @ModelAttribute Course course,
                               @RequestParam Long departmentId,
                               Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            Teacher teacher = teacherService.getTeacherByUsername(username);
            Course existingCourse = courseService.getCourseById(id);


            if (existingCourse.getTeacher() == null || !existingCourse.getTeacher().getId().equals(teacher.getId())) {
                model.addAttribute("error", "You can only edit your own courses");
                return populateCourseFormModel(model, username, course);
            }


            if (departmentId == null) {
                model.addAttribute("error", "Please select a department for the course");
                return populateCourseFormModel(model, username, course);
            }


            if (course.getName() == null || course.getName().trim().isEmpty()) {
                model.addAttribute("error", "Course name is required");
                return populateCourseFormModel(model, username, course);
            }

            courseService.updateCourse(id, course, departmentId, teacher.getId());
            return "redirect:/teacher/courses?success";

        } catch (Exception e) {
            model.addAttribute("error", "Error updating course: " + e.getMessage());
            return populateCourseFormModel(model, username, course);
        }
    }

    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            Teacher teacher = teacherService.getTeacherByUsername(username);
            Course course = courseService.getCourseById(id);


            if (course.getTeacher() == null || !course.getTeacher().getId().equals(teacher.getId())) {
                model.addAttribute("error", "You can only delete your own courses");
                return populateCourseFormModel(model, username, new Course());
            }

            courseService.deleteCourse(id);
            return "redirect:/teacher/courses?deleted";

        } catch (Exception e) {
            model.addAttribute("error", "Error deleting course: " + e.getMessage());
            return populateCourseFormModel(model, username, new Course());
        }
    }

    @GetMapping("/courses/{id}/students")
    public String viewCourseStudents(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Course course = courseService.getCourseById(id);
        Teacher teacher = teacherService.getTeacherByUsername(username);


        if (course.getTeacher() == null || !course.getTeacher().getId().equals(teacher.getId())) {
            model.addAttribute("error", "You can only view students in your own courses");
            return populateCourseFormModel(model, username, new Course());
        }

        model.addAttribute("course", course);
        model.addAttribute("students", course.getStudents());
        model.addAttribute("teacher", teacher);
        return "teacher/course-students";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Teacher teacher = teacherService.getTeacherByUsername(username);
        List<Course> myCourses = courseService.getCoursesByTeacher(teacher.getId());

        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", myCourses);
        return "teacher/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Teacher teacher = teacherService.getTeacherByUsername(username);
        List<Course> myCourses = courseService.getCoursesByTeacher(teacher.getId());

        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", myCourses);
        return "teacher/edit-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Teacher teacher,
                                @RequestParam(required = false) String newPassword,
                                @RequestParam String currentPassword,
                                Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            Teacher existingTeacher = teacherService.getTeacherByUsername(username);


            if (!passwordEncoder.matches(currentPassword, existingTeacher.getPassword())) {
                model.addAttribute("error", "Current password is incorrect");
                model.addAttribute("teacher", existingTeacher);
                model.addAttribute("courses", courseService.getCoursesByTeacher(existingTeacher.getId()));
                return "teacher/edit-profile";
            }


            existingTeacher.setFirstName(teacher.getFirstName());
            existingTeacher.setLastName(teacher.getLastName());
            existingTeacher.setEmail(teacher.getEmail());
            existingTeacher.setQualification(teacher.getQualification());


            if (newPassword != null && !newPassword.trim().isEmpty()) {
                existingTeacher.setPassword(passwordEncoder.encode(newPassword));
            }

            teacherService.updateTeacher(existingTeacher);
            return "redirect:/teacher/profile?success";

        } catch (Exception e) {
            model.addAttribute("error", "Error updating profile: " + e.getMessage());
            Teacher currentTeacher = teacherService.getTeacherByUsername(username);
            model.addAttribute("teacher", currentTeacher);
            model.addAttribute("courses", courseService.getCoursesByTeacher(currentTeacher.getId()));
            return "teacher/edit-profile";
        }
    }


    private String populateCourseFormModel(Model model, String username, Course course) {
        Teacher teacher = teacherService.getTeacherByUsername(username);
        List<Course> myCourses = courseService.getCoursesByTeacher(teacher.getId());
        List<Department> departments = departmentService.getAllDepartments();

        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", myCourses);
        model.addAttribute("departments", departments);
        model.addAttribute("course", course); // This is important for form persistence
        return "teacher/courses";
    }
}