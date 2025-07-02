package com.deploymanager.repository;

import com.deploymanager.model.Servidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServidorRepository extends JpaRepository<Servidor, Long>
{
    // Devuelve solo los activos, ya ordenados por id desc
    List<Servidor> findAllByEstadoTrueOrderByIdDesc();

    List<Servidor> findAllByEstadoTrue();
    Optional<Servidor> findByNombre(String nombre);

    Optional<Servidor> findByHostAndEstadoTrue(String host);
    Optional<Servidor> findByIdAndEstadoTrue(Long id);
}
