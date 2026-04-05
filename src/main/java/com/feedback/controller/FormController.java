package com.feedback.controller;

import com.feedback.dto.FormDTO;
import com.feedback.dto.FormResponseDTO;
import com.feedback.model.User;
import com.feedback.security.UserPrincipal;
import com.feedback.service.FormService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/forms")
public class FormController {

    @Autowired
    private FormService formService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createForm(@Valid @RequestBody FormDTO formDTO,
                                        Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = new User();
            user.setId(userPrincipal.getId());
            user.setUsername(userPrincipal.getUsername());
            user.setRole(userPrincipal.getRole());

            FormResponseDTO createdForm = formService.createForm(formDTO, user);
            return ResponseEntity.ok(Map.of(
                "message", "Form created successfully",
                "form", createdForm
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error creating form: " + e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    public ResponseEntity<List<FormResponseDTO>> getAllForms(Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            List<FormResponseDTO> forms;
            if (userPrincipal.getRole().equals(User.Role.ADMIN)) {
                forms = formService.getAllActiveForms();
            } else {
                // Faculty can only see forms they created
                User user = new User();
                user.setId(userPrincipal.getId());
                user.setUsername(userPrincipal.getUsername());
                user.setRole(userPrincipal.getRole());
                forms = formService.getFormsByUser(user);
            }

            return ResponseEntity.ok(forms);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    public ResponseEntity<?> getFormById(@PathVariable Long id, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            FormResponseDTO form = formService.getFormById(id);

            // Check if faculty user owns this form
            if (userPrincipal.getRole().equals(User.Role.FACULTY) &&
                !form.getCreatedBy().equals(userPrincipal.getUsername())) {
                return ResponseEntity.status(403)
                        .body(Map.of("message", "You don't have permission to view this form"));
            }

            return ResponseEntity.ok(form);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error retrieving form: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    public ResponseEntity<?> updateForm(@PathVariable Long id,
                                        @Valid @RequestBody FormDTO formDTO,
                                        Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = new User();
            user.setId(userPrincipal.getId());
            user.setUsername(userPrincipal.getUsername());
            user.setRole(userPrincipal.getRole());

            FormResponseDTO updatedForm = formService.updateForm(id, formDTO, user);
            return ResponseEntity.ok(Map.of(
                "message", "Form updated successfully",
                "form", updatedForm
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error updating form: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    public ResponseEntity<?> deleteForm(@PathVariable Long id, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = new User();
            user.setId(userPrincipal.getId());
            user.setUsername(userPrincipal.getUsername());
            user.setRole(userPrincipal.getRole());

            formService.deleteForm(id, user);
            return ResponseEntity.ok(Map.of("message", "Form deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error deleting form: " + e.getMessage()));
        }
    }
}