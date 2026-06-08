package com.example.smm_cms.dto.response.login;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;

    private String tokenType;

    private String username;

    private String role;
}
