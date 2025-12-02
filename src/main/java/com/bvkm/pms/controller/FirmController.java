package com.bvkm.pms.controller;

import com.bvkm.pms.entity.Firm;
import com.bvkm.pms.repository.FirmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/firms")
public class FirmController {

    @Autowired
    FirmRepository firmRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Firm> getAllFirms() {
        return firmRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Firm> getFirmById(@PathVariable UUID id) {
        return firmRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Firm createFirm(@RequestBody Firm firm) {
        return firmRepository.save(firm);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Firm> updateFirm(@PathVariable UUID id, @RequestBody Firm firmDetails) {
        return firmRepository.findById(id)
                .map(firm -> {
                    firm.setName(firmDetails.getName());
                    firm.setRegistrationDetails(firmDetails.getRegistrationDetails());
                    firm.setBillingFormat(firmDetails.getBillingFormat());
                    firm.setContactDetails(firmDetails.getContactDetails());
                    return ResponseEntity.ok(firmRepository.save(firm));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteFirm(@PathVariable UUID id) {
        return firmRepository.findById(id)
                .map(firm -> {
                    firmRepository.delete(firm);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
