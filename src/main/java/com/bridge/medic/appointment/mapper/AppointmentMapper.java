package com.bridge.medic.appointment.mapper;

import com.bridge.medic.appointment.model.Appointment;
import com.bridge.medic.specialist.dto.ConsultationForDoctorDto;
import com.bridge.medic.user.dto.UserDto;
import com.bridge.medic.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    AppointmentMapper INSTANCE = Mappers.getMapper(AppointmentMapper.class);

    @Mapping(source = "startTime", target = "start")
    @Mapping(source = "endTime", target = "end")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "comment", target = "doctorComment")
    @Mapping(source = "user", target = "user")
    ConsultationForDoctorDto toConsultationForDoctorDto(Appointment appointment);

    @Mapping(source = "image_url", target = "imageUrl")
    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(r -> r.getName()).toList())")
    UserDto toUserDto(User user);

    List<ConsultationForDoctorDto> toConsultationForDoctorDtoList(List<Appointment> appointments);
}