package com.bridge.medic.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetDoctorsFromSearchRequest {
    private String country;
    private String city;
    private String region;
    private String doctorType;
    private String language;
}
