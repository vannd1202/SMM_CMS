package com.example.smm_cms.dto.request.panel_service;

import com.example.smm_cms.dto.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchPanelServiceRequest  extends PageRequest {
    private String name;

    private Boolean active;

}
