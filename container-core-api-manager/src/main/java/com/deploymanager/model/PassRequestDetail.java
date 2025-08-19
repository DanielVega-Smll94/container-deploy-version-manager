package com.deploymanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pass_request_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassRequestDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String componentType;   // FRONTEND, BACKEND, APP, DATABASE
    private String componentName;   // Nombre del módulo o microservicio
    private String fileAffected;    // Archivo o recurso afectado
    private String notes;           // Notas técnicas del cambio

    private boolean requiresBackup; // ¿Requiere backup?
    private boolean hasUnitTests;   // ¿Tiene pruebas unitarias?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pass_request_header_id")
    private PassRequestHeader passRequestHeader;
}
