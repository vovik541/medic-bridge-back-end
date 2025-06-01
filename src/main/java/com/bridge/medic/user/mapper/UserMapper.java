package com.bridge.medic.user.mapper;

import com.bridge.medic.specialist.dto.SpecialistDto;
import com.bridge.medic.user.dto.UserDto;
import com.bridge.medic.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapRolesToStrings")
    @Mapping(source = "image_url", target = "imageUrl")
    UserDto userToUserDto(User user);

    @Named("mapRolesToStrings")
    static List<String> mapRolesToStrings(List<com.bridge.medic.config.security.authorization.model.Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(com.bridge.medic.config.security.authorization.model.Role::getName)
                .collect(Collectors.toList());
    }

    @Mapping(source = "image_url", target = "imageUrl")
    SpecialistDto userToSpecialistDto(User user);

    @Mapping(source = "image_url", target = "imageUrl")
    List<SpecialistDto> usersToSpecialistDtos(List<User> users);
}
