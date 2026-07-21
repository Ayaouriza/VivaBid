package com.example.demo.controller;

import com.example.demo.dto.VehiculeSimpleResponse;
import com.example.demo.dto.VenteRequest;
import com.example.demo.dto.VenteResponse;
import com.example.demo.service.VenteService;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ventes")
@RequiredArgsConstructor
public class VenteController {

    private final VenteService venteService;

    @GetMapping
    public ResponseEntity<List<VenteResponse>> getAllVentes() {
        return ResponseEntity.ok(venteService.getAllVentes());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<VenteResponse> getVenteById(@PathVariable Long id) {
        return ResponseEntity.ok(venteService.getVenteById(id));
    }

    @PostMapping
    public ResponseEntity<VenteResponse> createVente(@RequestBody VenteRequest request) {
        VenteResponse created = venteService.createVente(request);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/vehicules-disponibles")
public ResponseEntity<List<VehiculeSimpleResponse>> getVehiculesDisponibles(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
        @RequestParam(required = false) Boolean chargeAujourdhui) {
    return ResponseEntity.ok(venteService.getVehiculesDisponibles(dateDebut, dateFin, chargeAujourdhui));
}

    @PostMapping("/{venteId}/vehicules/{vehiculeId}")
    public ResponseEntity<VenteResponse> ajouterVehicule(
            @PathVariable Long venteId,
            @PathVariable Long vehiculeId
    ) {
        return ResponseEntity.ok(venteService.ajouterVehicule(venteId, vehiculeId));
    }

    @DeleteMapping("/{venteId}/vehicules/{vehiculeId}")
    public ResponseEntity<VenteResponse> retirerVehicule(
            @PathVariable Long venteId,
            @PathVariable Long vehiculeId
    ) {
        return ResponseEntity.ok(venteService.retirerVehicule(venteId, vehiculeId));
    }
}
