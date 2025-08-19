package com.deploymanager.service;

import com.deploymanager.dto.PassRequestDetailDto;
import com.deploymanager.dto.PassRequestUpsertDto;
import com.deploymanager.dto.PassRequestDto;
import com.deploymanager.mapper.PassRequestDetailMapper;
import com.deploymanager.mapper.PassRequestMapper;
import com.deploymanager.model.PassRequestDetail;
import com.deploymanager.model.PassRequestHeader;
import com.deploymanager.repository.PassRequestHeaderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PassRequestService {

    @Autowired
    private final PassRequestHeaderRepository headerRepo;
    @Autowired
    private final PassRequestMapper mapper;
    @Autowired
    private final PassRequestDetailMapper detailMapper;


    public PassRequestService(PassRequestHeaderRepository headerRepo, PassRequestMapper mapper, PassRequestDetailMapper detailMapper) {
        this.headerRepo = headerRepo;
        this.mapper = mapper;
        this.detailMapper = detailMapper;
    }

    public PassRequestDto create(PassRequestUpsertDto dto) {
        PassRequestHeader entity = mapper.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        //entity.setUpdatedAt(LocalDateTime.now());
        if (dto.getDetails() != null) {
            List<PassRequestDetail> details = detailMapper.toEntityList(dto.getDetails());
            details.forEach(d -> d.setPassRequestHeader(entity));
            entity.setDetails(details);
        }

        return mapper.toDto(headerRepo.save(entity));
    }

    public PassRequestDto update(PassRequestUpsertDto dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("ID del pase es requerido para actualizar.");
        }

        PassRequestHeader existing = headerRepo.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el pase con ID: " + dto.getId()));

        mapper.updateHeaderFromDto(dto, existing);
        existing.setUpdatedAt(LocalDateTime.now());

        // Manejo de detalles si vienen
        if (dto.getDetails() != null) {
            // Reemplazar: eliminar los que ya no vienen
            existing.getDetails().removeIf(old ->
                    dto.getDetails().stream().noneMatch(n -> n.getId() != null && n.getId().equals(old.getId()))
            );

            // Agregar o actualizar
            for (PassRequestDetailDto detailDto : dto.getDetails()) {
                if (detailDto.getId() == null) {
                    // Nuevo
                    PassRequestDetail newDetail = detailMapper.toEntity(detailDto);
                    newDetail.setPassRequestHeader(existing);
                    existing.getDetails().add(newDetail);
                } else {
                    // Editar existente
                    PassRequestDetail existingDetail = existing.getDetails().stream()
                            .filter(d -> d.getId().equals(detailDto.getId()))
                            .findFirst()
                            .orElseThrow(() -> new EntityNotFoundException("Detalle no encontrado: ID " + detailDto.getId()));

                    existingDetail.setComponentName(detailDto.getComponentName());
                    existingDetail.setComponentType(detailDto.getComponentType());
                    existingDetail.setFileAffected(detailDto.getFileAffected());
                    existingDetail.setNotes(detailDto.getNotes());
                    existingDetail.setRequiresBackup(detailDto.isRequiresBackup());
                    existingDetail.setHasUnitTests(detailDto.isHasUnitTests());
                }
            }
        }

        return mapper.toDto(headerRepo.save(existing));
    }

    public List<PassRequestDto> getAll() {
        return mapper.toDtoList(headerRepo.findAll());
    }

    public PassRequestDto getById(Long id) {
        PassRequestHeader entity = headerRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PassRequestHeader not found with ID: " + id));
        return mapper.toDto(entity);
    }

    public PassRequestDto reject(Long id) {
        PassRequestHeader entity = headerRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pase no encontrado"));

        String currentStatus = entity.getStatus();

        if (List.of("DEPLOYED", "REJECTED", "CANCELLED").contains(currentStatus)) {
            throw new IllegalStateException("No se puede rechazar un pase con estado: " + currentStatus);
        }

        entity.setStatus("REJECTED");
        entity.setUpdatedAt(LocalDateTime.now());
        return mapper.toDto(headerRepo.save(entity));
    }

    public PassRequestDto cancel(Long id) {
        PassRequestHeader entity = headerRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pase no encontrado"));

        String currentStatus = entity.getStatus();

        if (List.of("DEPLOYED", "REJECTED", "CANCELLED").contains(currentStatus)) {
            throw new IllegalStateException("No se puede cancelar un pase con estado: " + currentStatus);
        }

        entity.setStatus("CANCELLED");
        entity.setUpdatedAt(LocalDateTime.now());
        return mapper.toDto(headerRepo.save(entity));
    }

    public PassRequestDto approveByQA(Long id) {
        PassRequestHeader entity = headerRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pase no encontrado"));

        if (!"PENDING".equals(entity.getStatus())) {
            throw new IllegalStateException("Solo se puede aprobar por QA desde estado PENDING");
        }

        entity.setStatus("QA_APPROVED");
        entity.setUpdatedAt(LocalDateTime.now());
        return mapper.toDto(headerRepo.save(entity));
    }

    public PassRequestDto approveByLead(Long id) {
        PassRequestHeader entity = headerRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pase no encontrado"));

        if (!"QA_APPROVED".equals(entity.getStatus())) {
            throw new IllegalStateException("Solo se puede aprobar por el líder desde QA_APPROVED");
        }

        entity.setStatus("LEAD_APPROVED");
        entity.setUpdatedAt(LocalDateTime.now());
        return mapper.toDto(headerRepo.save(entity));
    }

    public PassRequestDto deploy(Long id) {
        PassRequestHeader entity = headerRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pase no encontrado"));

        if (!"LEAD_APPROVED".equals(entity.getStatus())) {
            throw new IllegalStateException("Solo se puede desplegar desde LEAD_APPROVED");
        }

        entity.setStatus("DEPLOYED");
        entity.setUpdatedAt(LocalDateTime.now());
        return mapper.toDto(headerRepo.save(entity));
    }

    public void delete(Long id) {
        PassRequestHeader entity = headerRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pase no encontrado"));
        headerRepo.delete(entity); // Gracias a orphanRemoval, elimina detalles también
    }

    public PassRequestDto cancel_(PassRequestUpsertDto dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("ID requerido para anular el pase.");
        }

        PassRequestHeader entity = headerRepo.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Pase no encontrado"));

        entity.setStatus("CANCELLED");
        entity.setUpdatedAt(LocalDateTime.now());

        return mapper.toDto(headerRepo.save(entity));
    }
}

    /*public PassRequestDto update(Long id, PassRequestCreateDto dto) {
        PassRequestHeader existing = headerRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found"));

        // Actualiza cabecera sin tocar detalles si no se envían
        mapper.updateHeaderFromDto(dto, existing);
        existing.setUpdatedAt(LocalDateTime.now());

        // Si vienen detalles, los reemplaza
        if (dto.getDetails() != null) {
            existing.getDetails().clear(); // limpia los actuales
            List<PassRequestDetail> newDetails = detailMapper.toEntityList(dto.getDetails());
            newDetails.forEach(d -> d.setPassRequestHeader(existing));
            existing.getDetails().addAll(newDetails);
        }

        return mapper.toDto(headerRepo.save(existing));
    }*/

    /*public PassRequestDto create(PassRequestUpsertDto dto) {
        PassRequestHeader entity = mapper.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        // Solo si hay detalles
        if (entity.getDetails() != null) {
            entity.getDetails().forEach(d -> d.setPassRequestHeader(entity));
        }

        return mapper.toDto(headerRepo.save(entity));
    }*/
