package com.bridge.medic.appointment.service;

import com.bridge.medic.appointment.AppointmentStatus;
import com.bridge.medic.appointment.dto.AppointmentDto;
import com.bridge.medic.appointment.dto.request.CreateAppointmentRequest;
import com.bridge.medic.appointment.exception.SpecialistNotFoundException;
import com.bridge.medic.appointment.model.Appointment;
import com.bridge.medic.appointment.model.Appointment.AppointmentBuilder;
import com.bridge.medic.appointment.repository.AppointmentRepository;
import com.bridge.medic.config.security.service.AuthenticatedUserService;
import com.bridge.medic.mail.EmailDetails;
import com.bridge.medic.mail.EmailService;
import com.bridge.medic.specialist.model.SpecialistData;
import com.bridge.medic.specialist.model.SpecialistDoctorType;
import com.bridge.medic.specialist.repository.SpecialistDataRepository;
import com.bridge.medic.specialist.repository.SpecialistDoctorTypeRepository;
import com.bridge.medic.storage.service.FileLocalStorageService;
import com.bridge.medic.user.model.User;
import com.bridge.medic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.Iterator;
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
    private final FileLocalStorageService fileLocalStorageService;
    private final EmailService emailService;

    public List<AppointmentDto> getAppointmentDtosBySpecialist(Long specialistId) {
        return appointmentRepository.findAllBySpecialistId(specialistId).stream()
                .map(a -> AppointmentDto.builder()
                        .start(a.getStartTime())
                        .end(a.getEndTime())
                        .summary(a.getSummary())
                        .meetingLink(a.getMeetingLink())
                        .status(a.getStatus().name())
                        .attachedDocumentUrl(a.getAttachedDocumentUrl())
                        .build())
                .collect(Collectors.toList());
    }

    //чекають підтвердження
    public List<Appointment> getPendingAppointmentsBySpecialistId(Long specialistId) {
        List<Appointment> appointments = appointmentRepository.findAppointmentsBySpecialistIdAndStatus(specialistId, AppointmentStatus.PENDING);
        closePassedTimeAppointment(appointments);
        return appointments;
    }

    private void closePassedTimeAppointment(List<Appointment> pendingAppointments){
        Appointment appointment;
        Iterator<Appointment> iterator = pendingAppointments.iterator();
        OffsetDateTime currentTime = OffsetDateTime.now();
        while (iterator.hasNext()){
            appointment = iterator.next();
            if (appointment.getStatus().equals(AppointmentStatus.PENDING) && currentTime.isAfter(appointment.getEndTime())){
                appointment.setStatus(AppointmentStatus.CANCELED);
                appointment.setSummary("Лікар не встиг відповісти на ваше бронювання");
                appointmentRepository.save(appointment);
                iterator.remove();
            }
        }
    }
    //підтверджені, мають відбутися
    public List<Appointment> getApprovedOngoingAppointmentsBySpecialistId(Long specialistId) {
        return appointmentRepository.findAppointmentsBySpecialistIdAndStatusAfterTime(specialistId,
                AppointmentStatus.CONFIRMED, OffsetDateTime.now());
    }

    //Оцінені і не оцінені PASSED / CONFIRMED
    public List<Appointment> getPassedAppointmentsBySpecialistId(Long specialistId) {
        List<Appointment> pastAppointments = appointmentRepository
                .findAppointmentsBySpecialistIdAndBeforeTimeAndStatusNotIn(specialistId,
                        OffsetDateTime.now(), List.of(AppointmentStatus.CANCELED, AppointmentStatus.PENDING));
        return pastAppointments;
    }

    //треба оцінити, відбулися CONFIRMED
    public List<Appointment> getNotReviewedAppointmentsSpecialistId(Long specialistId) {
        return appointmentRepository.findAppointmentsBySpecialistIdAndStatusBeforeTime(specialistId, AppointmentStatus.CONFIRMED, OffsetDateTime.now());
    }

    //просто всі відмінені
    public List<Appointment> getCanceledAppointmentsBySpecialistId(Long specialistId) {
        return appointmentRepository.findAppointmentsBySpecialistIdAndStatus(specialistId, AppointmentStatus.CANCELED);
    }

    public List<AppointmentDto> getAppointmentDtosByUser(Long userId) {
        return appointmentRepository.findAllByUser_Id(userId).stream()
                .map(a -> AppointmentDto.builder()
                        .start(a.getStartTime())
                        .end(a.getEndTime())
                        .build())
                .collect(Collectors.toList());
    }
    public List<Appointment> getAppointmentByUserId(Long userId) {

        return appointmentRepository.findAllByUser_Id(userId);
    }

    public Appointment bookAppointment(CreateAppointmentRequest request, MultipartFile attachedDocument) throws SpecialistNotFoundException {
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
                    return saveAppointment(request, specialistDataList.getFirst(), attachedDocument);
                }
            }
        }

        throw new SpecialistNotFoundException();
    }

    private boolean isAvailableForBooking(CreateAppointmentRequest request) {
        //TODO right now only front-end defended
        return true;
    }

    private Appointment saveAppointment(CreateAppointmentRequest request, SpecialistData specialistData, MultipartFile attachedDocument) {
        AppointmentBuilder appointmentBuilder = Appointment.builder()
                .status(AppointmentStatus.PENDING)
                .description(request.getDescription())
                .user(authenticatedUserService.getCurrentUser())
                .specialistData(specialistData)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime());
        if (attachedDocument != null && !attachedDocument.isEmpty()) {
            String fileLink = fileLocalStorageService.storeFile(attachedDocument);
            appointmentBuilder.attachedDocumentUrl(fileLink);
        }
        return appointmentRepository.save(appointmentBuilder.build());
    }

    public void approveAppointment(Long appointmentId, String comment, String meetingLink, User specialist){
        Appointment appointment = appointmentRepository.getById(appointmentId);
        if (appointment.getStatus().equals(AppointmentStatus.CONFIRMED)){
            return;
        }
        
        appointment.setMeetingLink(meetingLink);
        appointment.setComment(comment);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);
        emailService.sendSimpleMail(EmailDetails.builder()
                        .subject("BridgeMedic консультацію підтверджено")
                        .recipient(appointment.getUser().getEmail())
                        .msgBody(buildApproveAppointmentMessage(specialist, appointment))
                .build());
    }

    public Long getSpecialistIdByAppointmentId(Long appointmentId){
        return appointmentRepository.findById(appointmentId).get().getSpecialistData().getUser().getId();
    }

    private String buildApproveAppointmentMessage(User specialist, Appointment appointment){
        String ln = System.lineSeparator();
        return "Консультацію з " + specialist.getFirstName() + " " + specialist.getLastName() + " підтверджено." + ln
                + "Коментар: " + appointment.getComment() + ln
                + "Посилання: " + appointment.getMeetingLink() + ln
                + "Час: " + appointment.getStartTime().toString() + " - " + appointment.getEndTime().toString();
    }
}