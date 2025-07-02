package com.deploymanager.model;

import com.deploymanager.util.AttributeEncryptor;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "servidores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servidor {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Nullable
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String host;

    private int port = 22;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    @Convert(converter = AttributeEncryptor.class)
    private String password;

    @Column(nullable = false)
    private String descripcion;

    private Boolean estado;

    @Column(name = "fecha_creacion")/*, insertable = false, updatable = false)*/
    private Instant fechaCreacion;


    @Column(name = "usuario_creacion")
    private String usuarioCreacion;


    @Column(name = "fecha_modifica")
    private Instant fechaModifica;


    @Column(name = "usuario_modifica")
    private String usuarioModifica;
}
