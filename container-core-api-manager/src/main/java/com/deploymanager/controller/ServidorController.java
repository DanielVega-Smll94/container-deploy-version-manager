package com.deploymanager.controller;


import com.deploymanager.dto.SSHRequestDto;
import com.deploymanager.dto.ServidorDto;
import com.deploymanager.service.SSHService;
import com.deploymanager.service.ServidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/servidores")
public class ServidorController {

    @Autowired
    private ServidorService servidorController;

    @GetMapping("findAllServer/")
    public ResponseEntity<?> findAllServer()
    {
        return servidorController.findAllServer(false);
    }

    @GetMapping("findAllServerEncrypted/")
    public ResponseEntity<?> findAllServerEncrypted()
    {
        return servidorController.findAllServer(true);
    }

    @GetMapping("findByIdServer/")
    public ResponseEntity<?> findByIdServer(@RequestParam Long id)
    {
        return servidorController.findByIdServer(id, false);
    }

    @GetMapping("findByIdServerEncrypted/")
    public ResponseEntity<?> findByIdServerEncrypted(@RequestParam Long id)
    {
        return servidorController.findByIdServer(id, true);
    }


    @PostMapping("saveServer/")
    public ResponseEntity<?> create(@RequestBody ServidorDto dto) {
        return servidorController.create(dto);
    }

    @PutMapping("updateServer/")
    public ResponseEntity<?> updateUser(@RequestBody ServidorDto dto)
    {
        return servidorController.update(dto);
    }

    @DeleteMapping("deleteServer/")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        return servidorController.deleteServer(id);
    }
}
