package com.deploymanager.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServidorDto {
    private Long id;
    private String nombre;
    private String host;
    private int port;
    private String username;
    private String password;
    private String descripcion;
    private boolean estado;

}