package com.raf.si.userservice.service.impl;

import com.raf.si.userservice.dto.response.DepartmentResponse;
import com.raf.si.userservice.dto.response.HospitalResponse;
import com.raf.si.userservice.exception.NotFoundException;
import com.raf.si.userservice.mapper.DepartmentMapper;
import com.raf.si.userservice.model.Hospital;
import com.raf.si.userservice.repository.DepartmentRepository;
import com.raf.si.userservice.repository.HospitalRepository;
import com.raf.si.userservice.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository,
                                 HospitalRepository hospitalRepository,
                                 DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.hospitalRepository = hospitalRepository;
        this.departmentMapper = departmentMapper;
    }

    @Override
    public List<DepartmentResponse> getDepartmentsByHospital(UUID pbb) {
        Hospital hospital = hospitalRepository.findByPbb(pbb).orElseThrow(() -> {
            log.error("Bolnica sa pbb '{}' ne postoji", pbb);
            throw new NotFoundException("Bolnica sa datim pbb ne postoji");
        });

        log.info("Listanje svih odeljenja na osnovu pbb bolnice '{}", pbb);
        return departmentRepository.findDepartmentByHospital(hospital)
                .stream()
                .map(departmentMapper::modelToDepartmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentResponse> getAllDepartments() {
        log.info("Listanje svih odeljenja..");
        return departmentRepository.findAll()
                .stream()
                .map(departmentMapper::modelToDepartmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<HospitalResponse> getAllHospitals() {
        log.info("Listanje svih bolnica..");
        return hospitalRepository.findAll()
                .stream()
                .map(departmentMapper::modelToHospitalResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentResponse> getDepartmentsByName(String name) {
        log.info("Listanje svih departmana po imenu {}", name);
        return departmentRepository.findDepartmenstByName(name)
                .stream()
                .map(departmentMapper::modelToDepartmentResponse)
                .collect(Collectors.toList());
    }
}
