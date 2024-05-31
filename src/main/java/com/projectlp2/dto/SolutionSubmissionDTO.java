package com.projectlp2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class SolutionSubmissionDTO {
    @NotBlank(message = "Author is mandatory")
    @JsonProperty("author")
    private String author;

    @NotBlank(message = "Filename is mandatory")
    @JsonProperty("filename")
    private String filename;

    @NotBlank(message = "Problem code is mandatory")
    @JsonProperty("problemCode")
    private String problemCode;

    @NotBlank(message = "Source code is mandatory")
    @JsonProperty("sourceCode")
    private String sourceCode;
}
