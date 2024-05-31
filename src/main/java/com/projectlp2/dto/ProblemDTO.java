package com.projectlp2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Data
public class ProblemDTO {
    @NotBlank(message = "Filename is mandatory")
    @JsonProperty("filename")
    private String filename;

    @NotBlank(message = "Problem code is mandatory")
    @JsonProperty("problemCode")
    private String problemCode;

    @NotBlank(message = "LPS is mandatory")
    @JsonProperty("lps")
    private String lps;
}
