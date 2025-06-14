package com.bridge.medic.user.controller;

import com.bridge.medic.config.security.service.AuthenticatedUserService;
import com.bridge.medic.specialist.dto.SpecialistDto;
import com.bridge.medic.specialist.service.SpecialistService;
import com.bridge.medic.user.dto.LocationDTO;
import com.bridge.medic.user.dto.UserDto;
import com.bridge.medic.user.dto.request.ChangePasswordRequest;
import com.bridge.medic.user.dto.request.UpdateUserInfoRequest;
import com.bridge.medic.user.dto.response.GetSpecialistInfoPageResponse;
import com.bridge.medic.user.dto.response.GetDoctorsFromSearchResponse;
import com.bridge.medic.user.dto.response.GetUserSettingsPageResponse;
import com.bridge.medic.user.mapper.UserMapper;
import com.bridge.medic.user.model.User;
import com.bridge.medic.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticatedUserService authService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final SpecialistService specialistService;
    private final AuthenticatedUserService authenticatedUserService;

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<UserDto> getCurrentUserInfo() {
        return ResponseEntity.ok(userMapper.userToUserDto(authenticatedUserService.getCurrentUser()));
    }

    @PutMapping("/change-password")
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

    @GetMapping("/specialist-info-page")
    public ResponseEntity<GetSpecialistInfoPageResponse> getSpecialistInfoPage(
            @RequestParam(required = false) int specialistId
    ) {
        Optional<User> optionalSpecialist = userService.finUserById(specialistId);
        if (optionalSpecialist.isEmpty()){
            ResponseEntity.badRequest().build();
        }

        User specialist = optionalSpecialist.orElseThrow();
        if (!specialistService.isSpecialist(specialist.getId())){
            ResponseEntity.badRequest().build();
        }

        SpecialistDto specialistDto = userMapper.userToSpecialistDto(specialist);

        GetSpecialistInfoPageResponse response = new GetSpecialistInfoPageResponse();

        List<String> positions = specialistService.findAllApprovedPositionsBySpecialistId(specialist.getId());

        response.setSpecialist(specialistDto);
        response.setApprovedPositions(positions);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-by-login")
    public ResponseEntity<?> doesUserAlreadyExistByLogin(@RequestParam String login) {
        boolean exists = userService.finUserByLogin(login).isPresent();
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check-by-email")
    public ResponseEntity<?> doesUserAlreadyExistByEmail(String email) {
        boolean exists = userService.finUserByEmail(email).isPresent();
        return ResponseEntity.ok(exists);
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

    @PutMapping("/update-profile")
    public ResponseEntity<UserDto> updateProfile(@Valid @RequestBody UpdateUserInfoRequest request) {
        userService.updateUserInfo(request);
        return ResponseEntity.ok().build();
    }

}
