package com.bridge.medic.appointment.controller;

import com.bridge.medic.appointment.dto.AppointmentDto;
import com.bridge.medic.appointment.dto.ConsultationDto;
import com.bridge.medic.appointment.dto.request.CreateAppointmentRequest;
import com.bridge.medic.appointment.dto.response.GetUserConsultationsResponse;
import com.bridge.medic.appointment.exception.SpecialistNotFoundException;
import com.bridge.medic.appointment.model.Appointment;
import com.bridge.medic.appointment.service.AppointmentService;
import com.bridge.medic.config.security.service.AuthenticatedUserService;
import com.bridge.medic.user.mapper.UserMapper;
import com.bridge.medic.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AuthenticatedUserService authenticatedUserService;
    private final UserMapper userMapper;

    @GetMapping("/{specialistId}")
    public ResponseEntity<List<AppointmentDto>> getAppointments(@PathVariable Long specialistId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsBySpecialist(specialistId));
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody CreateAppointmentRequest request) {
        try {
            Appointment appointment = appointmentService.bookAppointment(request);
        } catch (SpecialistNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-appointments")
    public ResponseEntity<GetUserConsultationsResponse> getConsultations() {
        User currentUser = authenticatedUserService.getCurrentUser();
        List<Appointment> appointmentsByUser = appointmentService.getAppointmentByUserId(currentUser.getId());

        List<ConsultationDto> consultations = new LinkedList<>();
        for (Appointment appointment : appointmentsByUser){
            consultations.add(ConsultationDto.builder()
                            .id(appointment.getId())
                            .end(appointment.getEndTime())
                            .start(appointment.getStartTime())
                            .summary(appointment.getSummary())
                            .doctor(userMapper.userToUserDto(appointment.getSpecialistData().getUser()))
                            .description(appointment.getDescription())
                            .status(appointment.getStatus().name())
                    .build());
        }

        GetUserConsultationsResponse response = new GetUserConsultationsResponse();
        response.setConsultations(consultations);
        return ResponseEntity.ok(response);
    }
}