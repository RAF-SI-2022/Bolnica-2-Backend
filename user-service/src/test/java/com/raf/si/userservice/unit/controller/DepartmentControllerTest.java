package com.raf.si.userservice.unit.controller;

import com.raf.si.userservice.controller.DepartmentController;
import com.raf.si.userservice.dto.response.DepartmentResponse;
import com.raf.si.userservice.dto.response.HospitalResponse;
import com.raf.si.userservice.service.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DepartmentControllerTest {

    private DepartmentService departmentService;
    private DepartmentController departmentController;

    @BeforeEach
    public void setUp() {
        departmentService = mock(DepartmentService.class);
        departmentController = new DepartmentController(departmentService);
    }

    @Test
    public void getDepartmentsByHospital_Success() {
        UUID pbb = UUID.randomUUID();
        DepartmentResponse departmentResponse1 = createDepartmentResponse();
        DepartmentResponse departmentResponse2 = createDepartmentResponse();

        List<DepartmentResponse> departmentResponseList = Arrays.asList(departmentResponse1, departmentResponse2);

        when(departmentService.getDepartmentsByHospital(pbb))
                .thenReturn(departmentResponseList);

        assertEquals(departmentController.getDepartmentsByHospital(pbb),
                ResponseEntity.of(Optional.of(departmentResponseList)));
    }

    @Test
    public void getDepartments_Success() {
        DepartmentResponse departmentResponse1 = createDepartmentResponse();
        DepartmentResponse departmentResponse2 = createDepartmentResponse();

        List<DepartmentResponse> departmentResponseList = Arrays.asList(departmentResponse1, departmentResponse2);

        when(departmentService.getAllDepartments())
                .thenReturn(departmentResponseList);

        assertEquals(departmentController.getDepartments(),
                ResponseEntity.of(Optional.of(departmentResponseList)));
    }

    @Test
    public void getHospitals_Success() {
        HospitalResponse hospitalResponse1 = createHospitalResponse();
        HospitalResponse hospitalResponse2 = createHospitalResponse();


        List<HospitalResponse> hospitalResponses = Arrays.asList(hospitalResponse1, hospitalResponse2);

        when(departmentService.getAllHospitals())
                .thenReturn(hospitalResponses);

        assertEquals(departmentController.getHospitals(),
                ResponseEntity.of(Optional.of(hospitalResponses)));
    }

    private DepartmentResponse createDepartmentResponse() {
        DepartmentResponse departmentResponse = new DepartmentResponse();
        departmentResponse.setPbo(UUID.randomUUID());
        departmentResponse.setName("name");

        return departmentResponse;
    }

    private HospitalResponse createHospitalResponse() {
        HospitalResponse hospitalResponse = new HospitalResponse();
        hospitalResponse.setPbb(UUID.randomUUID());
        hospitalResponse.setActivity("activity");
        hospitalResponse.setPlace("place");
        hospitalResponse.setAddress("address");

        return hospitalResponse;
    }
}
