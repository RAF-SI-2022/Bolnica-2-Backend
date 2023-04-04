package com.raf.si.userservice.unit.service;

import com.raf.si.userservice.exception.NotFoundException;
import com.raf.si.userservice.mapper.DepartmentMapper;
import com.raf.si.userservice.model.Department;
import com.raf.si.userservice.model.Hospital;
import com.raf.si.userservice.repository.DepartmentRepository;
import com.raf.si.userservice.repository.HospitalRepository;
import com.raf.si.userservice.service.DepartmentService;
import com.raf.si.userservice.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DepartmentServiceTest {

    private DepartmentService departmentService;
    private DepartmentRepository departmentRepository;
    private HospitalRepository hospitalRepository;
    private DepartmentMapper departmentMapper;

    @BeforeEach
    public void setUp() {
        departmentRepository = mock(DepartmentRepository.class);
        hospitalRepository = mock(HospitalRepository.class);
        departmentMapper = new DepartmentMapper();
        departmentService = new DepartmentServiceImpl(departmentRepository,
                hospitalRepository, departmentMapper);
    }

    @Test
    public void getDepartmentByHospital_WhenPbbNotExist_ThrowsNotFoundException() {
        UUID pbb = UUID.randomUUID();

        when(hospitalRepository.findByPbb(pbb)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> departmentService.getDepartmentsByHospital(pbb));
    }

    @Test
    public void getDepartmentByHospital_Success() {
        UUID pbb = UUID.randomUUID();
        Hospital hospital = new Hospital();
        Department department1 = createDepartment();
        Department department2 = createDepartment();

        List<Department> departmentList = Arrays.asList(department1, department2);

        when(hospitalRepository.findByPbb(pbb)).thenReturn(Optional.of(hospital));
        when(departmentRepository.findDepartmentByHospital(hospital))
                .thenReturn(departmentList);

        assertEquals(departmentService.getDepartmentsByHospital(pbb),
                departmentList
                        .stream()
                        .map(departmentMapper::modelToDepartmentResponse)
                        .collect(Collectors.toList())
        );
    }

    @Test
    public void getAllDepartments_Success() {
        Department department1 = createDepartment();
        Department department2 = createDepartment();

        List<Department> departmentList = Arrays.asList(department1, department2);

        when(departmentRepository.findAll()).thenReturn(departmentList);

        assertEquals(departmentService.getAllDepartments(),
                departmentList
                        .stream()
                        .map(departmentMapper::modelToDepartmentResponse)
                        .collect(Collectors.toList())
        );
    }

    @Test
    public void getAllHospitals_Success() {
        Hospital hospital1 = new Hospital();
        Hospital hospital2 = new Hospital();

        List<Hospital> hospitals = Arrays.asList(hospital1, hospital2);

        when(hospitalRepository.findAll()).thenReturn(hospitals);

        assertEquals(departmentService.getAllHospitals(),
                hospitals.stream()
                        .map(departmentMapper::modelToHospitalResponse)
                        .collect(Collectors.toList())
        );
    }

    private Department createDepartment() {
        Department department = new Department();
        department.setPbo(UUID.randomUUID());
        department.setName("name");

        return department;
    }
}
