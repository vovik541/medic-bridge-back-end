package com.bridge.medic.appointment.service;

import com.bridge.medic.appointment.AppointmentStatus;
import com.bridge.medic.appointment.dto.AppointmentDto;
import com.bridge.medic.appointment.dto.request.CreateAppointmentRequest;
import com.bridge.medic.appointment.exception.SpecialistNotFoundException;
import com.bridge.medic.appointment.model.Appointment;
import com.bridge.medic.appointment.repository.AppointmentRepository;
import com.bridge.medic.config.security.service.AuthenticatedUserService;
import com.bridge.medic.specialist.model.SpecialistData;
import com.bridge.medic.specialist.model.SpecialistDoctorType;
import com.bridge.medic.specialist.repository.SpecialistDataRepository;
import com.bridge.medic.specialist.repository.SpecialistDoctorTypeRepository;
import com.bridge.medic.user.model.User;
import com.bridge.medic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final SpecialistDoctorTypeRepository specialistDoctorTypeRepository;
    private final SpecialistDataRepository specialistDataRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public List<AppointmentDto> getAppointments(Long specialistId) {
        return appointmentRepository.findAllBySpecialistId(specialistId).stream()
                .map(a -> new AppointmentDto(a.getStartTime(), a.getEndTime()))
                .collect(Collectors.toList());
    }

    public Appointment bookAppointment(CreateAppointmentRequest request) throws SpecialistNotFoundException {
        Optional<User> byId = userRepository.findById(Math.toIntExact(request.getSpecialistId()));
        if (byId.isEmpty())
            throw new SpecialistNotFoundException();

        User specialist = byId.get();

        List<SpecialistData> specialistDataList = specialistDataRepository.findAllByUserId(specialist.getId());

        if (specialistDataList.isEmpty())
            throw new SpecialistNotFoundException();

        List<SpecialistDoctorType> specialistCertifications = specialistDataList.getFirst().getSpecialistDoctorTypes();
        for (SpecialistDoctorType certification : specialistCertifications) {
            if (certification.getDoctorType().getName().equals(request.getDoctorType())
                    && certification.isApproved()) {
                if (isAvailableForBooking(request)) {

                    saveAppointment(request, specialistDataList.getFirst());

                }
            }
        }

        throw new SpecialistNotFoundException();


    }

    private boolean isAvailableForBooking(CreateAppointmentRequest request) {
        //TODO right now only front-end defended
        return true;
    }

    private void saveAppointment(CreateAppointmentRequest request, SpecialistData specialistData) {
        Appointment appointment = Appointment.builder()
                .status(AppointmentStatus.PENDING)
                .description("I feel like I need some consultation about ....")
                .user(authenticatedUserService.getCurrentUser())
                .specialistData(specialistData)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();
        appointmentRepository.save(appointment);
    }
}