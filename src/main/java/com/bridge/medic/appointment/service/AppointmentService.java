package com.bridge.medic.appointment.service;

import com.bridge.medic.appointment.AppointmentStatus;
import com.bridge.medic.appointment.dto.AppointmentDto;
import com.bridge.medic.appointment.dto.AvailableSlotDto;
import com.bridge.medic.appointment.dto.request.CreateAppointmentRequest;
import com.bridge.medic.appointment.exception.SpecialistNotFoundException;
import com.bridge.medic.appointment.model.Appointment;
import com.bridge.medic.appointment.repository.AppointmentRepository;
import com.bridge.medic.config.security.service.AuthenticatedUserService;
import com.bridge.medic.mail.EmailDetails;
import com.bridge.medic.mail.EmailService;
import com.bridge.medic.specialist.dto.RescheduleAppointmentRequest;
import com.bridge.medic.specialist.model.SpecialistData;
import com.bridge.medic.specialist.model.SpecialistDoctorType;
import com.bridge.medic.specialist.repository.SpecialistDataRepository;
import com.bridge.medic.specialist.repository.SpecialistDoctorTypeRepository;
import com.bridge.medic.storage.service.LocalStorageService;
import com.bridge.medic.storage.service.S3StorageService;
import com.bridge.medic.user.model.User;
import com.bridge.medic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final SpecialistDoctorTypeRepository specialistDoctorTypeRepository;
    private final SpecialistDataRepository specialistDataRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final LocalStorageService localStorageService;
    private final EmailService emailService;
    private final S3StorageService s3StorageService;

    public List<AvailableSlotDto> getAvailableSlots(Long consultationId, OffsetDateTime date) {
        ZoneOffset offset = date.getOffset();

        //zone -3 h Kyiv 8:00 - 20:00
        OffsetDateTime dayStart = date.toLocalDate().atTime(5, 0).atOffset(offset);
        OffsetDateTime dayEnd = date.toLocalDate().atTime(17, 0).atOffset(offset);

        Long specialistId = appointmentRepository.findById(consultationId).orElseThrow().getSpecialistData().getUser().getId();
        List<Appointment> appointments = appointmentRepository
                .findAppointmentsBySpecialistIdAndTimeRange(specialistId, dayStart, dayEnd);

        Set<OffsetDateTime> busyStarts = appointments.stream()
                .map(Appointment::getStartTime)
                .collect(Collectors.toSet());

        List<AvailableSlotDto> slots = new ArrayList<>();
        OffsetDateTime current = dayStart;
        OffsetDateTime now = OffsetDateTime.now();

        while (!current.isAfter(dayEnd.minusMinutes(30))) {
            if (!busyStarts.contains(current) && now.isBefore(current)) {
                slots.add(new AvailableSlotDto(current, current.plusMinutes(30)));
            }
            current = current.plusMinutes(30);
        }

        return slots;
    }

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

    private void closePassedTimeAppointment(List<Appointment> pendingAppointments) {
        Appointment appointment;
        Iterator<Appointment> iterator = pendingAppointments.iterator();
        OffsetDateTime currentTime = OffsetDateTime.now();
        while (iterator.hasNext()) {
            appointment = iterator.next();
            if (appointment.getStatus().equals(AppointmentStatus.PENDING) && currentTime.isAfter(appointment.getEndTime())) {
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

    //просто всі відмінені
    public List<Appointment> getRescheduledAppointmentsBySpecialistId(Long specialistId) {
        return appointmentRepository.findAppointmentsBySpecialistIdAndStatus(specialistId, AppointmentStatus.RESCHEDULED);
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

    public List<Appointment> getAppointmentToBeApprovedByUser(Long userId) {
        return appointmentRepository.findAllByUser_IdAndStatus(userId, AppointmentStatus.RESCHEDULED);
    }

    public void rescheduleAppointment(RescheduleAppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        //TODO validate reschedule time
        appointment.setStartTime(request.getNewStart());
        appointment.setEndTime(request.getNewEnd());
        appointment.setStatus(AppointmentStatus.RESCHEDULED);

        appointmentRepository.save(appointment);

        emailService.sendSimpleMail(EmailDetails.builder()
                .msgBody(buildRescheduleAppointmentMessage(appointment))
                .recipient(appointment.getUser().getEmail())
                .subject("Лікар запропонував новий час для зустрічі")
                .build());
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
        Appointment.AppointmentBuilder appointmentBuilder = Appointment.builder()
                .status(AppointmentStatus.PENDING)
                .description(request.getDescription())
                .user(authenticatedUserService.getCurrentUser())
                .specialistData(specialistData)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime());

        if (attachedDocument != null && !attachedDocument.isEmpty()) {
            Long userId = authenticatedUserService.getCurrentUser().getId();
            String key = "appointment/" + userId + "/" + System.currentTimeMillis() + "_" + attachedDocument.getOriginalFilename();

            s3StorageService.upload(key, attachedDocument);

            String fileLink = key;
            appointmentBuilder.attachedDocumentUrl(fileLink);
        }

        return appointmentRepository.save(appointmentBuilder.build());
    }


    public void approveAppointment(Long appointmentId, String comment, String meetingLink, User specialist) {
        Appointment appointment = appointmentRepository.getById(appointmentId);
        if (appointment.getStatus().equals(AppointmentStatus.CONFIRMED)) {
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

    public void cancelAppointment(Long appointmentId, String comment, User specialist) {
        Appointment appointment = appointmentRepository.getById(appointmentId);
//        if (appointment.getStatus().equals(AppointmentStatus.CONFIRMED)){
//            return;
//        }

        appointment.setStatus(AppointmentStatus.CANCELED);
        appointment.setComment(comment);
        appointmentRepository.save(appointment);
        emailService.sendSimpleMail(EmailDetails.builder()
                .subject("BridgeMedic консультацію скасовано")
                .recipient(appointment.getUser().getEmail())
                .msgBody(buildCancelAppointmentMessage(specialist, appointment))
                .build());
    }

    public Long getSpecialistIdByAppointmentId(Long appointmentId) {
        return appointmentRepository.findById(appointmentId).orElseThrow().getSpecialistData().getUser().getId();
    }

    public void updateStatusByUserChoice(Long appointmentId, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow();

        if (!(newStatus.equals(AppointmentStatus.CONFIRMED)
                || newStatus.equals(AppointmentStatus.CANCELED))
                && !appointment.getStatus().equals(AppointmentStatus.RESCHEDULED)
        ) {
            return;
        }

        appointment.setStatus(newStatus);
        appointmentRepository.save(appointment);

        User user = authenticatedUserService.getCurrentUser();
        String message;
        String subject;

        if (newStatus.equals(AppointmentStatus.CONFIRMED)) {
            subject = "Пацієнт підтвердив запис на консультацію";
            message = "Консультацію погоджено! " + user.getFirstName() + " " + user.getLastName() + System.lineSeparator()
                    + "Чекаємо на зустріч о " + appointment.getStartTime().toString();
        } else {
            subject = "Пацієнт скасував консультацію";
            message = "Консультацію скасовано! " + user.getFirstName() + " " + user.getLastName() + System.lineSeparator()
                    + "Час консультації " + appointment.getStartTime().toString();
        }

        emailService.sendSimpleMail(EmailDetails.builder()
                .subject(subject)
                .recipient(appointment.getSpecialistData().getUser().getEmail())
                .msgBody(message)
                .build());

        message = newStatus.equals(AppointmentStatus.CONFIRMED)
                ? buildApproveAppointmentMessage(appointment.getSpecialistData().getUser(), appointment)
                : buildCancelAppointmentMessage(appointment.getSpecialistData().getUser(), appointment);

        subject = newStatus.equals(AppointmentStatus.CONFIRMED)
                ? "Консультацію погоджено!"
                : "Консультацію скасовано!";

        emailService.sendSimpleMail(EmailDetails.builder()
                .subject(subject)
                .recipient(user.getEmail())
                .msgBody(message)
                .build());
    }

    private String buildApproveAppointmentMessage(User specialist, Appointment appointment) {
        String ln = System.lineSeparator();
        return "Консультацію з " + specialist.getFirstName() + " " + specialist.getLastName() + " підтверджено." + ln
                + "Коментар: " + appointment.getComment() + ln
                + "Посилання: " + appointment.getMeetingLink() + ln
                + "Час: " + appointment.getStartTime().toString() + " - " + appointment.getEndTime().toString();
    }

    private String buildCancelAppointmentMessage(User specialist, Appointment appointment) {
        String ln = System.lineSeparator();
        return "Консультацію з " + specialist.getFirstName() + " " + specialist.getLastName() + " скасовано." + ln
                + "Коментар: " + appointment.getComment() + ln
                + "Час: " + appointment.getStartTime().toString() + " - " + appointment.getEndTime().toString();
    }

    private String buildRescheduleAppointmentMessage(Appointment appointment) {
        String ln = System.lineSeparator();
        return "Консультацію з " + appointment.getSpecialistData().getUser().getFirstName() + " "
                + appointment.getSpecialistData().getUser().getLastName()
                + " було перенесено на інший час у зв'язку із занятістю лікаря." + ln
                + "Час: " + appointment.getStartTime().toString() + " - " + appointment.getEndTime().toString() + ln
                + "підтвердіть будь ласка, чи підходить даний час та день. Якщо ж ні - забронюйте нову зустріч!";
    }
}