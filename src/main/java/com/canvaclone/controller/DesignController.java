package com.canvaclone.controller;

import com.canvaclone.dto.DesignDto;
import com.canvaclone.service.DesignService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/designs")
public class DesignController {

    @Autowired
    private DesignService designService;

    @GetMapping("/public")
    public ResponseEntity<List<DesignDto.DesignResponse>> getPublicDesigns() {
        return ResponseEntity.ok(designService.getPublicDesigns());
    }

    @PostMapping
    public ResponseEntity<DesignDto.DesignResponse> createDesign(
            Authentication authentication,
            @Valid @RequestBody DesignDto.CreateRequest request) {
        return new ResponseEntity<>(designService.createDesign(authentication.getName(), request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DesignDto.DesignResponse>> getAllDesigns(Authentication authentication) {
        return ResponseEntity.ok(designService.getAllDesigns(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DesignDto.DesignResponse> getDesignById(
            Authentication authentication,
            @PathVariable Long id) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(designService.getDesignById(email, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DesignDto.DesignResponse> updateDesign(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody DesignDto.UpdateRequest request) {
        return ResponseEntity.ok(designService.updateDesign(authentication.getName(), id, request));
    }

    @PutMapping("/{id}/access-level")
    public ResponseEntity<DesignDto.DesignResponse> updateAccessLevel(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam String accessLevel) {
        DesignDto.UpdateRequest request = DesignDto.UpdateRequest.builder()
                .accessLevel(accessLevel)
                .build();
        return ResponseEntity.ok(designService.updateDesign(authentication.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDesign(
            Authentication authentication,
            @PathVariable Long id) {
        designService.deleteDesign(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
