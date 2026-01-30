package com.example.university.repository;

import com.example.university.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByName(String name);
    boolean existsByCode(String code);
    List<Course> findByDepartmentId(Long departmentId);
    List<Course> findByTeacherId(Long teacherId);

    @Query("SELECT c FROM Course c WHERE c.id NOT IN " +
            "(SELECT sc.id FROM Student s JOIN s.enrolledCourses sc WHERE s.id = :studentId)")
    List<Course> findCoursesNotEnrolledByStudent(@Param("studentId") Long studentId);
}