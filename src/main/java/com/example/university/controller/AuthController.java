package com.example.university.controller;

import com.example.university.entity.*;
import com.example.university.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          StudentRepository studentRepository,
                          TeacherRepository teacherRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        logger.info("GET /register accessed");
        return "register";
    }

    @PostMapping("/register")
    @Transactional
    public String registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String teacherId,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String qualification,
            Model model) {

        logger.info("=== REGISTRATION START ===");
        logger.info("Username: {}, Email: {}, Role: {}", username, email, role);

        try {
            // Check if username exists
            boolean usernameExists = userRepository.existsByUsername(username);
            logger.info("Username exists? {}", usernameExists);

            if (usernameExists) {
                model.addAttribute("error", "Username already exists");
                return "register";
            }

            // Check if email exists
            boolean emailExists = userRepository.existsByEmail(email);
            logger.info("Email exists? {}", emailExists);

            if (emailExists) {
                model.addAttribute("error", "Email already exists");
                return "register";
            }

            // Create user based on role
            if ("STUDENT".equals(role)) {
                logger.info("Creating STUDENT: {}", username);

                // Validate student fields
                if (studentId == null || studentId.trim().isEmpty()) {
                    model.addAttribute("error", "Student ID is required");
                    return "register";
                }

                if (studentRepository.existsByStudentId(studentId)) {
                    model.addAttribute("error", "Student ID already exists");
                    return "register";
                }

                // Create student entity
                Student student = new Student();
                student.setUsername(username);
                student.setEmail(email);
                student.setPassword(passwordEncoder.encode(password));
                student.setRole(Role.ROLE_STUDENT);
                student.setEnabled(true);
                student.setStudentId(studentId);
                student.setFirstName(firstName != null ? firstName : "");
                student.setLastName(lastName != null ? lastName : "");

                logger.info("Saving student to database...");
                Student savedStudent = studentRepository.save(student);
                logger.info("✅ Student saved with ID: {}", savedStudent.getId());

            } else if ("TEACHER".equals(role)) {
                logger.info("Creating TEACHER: {}", username);

                // Validate teacher fields
                if (teacherId == null || teacherId.trim().isEmpty()) {
                    model.addAttribute("error", "Teacher ID is required");
                    return "register";
                }

                if (teacherRepository.existsByTeacherId(teacherId)) {
                    model.addAttribute("error", "Teacher ID already exists");
                    return "register";
                }

                // Create teacher entity
                Teacher teacher = new Teacher();
                teacher.setUsername(username);
                teacher.setEmail(email);
                teacher.setPassword(passwordEncoder.encode(password));
                teacher.setRole(Role.ROLE_TEACHER);
                teacher.setEnabled(true);
                teacher.setTeacherId(teacherId);
                teacher.setFirstName(firstName != null ? firstName : "");
                teacher.setLastName(lastName != null ? lastName : "");
                teacher.setQualification(qualification != null ? qualification : "");

                logger.info("Saving teacher to database...");
                Teacher savedTeacher = teacherRepository.save(teacher);
                logger.info("✅ Teacher saved with ID: {}", savedTeacher.getId());

            } else if ("ADMIN".equals(role)) {
                logger.info("Creating ADMIN: {}", username);

                // Create admin user (regular User entity)
                User admin = new User();
                admin.setUsername(username);
                admin.setEmail(email);
                admin.setPassword(passwordEncoder.encode(password));
                admin.setRole(Role.ROLE_ADMIN);
                admin.setEnabled(true);

                logger.info("Saving admin to database...");
                User savedAdmin = userRepository.save(admin);
                logger.info("✅ Admin saved with ID: {}", savedAdmin.getId());

            } else {
                model.addAttribute("error", "Invalid role selected");
                return "register";
            }

            logger.info("=== REGISTRATION COMPLETED SUCCESSFULLY ===");
            return "redirect:/login?success";

        } catch (Exception e) {
            logger.error("❌ Registration failed", e);
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    // ADD THIS METHOD - Dashboard redirect based on role
    @GetMapping("/dashboard")
    public String redirectToRoleDashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Check user role and redirect accordingly
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER"))) {
            return "redirect:/teacher";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
            return "redirect:/student";
        }

        // Default fallback
        return "redirect:/login";
    }
}