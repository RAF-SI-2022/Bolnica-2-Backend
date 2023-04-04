package com.raf.si.userservice.mapper;

import com.raf.si.userservice.dto.response.DepartmentResponse;
import com.raf.si.userservice.dto.response.HospitalResponse;
import com.raf.si.userservice.model.Department;
import com.raf.si.userservice.model.Hospital;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public DepartmentResponse modelToDepartmentResponse(Department department) {
        DepartmentResponse departmentResponse = new DepartmentResponse();
        departmentResponse.setPbo(department.getPbo());
        departmentResponse.setName(department.getName());

        return departmentResponse;
    }

    public HospitalResponse modelToHospitalResponse(Hospital hospital) {
        HospitalResponse hospitalResponse = new HospitalResponse();

        hospitalResponse.setPbb(hospital.getPbb());
        hospitalResponse.setFullName(hospital.getFullName());
        hospitalResponse.setShortName(hospital.getShortName());
        hospitalResponse.setAddress(hospital.getAddress());
        hospitalResponse.setDateOfEstablishment(hospital.getDateOfEstablishment());
        hospitalResponse.setActivity(hospital.getActivity());
        hospitalResponse.setPlace(hospital.getPlace());

        return hospitalResponse;
    }
}
