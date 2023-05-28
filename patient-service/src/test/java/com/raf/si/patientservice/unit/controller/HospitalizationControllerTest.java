package com.raf.si.patientservice.unit.controller;

import com.raf.si.patientservice.controller.HospitalizationController;
import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.dto.response.HospitalizationResponse;
import com.raf.si.patientservice.service.HospitalizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HospitalizationControllerTest {

    @Mock
    private HospitalizationService hospitalizationService;

    @InjectMocks
    private HospitalizationController hospitalizationController;

    @Test
    void hospitalize_Success() {
        HospitalizationRequest request = new HospitalizationRequest();
        HospitalizationResponse response = new HospitalizationResponse();

        when(hospitalizationService.hospitalize(request, ""))
                .thenReturn(response);

        assertEquals(hospitalizationController.hospitalization(request, ""),
                ResponseEntity.ok(response));
    }
}
