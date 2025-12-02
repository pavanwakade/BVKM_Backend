package com.bvkm.pms.controller;

import com.bvkm.pms.entity.Staff;
import com.bvkm.pms.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    StaffRepository staffRepository;

    @Autowired
    PasswordEncoder encoder;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Staff> getStaffById(@PathVariable UUID id) {
        return staffRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Staff createStaff(@RequestBody Staff staff) {
        // Hash password if provided
        if (staff.getPasswordHash() != null && !staff.getPasswordHash().isEmpty()) {
            staff.setPasswordHash(encoder.encode(staff.getPasswordHash()));
        }
        return staffRepository.save(staff);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Staff> updateStaff(@PathVariable UUID id, @RequestBody Staff staffDetails) {
        return staffRepository.findById(id)
                .map(staff -> {
                    staff.setName(staffDetails.getName());
                    staff.setDesignation(staffDetails.getDesignation());
                    staff.setLevel(staffDetails.getLevel());
                    staff.setReportingTo(staffDetails.getReportingTo());
                    staff.setAssignedLocation(staffDetails.getAssignedLocation());
                    staff.setLoginId(staffDetails.getLoginId());
                    staff.setRole(staffDetails.getRole());
                    // Only update password if provided
                    if (staffDetails.getPasswordHash() != null && !staffDetails.getPasswordHash().isEmpty()) {
                        staff.setPasswordHash(encoder.encode(staffDetails.getPasswordHash()));
                    }
                    return ResponseEntity.ok(staffRepository.save(staff));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteStaff(@PathVariable UUID id) {
        return staffRepository.findById(id)
                .map(staff -> {
                    staffRepository.delete(staff);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
