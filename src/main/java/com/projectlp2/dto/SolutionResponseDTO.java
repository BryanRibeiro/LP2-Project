package com.projectlp2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SolutionResponseDTO {
    @JsonProperty("author")
    private String author;

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("problemCode")
    private String problemCode;

    @JsonProperty("status")
    private String status;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
}
