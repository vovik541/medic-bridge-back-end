package com.bridge.medic.specialist.controller;

import com.bridge.medic.appointment.mapper.AppointmentMapper;
import com.bridge.medic.appointment.model.Appointment;
import com.bridge.medic.appointment.service.AppointmentService;
import com.bridge.medic.config.security.service.AuthenticatedUserService;
import com.bridge.medic.specialist.dto.ConsultationForDoctorDto;
import com.bridge.medic.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor")
@Tag(name = "Doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final AppointmentService appointmentService;
    private final AuthenticatedUserService authenticatedUserService;
    private final AppointmentMapper appointmentMapper;

    @Operation(
            description = "Get endpoint for doctor",
            summary = "This is a summary for doctor get endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }

    )
    //Ще треба підтвердити
    @GetMapping("appointments/ongoing-upApproved")
    public ResponseEntity<List<ConsultationForDoctorDto>> getUnApprovedOngoingAppointments() {
        User doctor = authenticatedUserService.getCurrentUser();
        List<Appointment> pending = appointmentService.getPendingAppointmentsBySpecialistId(doctor.getId());
        List<ConsultationForDoctorDto> consultations = appointmentMapper.toConsultationForDoctorDtoList(pending);
        return ResponseEntity.ok(consultations);
    }

    //підтверджені CONFIRMED, мають відбутися
    @GetMapping("appointments/ongoing-approved")
    public ResponseEntity<List<ConsultationForDoctorDto>> getApprovedOngoingAppointments() {
        return ResponseEntity.ok(appointmentMapper
                .toConsultationForDoctorDtoList(appointmentService
                        .getApprovedOngoingAppointmentsBySpecialistId(authenticatedUserService.getCurrentUser().getId())));
    }

    //треба ще оцінити Окрема задача (блок)
    @GetMapping("appointments/to-review")
    public ResponseEntity<List<ConsultationForDoctorDto>> getPastUnreviewedAppointments() {
        return ResponseEntity.ok(appointmentMapper
                .toConsultationForDoctorDtoList(appointmentService
                        .getNotReviewedAppointmentsSpecialistId(authenticatedUserService.getCurrentUser().getId())));
    }

    //всі минули оцінені і не оцінені
    @GetMapping("appointments/past")
    public ResponseEntity<List<ConsultationForDoctorDto>> getPastAppointments() {
        return ResponseEntity.ok(appointmentMapper
                .toConsultationForDoctorDtoList(appointmentService
                        .getPassedAppointmentsBySpecialistId(authenticatedUserService.getCurrentUser().getId())));
    }

    //всі які відмовили або прострочили
    @GetMapping("appointments/rejected")
    public ResponseEntity<List<ConsultationForDoctorDto>> getRejectedAppointments() {
        return ResponseEntity.ok(appointmentMapper
                .toConsultationForDoctorDtoList(appointmentService
                        .getCanceledAppointmentsBySpecialistId(authenticatedUserService.getCurrentUser().getId())));
    }

}
