package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.CreateAppointmentRequest;
import com.raf.si.patientservice.dto.response.AppointmentListResponse;
import com.raf.si.patientservice.dto.response.AppointmentResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.mapper.AppointmentMapper;
import com.raf.si.patientservice.model.Appointment;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.enums.appointment.AppointmentStatus;
import com.raf.si.patientservice.repository.AppointmentRepository;
import com.raf.si.patientservice.repository.filtering.filter.AppointmentFilter;
import com.raf.si.patientservice.repository.filtering.specification.AppointmentSpecification;
import com.raf.si.patientservice.service.AppointmentService;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;
    private final AppointmentMapper appointmentMapper;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  PatientService patientService,
                                  AppointmentMapper appointmentMapper) {

        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
        Patient patient = patientService.findPatient(request.getLbp());
        checkDate(request.getReceiptDate());

        Appointment appointment = appointmentMapper.createAppointmentRequestToAppointment(
                request,
                patient
        );

        appointment = appointmentRepository.save(appointment);
        log.info("Zakazan termin prijema sa id-jem " + appointment.getId());
        return appointmentMapper.appointmentToAppointmentResponse(appointment);
    }

    @Override
    public AppointmentListResponse getAppointments(UUID lbp, Date date, Pageable pageable) {
        Patient patient = lbp == null? null: patientService.findPatient(lbp);

        AppointmentFilter filter = new AppointmentFilter(
                patient,
                date,
                TokenPayloadUtil.getTokenPayload().getPbo()
        );
        AppointmentSpecification spec = new AppointmentSpecification(filter);

        Page<Appointment> appointments = appointmentRepository.findAll(spec, pageable);
        return appointmentMapper.appointmentsToAppointmentListResponse(appointments);
    }

    @Override
    public AppointmentResponse changeStatus(Long id, String status) {
        Appointment appointment = getAppointment(id);

        AppointmentStatus appointmentStatus = AppointmentStatus.valueOfNotation(status);
        if (appointmentStatus == null) {
            String errMessage = String.format("Status \'%s\' ne postoji", status);
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }

        appointment.setStatus(appointmentStatus);
        appointment = appointmentRepository.save(appointment);

        log.info(String.format("Promenjen status pregleda sa id-jem %d na %s", id, status));
        return appointmentMapper.appointmentToAppointmentResponse(appointment);
    }

    @Override
    public Appointment getAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    String errMessage = String.format("Zakazana poseta sa id-jem %d ne postoji", id);
                    log.error(errMessage);
                    throw new BadRequestException(errMessage);
                });
    }

    private void checkDate(Date date){
        Date currDate = new Date();
        if (currDate.after(date)) {
            String errMessage = String.format("Datum %s je u proslosti", date.toString());
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
    }
}
