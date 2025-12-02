package com.bvkm.pms.controller;

import com.bvkm.pms.entity.Staff;
import com.bvkm.pms.payload.request.LoginRequest;
import com.bvkm.pms.payload.response.JwtResponse;
import com.bvkm.pms.repository.StaffRepository;
import com.bvkm.pms.security.jwt.JwtUtils;
import com.bvkm.pms.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    StaffRepository staffRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                roles));
    }

    // Endpoint to create initial admin user (remove in production or secure it)
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody LoginRequest signUpRequest) {
        if (staffRepository.findByLoginId(signUpRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        Staff staff = new Staff();
        staff.setLoginId(signUpRequest.getUsername());
        staff.setPasswordHash(encoder.encode(signUpRequest.getPassword()));
        staff.setName("Admin User");
        staff.setRole(Staff.Role.ADMIN);

        staffRepository.save(staff);

        return ResponseEntity.ok("Admin registered successfully!");
    }
}
