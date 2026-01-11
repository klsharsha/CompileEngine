package com.compiler.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubmissionRequest {

    @NotBlank
    private String language;

    @NotBlank
    private String code;

    private String input;
}
