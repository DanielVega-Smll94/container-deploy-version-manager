package com.deploymanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DockerImageDto {
    @JsonProperty("ID")
    private String id;

    @JsonProperty("Repository")
    private String repository;

    @JsonProperty("Tag")
    private String tag;

    @JsonProperty("CreatedAt")
    private String createdAt;

    @JsonProperty("Size")
    private String size;
}
