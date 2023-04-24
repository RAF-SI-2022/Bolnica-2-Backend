package com.raf.si.patientservice.unit.controller;

import com.raf.si.patientservice.controller.SchedMedExaminationController;
import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.dto.response.SchedMedExamListResponse;
import com.raf.si.patientservice.dto.response.SchedMedExamResponse;
import com.raf.si.patientservice.service.SchedMedExaminationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;


import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SchedMedExaminationControllerTest {

    private SchedMedExaminationController schedMedExaminationController;
    private SchedMedExaminationService schedMedExaminationService;



    @BeforeEach
    public void setUp(){
        schedMedExaminationService= mock(SchedMedExaminationService.class);
        schedMedExaminationController= new SchedMedExaminationController(schedMedExaminationService);
    }

    @Test
    protected void createSchedMedExamination_Success(){
        SchedMedExamRequest schedMedExamRequest= createSchedMedExamRequest();
        SchedMedExamResponse schedMedExamResponse= createSchedMedExamResponse();
        String auth= "Bearer enawoudoawnjdfoawnfuoaw";

        when(schedMedExaminationService.createSchedMedExamination(schedMedExamRequest, auth))
                .thenReturn(schedMedExamResponse);

        assertEquals(schedMedExaminationController.createSchedMedExamination(schedMedExamRequest,auth),
                ResponseEntity.of(Optional.of(schedMedExamResponse)));
    }

    @Test
    protected void updateSchedMedExaminationStatus_Success(){
        UpdateSchedMedExamRequest updateSchedMedExamRequest= createUpdateSchedMedExamRequest("U toku");
        SchedMedExamResponse schedMedExamResponse= createSchedMedExamResponse();

        when(schedMedExaminationService.updateSchedMedExaminationExamStatus(updateSchedMedExamRequest))
                .thenReturn(schedMedExamResponse);

        assertEquals(schedMedExaminationController.updateSchedMedExaminationStatus(updateSchedMedExamRequest)
                , ResponseEntity.of(Optional.of(schedMedExamResponse)));
    }

    @Test
    protected void deleteSchedMedExamination_Success(){
        Long id= 1L;
        SchedMedExamResponse schedMedExamResponse= createSchedMedExamResponse();

        when(schedMedExaminationService.deleteSchedMedExamination(id)).thenReturn(schedMedExamResponse);

        assertEquals(schedMedExaminationController.deleteSchedMedExamination(id),
                ResponseEntity.of(Optional.of(schedMedExamResponse)));
    }

    @Test
    protected void getSchedMedExam_Success(){
        String auth= "Bearer enawoudoawnjdfoawnfuoaw";
        UUID lbz= UUID.fromString("c7a6ba26-ce7a-11ed-afa1-0242ac120002");
        int page=0, size=5;
        Pageable pageable= PageRequest.of(page,size);
        Date appointmentDate= new Date();

        SchedMedExamListResponse schedMedExamListResponse= new SchedMedExamListResponse();

        when(schedMedExaminationService.getSchedMedExaminationByLbz(lbz,appointmentDate,auth,pageable))
                .thenReturn(schedMedExamListResponse);

        assertEquals(schedMedExaminationController.getSchedMedExam(auth,lbz,page,size,appointmentDate)
                , ResponseEntity.of(Optional.of(schedMedExamListResponse)));
    }

    @Test
    protected void updateSchedMedExaminationPatientArrivalStatus_Success(){
        UpdateSchedMedExamRequest updateSchedMedExamRequest= createUpdateSchedMedExamRequest("Otkazao");
        SchedMedExamResponse schedMedExamResponse= createSchedMedExamResponse();

        when(schedMedExaminationService.updateSchedMedExaminationPatientArrivalStatus(updateSchedMedExamRequest))
                .thenReturn(schedMedExamResponse);

        assertEquals(schedMedExaminationController.updateSchedMedExaminationPatientArrivalStatus(updateSchedMedExamRequest)
                , ResponseEntity.of(Optional.of(schedMedExamResponse)));
    }


    private UpdateSchedMedExamRequest createUpdateSchedMedExamRequest(String status){
        UpdateSchedMedExamRequest updateSchedMedExamRequest=new UpdateSchedMedExamRequest();
        updateSchedMedExamRequest.setId(1L);
        updateSchedMedExamRequest.setNewStatus(status);
        return updateSchedMedExamRequest;
    }

    private SchedMedExamRequest createSchedMedExamRequest(){
        SchedMedExamRequest schedMedExamRequest= new SchedMedExamRequest();
        schedMedExamRequest.setLbp(UUID.fromString("01d30a14-ce77-11ed-afa1-0242ac120002"));
        schedMedExamRequest.setLbzDoctor(UUID.fromString("11d47e16-ce77-11ed-afa1-0242ac120002"));
        schedMedExamRequest.setAppointmentDate(new Date());
        schedMedExamRequest.setNote("Note");
        schedMedExamRequest.setLbzNurse(UUID.fromString("2024a3b0-ce77-11ed-afa1-0242ac120002"));

        return  schedMedExamRequest;
    }
    private SchedMedExamResponse createSchedMedExamResponse(){return  new SchedMedExamResponse();}
}
