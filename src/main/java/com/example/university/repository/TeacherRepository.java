package com.example.university.repository;

import com.example.university.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByTeacherId(String teacherId);
    boolean existsByTeacherId(String teacherId);
    Optional<Teacher> findByUsername(String username);
}