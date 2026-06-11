package com.example.smm_cms.dto.response.customer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class UserResponse {
    private Long id;

    private String username;

    private String email;

    private BigDecimal balance;

    private Boolean active;

    private Enum role;

    private LocalDateTime createdDate;
}