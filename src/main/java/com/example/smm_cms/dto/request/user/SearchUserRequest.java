package com.example.smm_cms.dto.request.user;

import com.example.smm_cms.dto.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchUserRequest  extends PageRequest {

    private String username;

    private String email;

    private Boolean active;

    private String role;

}
