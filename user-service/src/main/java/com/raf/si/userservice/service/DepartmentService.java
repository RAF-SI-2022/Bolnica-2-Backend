package com.raf.si.userservice.service;

import com.raf.si.userservice.dto.response.DepartmentResponse;
import com.raf.si.userservice.dto.response.HospitalResponse;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    List<DepartmentResponse> getDepartmentsByHospital(UUID pbb);

    List<DepartmentResponse> getAllDepartments();

    List<HospitalResponse> getAllHospitals();

    List<DepartmentResponse> getDepartmentsByName(String name);
}
