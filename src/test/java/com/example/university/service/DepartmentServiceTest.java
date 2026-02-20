package com.example.university.service;

import com.example.university.entity.Department;
import com.example.university.repository.DepartmentRepository;
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
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private Department department1;
    private Department department2;

    @BeforeEach
    void setUp() {
        // Create test departments
        department1 = new Department();
        department1.setId(1L);
        department1.setName("Computer Science");
        department1.setCode("CS");
        department1.setDescription("Computer Science Department");

        department2 = new Department();
        department2.setId(2L);
        department2.setName("Mathematics");
        department2.setCode("MATH");
        department2.setDescription("Mathematics Department");
    }

    @Test
    void getAllDepartments_ShouldReturnAllDepartments() {
        // Given
        List<Department> expectedDepartments = Arrays.asList(department1, department2);
        when(departmentRepository.findAll()).thenReturn(expectedDepartments);

        // When
        List<Department> actualDepartments = departmentService.getAllDepartments();

        // Then
        assertThat(actualDepartments).hasSize(2);
        assertThat(actualDepartments.get(0).getName()).isEqualTo("Computer Science");
        assertThat(actualDepartments.get(1).getName()).isEqualTo("Mathematics");
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void getDepartmentById_WhenDepartmentExists_ShouldReturnDepartment() {
        // Given
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department1));

        // When
        Department foundDepartment = departmentService.getDepartmentById(1L);

        // Then
        assertThat(foundDepartment).isNotNull();
        assertThat(foundDepartment.getName()).isEqualTo("Computer Science");
        assertThat(foundDepartment.getCode()).isEqualTo("CS");
        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    void getDepartmentById_WhenDepartmentDoesNotExist_ShouldThrowException() {
        // Given
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> departmentService.getDepartmentById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Department not found");

        verify(departmentRepository, times(1)).findById(99L);
    }

    @Test
    void createDepartment_WithValidData_ShouldCreateDepartment() {
        // Given
        Department newDepartment = new Department();
        newDepartment.setName("Physics");
        newDepartment.setCode("PHY");
        newDepartment.setDescription("Physics Department");

        when(departmentRepository.existsByName("Physics")).thenReturn(false);
        when(departmentRepository.existsByCode("PHY")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> {
            Department saved = invocation.getArgument(0);
            saved.setId(3L);
            return saved;
        });

        // When
        Department created = departmentService.createDepartment(newDepartment);

        // Then
        assertThat(created.getId()).isEqualTo(3L);
        assertThat(created.getName()).isEqualTo("Physics");
        assertThat(created.getCode()).isEqualTo("PHY");
        assertThat(created.getDescription()).isEqualTo("Physics Department");

        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    void createDepartment_WithExistingName_ShouldThrowException() {
        // Given
        Department newDepartment = new Department();
        newDepartment.setName("Computer Science");
        newDepartment.setCode("CS101");

        when(departmentRepository.existsByName("Computer Science")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> departmentService.createDepartment(newDepartment))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Department name already exists");

        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void createDepartment_WithExistingCode_ShouldThrowException() {
        // Given
        Department newDepartment = new Department();
        newDepartment.setName("Physics");
        newDepartment.setCode("CS");

        when(departmentRepository.existsByName("Physics")).thenReturn(false);
        when(departmentRepository.existsByCode("CS")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> departmentService.createDepartment(newDepartment))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Department code already exists");

        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void updateDepartment_WithValidData_ShouldUpdateDepartment() {
        // Given
        Department updatedDetails = new Department();
        updatedDetails.setName("Computer Science & Engineering");
        updatedDetails.setCode("CSE");
        updatedDetails.setDescription("Updated Department Description");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department1));
        when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Department updated = departmentService.updateDepartment(1L, updatedDetails);

        // Then
        assertThat(updated.getName()).isEqualTo("Computer Science & Engineering");
        assertThat(updated.getCode()).isEqualTo("CSE");
        assertThat(updated.getDescription()).isEqualTo("Updated Department Description");

        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    void updateDepartment_WhenDepartmentDoesNotExist_ShouldThrowException() {
        // Given
        Department updatedDetails = new Department();
        updatedDetails.setName("New Name");

        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> departmentService.updateDepartment(99L, updatedDetails))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Department not found");

        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void deleteDepartment_ShouldCallRepository() {
        // Given
        doNothing().when(departmentRepository).deleteById(1L);

        // When
        departmentService.deleteDepartment(1L);

        // Then
        verify(departmentRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteDepartment_WithNonExistentId_ShouldStillCallRepository() {
        // Given
        doNothing().when(departmentRepository).deleteById(99L);

        // When
        departmentService.deleteDepartment(99L);

        // Then
        verify(departmentRepository, times(1)).deleteById(99L);
    }
}