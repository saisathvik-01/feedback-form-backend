package com.feedback.controller;

import com.feedback.dto.AuthResponse;
import com.feedback.dto.LoginRequest;
import com.feedback.dto.SignupRequest;
import com.feedback.model.User;
import com.feedback.service.AuthService;
import com.feedback.security.JwtUtils;
import com.feedback.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    // Validation regexes
    private static final Pattern STUDENT_USERNAME_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern STUDENT_EMAIL_PATTERN = Pattern.compile("^\\d{10}@kluniversity\\.in$");
    private static final Pattern FACULTY_ADMIN_USERNAME_PATTERN = Pattern.compile("^.{3,}$");
    private static final Pattern GENERAL_EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$");

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Validate input format - allow both student format and general formats
            String identifier = loginRequest.getIdentifier();
            boolean isValidFormat = STUDENT_USERNAME_PATTERN.matcher(identifier).matches() ||
                                   STUDENT_EMAIL_PATTERN.matcher(identifier).matches() ||
                                   FACULTY_ADMIN_USERNAME_PATTERN.matcher(identifier).matches() ||
                                   GENERAL_EMAIL_PATTERN.matcher(identifier).matches();

            if (!isValidFormat) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Enter valid Username or Email"));
            }

            // Find user by username or email
            User user = authService.findByUsernameOrEmail(identifier)
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

            // Compare raw password with encoded password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }

            UserPrincipal userDetails = UserPrincipal.create(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken(authentication);

            return ResponseEntity.ok(new AuthResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getRole(),
                    userDetails.getFacultyName(),
                    userDetails.getSection()));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Invalid credentials"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        Map<String, String> errors = new HashMap<>();

        // Role-based validation for username
        if (signUpRequest.getRole() != null) {
            if ("STUDENT".equals(signUpRequest.getRole())) {
                if (signUpRequest.getUsername() != null && !STUDENT_USERNAME_PATTERN.matcher(signUpRequest.getUsername()).matches()) {
                    errors.put("username", "Student ID must be exactly 10 digits");
                }
                if (!STUDENT_EMAIL_PATTERN.matcher(signUpRequest.getEmail()).matches()) {
                    errors.put("email", "Email must be in format: 1234567890@kluniversity.in");
                }
            } else {
                // FACULTY or ADMIN
                if (signUpRequest.getUsername() != null && !FACULTY_ADMIN_USERNAME_PATTERN.matcher(signUpRequest.getUsername()).matches()) {
                    errors.put("username", "Username must be at least 3 characters");
                }
                if (!GENERAL_EMAIL_PATTERN.matcher(signUpRequest.getEmail()).matches()) {
                    errors.put("email", "Please enter a valid email address");
                }
            }
        }

        // Validate password
        if (!PASSWORD_PATTERN.matcher(signUpRequest.getPassword()).matches()) {
            errors.put("password", "Password must be 8+ characters with uppercase, number, and special character");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        // Check if username or email already exists
        if (signUpRequest.getUsername() != null && authService.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Username is already taken!"));
        }

        if (authService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Email is already in use!"));
        }

        // Create new user account
        User user = new User(signUpRequest.getUsername(),
                           signUpRequest.getEmail(),
                           signUpRequest.getPassword(),
                           signUpRequest.getRole());
        user.setFacultyName(signUpRequest.getFacultyName());
        user.setSection(signUpRequest.getSection());

        authService.save(user);

        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }
}