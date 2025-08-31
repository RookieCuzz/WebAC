package com.cuzz.webac.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SmsSendRequestDTO {
    @NotBlank
    @Pattern(regexp = "^\\+?\\d{6,20}$", message = "mobile 格式不正确")
    private String mobile;

    private String code;




}
