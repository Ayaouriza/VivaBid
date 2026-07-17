package com.example.demo.controller;

import com.example.demo.dto.VehiculeRequest;
import com.example.demo.dto.VehiculeResponse;
import com.example.demo.service.VehiculeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.ImportResultat;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/import")
    public ResponseEntity<ImportResultat> importVehicules(@RequestParam("file") MultipartFile file) {
        ImportResultat resultat = vehiculeService.importFromExcel(file);
        return ResponseEntity.ok(resultat);
    }

    @PostMapping("/import/erreurs")
    public ResponseEntity<ByteArrayResource> telechargerFichierErreurs(
          @RequestBody List<com.example.demo.dto.LigneErreurImport> lignesEnErreur) {

        byte[] fichier = vehiculeService.genererFichierErreurs(lignesEnErreur);
        ByteArrayResource resource = new ByteArrayResource(fichier);

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vehicules_erreurs.xlsx")
            .body(resource);
    }

    @GetMapping("/import/template")
    public ResponseEntity<ByteArrayResource> telechargerTemplate() {

         byte[] fichier = vehiculeService.genererTemplateVierge();
         ByteArrayResource resource = new ByteArrayResource(fichier);

         return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template_vehicules.xlsx")
            .body(resource);
    }
}