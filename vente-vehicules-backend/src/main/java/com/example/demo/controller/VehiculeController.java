package com.example.demo.controller;

import com.example.demo.dto.VehiculeRequest;
import com.example.demo.dto.VehiculeResponse;
import com.example.demo.service.VehiculeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicules")
@RequiredArgsConstructor
public class VehiculeController {

    private final VehiculeService vehiculeService;

    @GetMapping
    public ResponseEntity<List<VehiculeResponse>> getAllVehicules() {
        return ResponseEntity.ok(vehiculeService.getAllVehicules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculeResponse> getVehiculeById(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculeService.getVehiculeById(id));
    }

    @PostMapping
    public ResponseEntity<VehiculeResponse> createVehicule(@RequestBody VehiculeRequest request) {
        VehiculeResponse created = vehiculeService.createVehicule(request);
        return ResponseEntity.status(201).body(created);
    }
}