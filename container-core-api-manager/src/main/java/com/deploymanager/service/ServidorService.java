package com.deploymanager.service;

import com.deploymanager.dto.ServidorDto;
import com.deploymanager.mapper.ServidorMapper;
import com.deploymanager.model.Servidor;
import com.deploymanager.repository.ServidorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ServidorService {

    private final ServidorRepository serverRepository;
    private final ServidorMapper serverMapper;
    private final TextEncryptor textEncryptor;  // inyecta tu cifrador

    public ServidorService(ServidorRepository serverRepository, ServidorMapper serverMapper, TextEncryptor textEncryptor) {
        this.serverRepository = serverRepository;
        this.serverMapper = serverMapper;
        this.textEncryptor = textEncryptor;
    }

    /**
     * Lista todos los ServidorDto.
     * - 204 No Content si no hay ninguno.
     * - 200 OK con la lista si hay al menos uno.
     */

    public ResponseEntity<?> findAllServer(boolean encrypt) {
        List<Servidor> list = serverRepository.findAllByEstadoTrueOrderByIdDesc();
        List<ServidorDto> dtos = serverMapper.toDtoList(list);

        if (dtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        if(encrypt) {
            dtos.forEach(d -> d.setPassword(textEncryptor.encrypt(d.getPassword())));
        }
        return ResponseEntity.ok(dtos);
    }

    /**
     * Devuelve un ServidorDto por id o 404 con mensaje si no existe.
     */
    public ResponseEntity<?> findByIdServer(Long id, boolean encrypt) {
        Optional<Servidor> opt = serverRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Servidor no encontrado: " + id);
        }
        ServidorDto dto = serverMapper.toDto(opt.get());
        if(encrypt) {
            String encrypted = textEncryptor.encrypt(dto.getPassword());
            dto.setPassword(encrypted);
        }

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<?> create(ServidorDto serverDto) {
        if (serverRepository.findByHostAndEstadoTrue(serverDto.getHost()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("Host ya se encuentra registrado: " + serverDto.getHost());
        }
        serverDto.setEstado(true);
        Servidor server = serverMapper.toEntity(serverDto);

        server.setFechaCreacion(Instant.now());
        server.setUsuarioCreacion("admin");
        Servidor saved  = serverRepository.save(server);
        ServidorDto result = serverMapper.toDto(saved);
        String encrypted = textEncryptor.encrypt(saved.getPassword());
        result.setPassword(encrypted);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    public ResponseEntity<?> update(ServidorDto dto) {
        Optional<Servidor> opt = serverRepository.findById(dto.getId());
        if (opt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Id de Servidor no encontrado: " + dto.getId());
        }

        // Mapeo shallow: sólo campos no nulos del dto
        Servidor existing = opt.get();
        serverMapper.updateServidorFromDto(dto, existing);
        existing.setFechaModifica(Instant.now());
        existing.setUsuarioModifica("admin2");
        // Guarda y devuelve el DTO actualizado
        Servidor updated  = serverRepository.save(existing);
        ServidorDto result = serverMapper.toDto(updated);
        String encrypted = textEncryptor.encrypt(result.getPassword());
        result.setPassword(encrypted);
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<?> deleteServer(Long id) {
        Optional<Servidor> opt = serverRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Servidor no encontrado: " + id);
        }

        Servidor srv = opt.get();
        srv.setEstado(false);                      // desactiva
        srv.setFechaModifica(Instant.now());
        Servidor saved = serverRepository.save(srv);// persiste cambio

        ServidorDto dto = serverMapper.toDto(saved);
        return ResponseEntity.ok(dto);
    }
}
