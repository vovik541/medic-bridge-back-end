package com.bridge.medic.user.controller;

import com.bridge.medic.config.security.service.AuthenticatedUserService;
import com.bridge.medic.specialist.dto.SpecialistDto;
import com.bridge.medic.specialist.service.SpecialistService;
import com.bridge.medic.user.dto.LocationDTO;
import com.bridge.medic.user.dto.request.ChangePasswordRequest;
import com.bridge.medic.user.dto.response.GetDoctorsFromSearchResponse;
import com.bridge.medic.user.dto.response.GetUserSettingsPageResponse;
import com.bridge.medic.user.mapper.UserMapper;
import com.bridge.medic.user.model.User;
import com.bridge.medic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticatedUserService authService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final SpecialistService specialistService;

    @PatchMapping
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        userService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check")
    public boolean doesUserAlreadyExist(String emailOrLogin) {
        return userService.finUserByEmailOrLogin(emailOrLogin).isPresent();
    }

    @GetMapping("/specialist-search")
    public ResponseEntity<GetDoctorsFromSearchResponse> findSpecialists(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String doctorType,
            @RequestParam(required = false) String language
    ) {
        List<User> specialists = specialistService.findSpecialists(city, language, doctorType);
        GetDoctorsFromSearchResponse response = new GetDoctorsFromSearchResponse(userMapper.usersToSpecialistDtos(specialists));
        for (SpecialistDto specialistDto : response.getSpecialists()) {
            specialistDto.setDoctorType(doctorType);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-by-login")
    public boolean doesUserAlreadyExistByLogin(String login) {
        return userService.finUserByLogin(login).isPresent();
    }

    @GetMapping("/check-by-email")
    public boolean doesUserAlreadyExistByEmail(String email) {
        return userService.finUserByEmail(email).isPresent();
    }

    @GetMapping("/settings-page")
    public ResponseEntity<GetUserSettingsPageResponse> getSettingsPage() {
        User currentUser = authService.getCurrentUser();
        LocationDTO locationDTO = new LocationDTO(currentUser.getCity().getName(),
                currentUser.getCity().getRegion().getName(),
                currentUser.getCity().getRegion().getCountry().getName()
        );

        return ResponseEntity.ok(new GetUserSettingsPageResponse(locationDTO, userMapper.userToUserDto(currentUser)));
    }
}
