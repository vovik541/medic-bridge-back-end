package com.bridge.medic.appointment.controller;

import com.bridge.medic.appointment.dto.AppointmentDto;
import com.bridge.medic.appointment.dto.AvailableSlotDto;
import com.bridge.medic.appointment.dto.ConsultationDto;
import com.bridge.medic.appointment.dto.request.CreateAppointmentRequest;
import com.bridge.medic.appointment.dto.request.UpdateStatusRequest;
import com.bridge.medic.appointment.dto.response.GetUserConsultationsResponse;
import com.bridge.medic.appointment.exception.SpecialistNotFoundException;
import com.bridge.medic.appointment.model.Appointment;
import com.bridge.medic.appointment.service.AppointmentService;
import com.bridge.medic.config.security.service.AuthenticatedUserService;
import com.bridge.medic.specialist.dto.RescheduleAppointmentRequest;
import com.bridge.medic.user.mapper.UserMapper;
import com.bridge.medic.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AuthenticatedUserService authenticatedUserService;
    private final UserMapper userMapper;

    @GetMapping("/available/{consultationId}")
    public ResponseEntity<List<AvailableSlotDto>> getAvailableSlots(
            @PathVariable Long consultationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime offsetDate) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(consultationId, offsetDate));
    }

    @GetMapping("/{specialistId}")
    public ResponseEntity<List<AppointmentDto>> getAppointments(@PathVariable Long specialistId) {
        return ResponseEntity.ok(appointmentService.getAppointmentDtosBySpecialist(specialistId));
    }

    @PostMapping(value = "/book", consumes = {"multipart/form-data"})
    public ResponseEntity<?> bookAppointment(
            @RequestPart("appointment") CreateAppointmentRequest request,
            @RequestPart(value = "attachedDocument", required = false) MultipartFile attachedDocument
    ) {
        try {
            appointmentService.bookAppointment(request, attachedDocument);
            return ResponseEntity.ok().build();
        } catch (SpecialistNotFoundException e) {
            return ResponseEntity.badRequest().body("Спеціаліста не знайдено");
        }
    }

    @GetMapping("/my-appointments")
    public ResponseEntity<GetUserConsultationsResponse> getConsultations() {
        User currentUser = authenticatedUserService.getCurrentUser();
        List<Appointment> appointmentsByUser = appointmentService.getAppointmentByUserId(currentUser.getId());

        List<ConsultationDto> consultations = new LinkedList<>();
        for (Appointment appointment : appointmentsByUser){
            consultations.add(ConsultationDto.builder()
                            .id(appointment.getId())
                            .comment(appointment.getComment())
                            .end(appointment.getEndTime())
                            .start(appointment.getStartTime())
                            .summary(appointment.getSummary())
                            .doctor(userMapper.userToUserDto(appointment.getSpecialistData().getUser()))
                            .description(appointment.getDescription())
                            .status(appointment.getStatus().name())
                            .attachedDocumentUrl(appointment.getAttachedDocumentUrl())
                            .meetingLink(appointment.getMeetingLink())
                    .build());
        }

        GetUserConsultationsResponse response = new GetUserConsultationsResponse();
        response.setConsultations(consultations.reversed());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/to-approve")
    public ResponseEntity<GetUserConsultationsResponse> getToApproveAppointments() {
        User currentUser = authenticatedUserService.getCurrentUser();
        List<Appointment> appointmentsByUser = appointmentService.getAppointmentToBeApprovedByUser(currentUser.getId());

        List<ConsultationDto> consultations = new LinkedList<>();
        for (Appointment appointment : appointmentsByUser){
            consultations.add(ConsultationDto.builder()
                    .id(appointment.getId())
                    .comment(appointment.getComment())
                    .end(appointment.getEndTime())
                    .start(appointment.getStartTime())
                    .summary(appointment.getSummary())
                    .doctor(userMapper.userToUserDto(appointment.getSpecialistData().getUser()))
                    .description(appointment.getDescription())
                    .status(appointment.getStatus().name())
                    .attachedDocumentUrl(appointment.getAttachedDocumentUrl())
                    .meetingLink(appointment.getMeetingLink())
                    .build());
        }

        GetUserConsultationsResponse response = new GetUserConsultationsResponse();
        response.setConsultations(consultations.reversed());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reschedule")
    public ResponseEntity<Void> rescheduleAppointment(@RequestBody RescheduleAppointmentRequest request) {
        appointmentService.rescheduleAppointment(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/approve-appointment")
    public ResponseEntity<?> approveAppointment(@RequestParam("appointmentId") Long appointmentId,
                                                @RequestParam("message") String comment,
                                                @RequestParam("appointmentLink") String appointmentLink) {
        User currentSpecialist = authenticatedUserService.getCurrentUser();
        if (!currentSpecialist.getId().equals(appointmentService.getSpecialistIdByAppointmentId(appointmentId))){
            return ResponseEntity.unprocessableEntity().build();
        }
        appointmentService.approveAppointment(appointmentId, comment, appointmentLink, currentSpecialist);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel-appointment")
    public ResponseEntity<?> cancelAppointment(@RequestParam("appointmentId") Long appointmentId,
                                                @RequestParam("message") String comment) {
        User currentSpecialist = authenticatedUserService.getCurrentUser();
        if (!currentSpecialist.getId().equals(appointmentService.getSpecialistIdByAppointmentId(appointmentId))){
            return ResponseEntity.unprocessableEntity().build();
        }
        appointmentService.cancelAppointment(appointmentId, comment, currentSpecialist);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-status-user-choice")
    public ResponseEntity<Void> updateAppointmentStatus(@RequestBody UpdateStatusRequest request) {
        appointmentService.updateStatusByUserChoice(request.getAppointmentId(), request.getNewStatus());
        return ResponseEntity.ok().build();
    }
}