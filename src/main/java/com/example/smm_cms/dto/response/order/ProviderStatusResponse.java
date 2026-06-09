package com.example.smm_cms.dto.response.order;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderStatusResponse {
    private String charge;

    @JsonProperty("start_count")
    private Integer startCount;

    private String status;

    private Integer remains;
}
