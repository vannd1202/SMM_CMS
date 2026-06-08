package com.example.smm_cms.dto.request.user;

import com.example.smm_cms.dto.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class CustomerRequest extends PageRequest {
    @Schema(description = "Tên khách hàng")
    String name;

}
