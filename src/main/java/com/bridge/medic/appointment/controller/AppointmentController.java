package com.bridge.medic.appointment.controller;

import com.bridge.medic.appointment.dto.AppointmentDto;
import com.bridge.medic.appointment.dto.request.CreateAppointmentRequest;
import com.bridge.medic.appointment.exception.SpecialistNotFoundException;
import com.bridge.medic.appointment.model.Appointment;
import com.bridge.medic.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/{specialistId}")
    public ResponseEntity<List<AppointmentDto>> getAppointments(@PathVariable Long specialistId) {
        return ResponseEntity.ok(appointmentService.getAppointments(specialistId));
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

//    @GetMapping("/")
//    public ResponseEntity<?> getMyConsultations(@RequestBody CreateAppointmentRequest request) {
//        try {
//            Appointment appointment = appointmentService.bookAppointment(request);
//        } catch (SpecialistNotFoundException e) {
//            return ResponseEntity.badRequest().build();
//        }
//        return ResponseEntity.ok().build();
//    }
}