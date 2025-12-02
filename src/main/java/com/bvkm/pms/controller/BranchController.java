package com.bvkm.pms.controller;

import com.bvkm.pms.entity.Branch;
import com.bvkm.pms.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/branches")
public class BranchController {

    @Autowired
    BranchRepository branchRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Branch> getBranchById(@PathVariable UUID id) {
        return branchRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Branch createBranch(@RequestBody Branch branch) {
        return branchRepository.save(branch);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Branch> updateBranch(@PathVariable UUID id, @RequestBody Branch branchDetails) {
        return branchRepository.findById(id)
                .map(branch -> {
                    branch.setName(branchDetails.getName());
                    branch.setAddress(branchDetails.getAddress());
                    branch.setFirm(branchDetails.getFirm());
                    branch.setContactDetails(branchDetails.getContactDetails());
                    return ResponseEntity.ok(branchRepository.save(branch));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBranch(@PathVariable UUID id) {
        return branchRepository.findById(id)
                .map(branch -> {
                    branchRepository.delete(branch);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
