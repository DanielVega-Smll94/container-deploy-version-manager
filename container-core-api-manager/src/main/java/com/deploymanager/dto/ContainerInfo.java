package com.deploymanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Representa un contenedor Docker según salida JSON de `docker ps --format '{{json .}}'`
 */
@Data
public class ContainerInfo {

    @JsonProperty("ID")
    private String id;

    @JsonProperty("Image")
    private String image;

    @JsonProperty("Names")
    private String names;

    @JsonProperty("Command")
    private String command;

    @JsonProperty("CreatedAt")
    private String createdAt;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("Ports")
    private String ports;

}
