package com.projectlp2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Data
public class TestCaseDTO {
    @NotBlank(message = "Problem code is mandatory")
    @JsonProperty("problemCode")
    private String problemCode;

    @NotBlank(message = "Input file is mandatory")
    @JsonProperty("inputFile")
    private String inputFile;

    @NotBlank(message = "Expected output file is mandatory")
    @JsonProperty("expectedOutputFile")
    private String expectedOutputFile;
}