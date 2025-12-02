package com.bvkm.pms.controller;

import com.bvkm.pms.entity.Activity;
import com.bvkm.pms.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    @Autowired
    ActivityRepository activityRepository;

    @GetMapping
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    @Autowired
    com.bvkm.pms.repository.StaffRepository staffRepository;

    @PostMapping
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public Activity createActivity(@RequestBody Activity activity) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        com.bvkm.pms.entity.Staff staff = staffRepository.findByLoginId(username)
                .orElseThrow(() -> new RuntimeException("Error: Staff not found."));

        activity.setStaff(staff);
        activity.setTimestamp(LocalDateTime.now());
        return activityRepository.save(activity);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Activity> getActivityById(@PathVariable UUID id) {
        return activityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
