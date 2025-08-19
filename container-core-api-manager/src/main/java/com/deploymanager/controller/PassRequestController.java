package com.deploymanager.controller;
import com.deploymanager.dto.PassRequestUpsertDto;
import com.deploymanager.dto.PassRequestDto;
import com.deploymanager.service.PassRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/passes")
public class PassRequestController {

    @Autowired
    private PassRequestService passRequestService;

    /*@PostMapping
    public ResponseEntity<PassRequestDto> create(@RequestBody PassRequestUpsertDto request) {
        return ResponseEntity.ok(passRequestService.create(request));
    }*/

    @PostMapping
    public ResponseEntity<PassRequestDto> create(@RequestBody PassRequestUpsertDto dto) {
        return ResponseEntity.ok(passRequestService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassRequestDto> update(@RequestBody PassRequestUpsertDto dto) {
        return ResponseEntity.ok(passRequestService.update(dto));
    }

    @GetMapping
    public ResponseEntity<List<PassRequestDto>> getAll() {
        return ResponseEntity.ok(passRequestService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassRequestDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(passRequestService.getById(id));
    }

    //Cancelar pase
    @PutMapping("/cancel/{id}")
    public ResponseEntity<PassRequestDto> cancel(@PathVariable Long id) {/*@RequestBody PassRequestUpsertDto dto*/
        return ResponseEntity.ok(passRequestService.cancel(id));
    }

    // Rechazar pase
    @PutMapping("/reject/{id}")
    public ResponseEntity<PassRequestDto> reject(@PathVariable Long id) {
        return ResponseEntity.ok(passRequestService.reject(id));
    }

    // Aprobación por QA
    @PutMapping("/approve-by-qa/{id}")
    public ResponseEntity<PassRequestDto> approveByQA(@PathVariable Long id) {
        return ResponseEntity.ok(passRequestService.approveByQA(id));
    }

    // Aprobación por líder
    @PutMapping("/approve-by-lead/{id}")
    public ResponseEntity<PassRequestDto> approveByLead(@PathVariable Long id) {
        return ResponseEntity.ok(passRequestService.approveByLead(id));
    }

    // Desplegar pase
    @PutMapping("/deploy/{id}")
    public ResponseEntity<PassRequestDto> deploy(@PathVariable Long id) {
        return ResponseEntity.ok(passRequestService.deploy(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        passRequestService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
