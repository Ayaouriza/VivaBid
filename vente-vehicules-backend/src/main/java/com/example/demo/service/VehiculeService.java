package com.example.demo.service;

import com.example.demo.dto.VehiculeRequest;
import com.example.demo.dto.VehiculeResponse;
import com.example.demo.entity.StatutVehicule;
import com.example.demo.entity.Vehicule;
import com.example.demo.repository.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.demo.dto.ImportResultat;
import com.example.demo.dto.LigneErreurImport;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;

    public List<VehiculeResponse> getAllVehicules() {
        return vehiculeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public VehiculeResponse getVehiculeById(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable avec l'id : " + id));
        return toResponse(vehicule);
    }

    public VehiculeResponse createVehicule(VehiculeRequest request) {

        if (vehiculeRepository.existsByImmatriculation(request.getImmatriculation())) {
            throw new RuntimeException("Un véhicule avec cette immatriculation existe déjà.");
        }

        if (request.getVin() != null && vehiculeRepository.existsByVin(request.getVin())) {
            throw new RuntimeException("Un véhicule avec ce VIN existe déjà.");
        }

        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation(request.getImmatriculation());
        vehicule.setVille(request.getVille());
        vehicule.setMarque(request.getMarque());
        vehicule.setModele(request.getModele());
        vehicule.setAnneeMec(request.getAnneeMec());
        vehicule.setVin(request.getVin());
        vehicule.setNombreCles(request.getNombreCles());
        vehicule.setPrixPlancher(request.getPrixPlancher());
        vehicule.setStatut(StatutVehicule.EN_STOCK);

        Vehicule saved = vehiculeRepository.save(vehicule);
        return toResponse(saved);
    }

    private VehiculeResponse toResponse(Vehicule vehicule) {
        return new VehiculeResponse(
                vehicule.getId(),
                vehicule.getImmatriculation(),
                vehicule.getVille(),
                vehicule.getMarque(),
                vehicule.getModele(),
                vehicule.getAnneeMec(),
                vehicule.getVin(),
                vehicule.getNombreCles(),
                vehicule.getPrixPlancher(),
                vehicule.getStatut(),
                vehicule.getCarteGrisePath(),
                vehicule.getDocsJustificatifsPath(),
                vehicule.getDateCreation()
        );
    }
    public ImportResultat importFromExcel(MultipartFile file) {

    List<LigneErreurImport> lignesEnErreur = new ArrayList<>();
    int totalLignes = 0;
    int nombreImportes = 0;

    // Pour détecter les doublons À L'INTÉRIEUR du même fichier
    Set<String> immatriculationsVues = new HashSet<>();
    Set<String> vinsVus = new HashSet<>();

    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

        Sheet sheet = workbook.getSheetAt(0);

        // On commence à la ligne 1 (index 1) pour sauter l'en-tête (ligne 0)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);
            if (row == null || isRowEmpty(row)) {
                continue; // on ignore les lignes vides
            }

            totalLignes++;
            int numeroLigneExcel = i + 1; // +1 car Excel affiche à partir de 1, pas 0

            String immatriculation = getCellAsString(row, 0);
            String ville = getCellAsString(row, 1);
            String marque = getCellAsString(row, 2);
            String modele = getCellAsString(row, 3);
            String anneeMecStr = getCellAsString(row, 4);
            String vin = getCellAsString(row, 5);
            String nombreClesStr = getCellAsString(row, 6);
            String prixPlancherStr = getCellAsString(row, 7);

            List<String> erreurs = new ArrayList<>();

            // ----- Validation immatriculation -----
            if (immatriculation.isBlank()) {
                erreurs.add("Immatriculation vide");
            } else if (vehiculeRepository.existsByImmatriculation(immatriculation)
                    || immatriculationsVues.contains(immatriculation)) {
                erreurs.add("Immatriculation déjà existante");
            }

            // ----- Validation ville -----
            if (ville.isBlank()) {
                erreurs.add("Ville vide");
            } else if (!ville.matches("^[a-zA-ZÀ-ÿ\\s\\-]+$")) {
                erreurs.add("Ville invalide (doit contenir uniquement des lettres)");
            }

            // ----- Validation marque -----
            if (marque.isBlank()) {
                erreurs.add("Marque vide");
            } else if (!marque.matches("^[a-zA-ZÀ-ÿ\\s\\-]+$")) {
                erreurs.add("Marque invalide (doit contenir uniquement des lettres)");
            }

            // ----- Validation modèle -----
            if (modele.isBlank()) {
                erreurs.add("Modèle vide");
            }

            // ----- Validation année -----
            Integer anneeMec = null;
            if (anneeMecStr.isBlank()) {
                erreurs.add("Année vide");
            } else {
                try {
                    anneeMec = (int) Double.parseDouble(anneeMecStr);
                    if (anneeMec < 1980 || anneeMec > 2026) {
                        erreurs.add("Année hors limite (1980-2026)");
                    }
                } catch (NumberFormatException e) {
                    erreurs.add("Année invalide (doit être un nombre)");
                }
            }

            // ----- Validation VIN -----
            if (vin.isBlank()) {
                erreurs.add("VIN vide");
            } else if (vin.length() != 17) {
                erreurs.add("VIN invalide (doit contenir 17 caractères)");
            } else if (vehiculeRepository.existsByVin(vin) || vinsVus.contains(vin)) {
                erreurs.add("VIN déjà existant");
            }

            // ----- Validation nombre de clés -----
            Integer nombreCles = null;
            if (nombreClesStr.isBlank()) {
                erreurs.add("Nombre de clés vide");
            } else {
                try {
                    nombreCles = (int) Double.parseDouble(nombreClesStr);
                    if (nombreCles < 0 || nombreCles > 5) {
                        erreurs.add("Nombre de clés hors limite (0-5)");
                    }
                } catch (NumberFormatException e) {
                    erreurs.add("Nombre de clés invalide (doit être un nombre)");
                }
            }

            // ----- Validation prix plancher -----
            Double prixPlancher = null;
            if (prixPlancherStr.isBlank()) {
                erreurs.add("Prix plancher vide");
            } else {
                try {
                    prixPlancher = Double.parseDouble(prixPlancherStr);
                    if (prixPlancher <= 0) {
                        erreurs.add("Prix plancher doit être positif");
                    }
                } catch (NumberFormatException e) {
                    erreurs.add("Prix plancher invalide (doit être un nombre)");
                }
            }

            // ----- Si erreurs, on rejette la ligne -----
            if (!erreurs.isEmpty()) {
                lignesEnErreur.add(new LigneErreurImport(
                        numeroLigneExcel, immatriculation, ville, marque, modele,
                        anneeMecStr, vin, nombreClesStr, prixPlancherStr,
                        String.join(" ; ", erreurs)
                ));
                continue;
            }

            // ----- Sinon, on enregistre le véhicule -----
            Vehicule vehicule = new Vehicule();
            vehicule.setImmatriculation(immatriculation);
            vehicule.setVille(ville);
            vehicule.setMarque(marque);
            vehicule.setModele(modele);
            vehicule.setAnneeMec(anneeMec);
            vehicule.setVin(vin);
            vehicule.setNombreCles(nombreCles);
            vehicule.setPrixPlancher(prixPlancher);
            vehicule.setStatut(StatutVehicule.EN_STOCK);

            vehiculeRepository.save(vehicule);
            immatriculationsVues.add(immatriculation);
            vinsVus.add(vin);
            nombreImportes++;
        }

    } catch (IOException e) {
        throw new RuntimeException("Erreur lors de la lecture du fichier Excel : " + e.getMessage());
    }

    return new ImportResultat(totalLignes, nombreImportes, lignesEnErreur.size(), lignesEnErreur);
}

// ----- Méthodes utilitaires -----

private String getCellAsString(Row row, int cellIndex) {
    Cell cell = row.getCell(cellIndex);
    if (cell == null) return "";

    return switch (cell.getCellType()) {
        case STRING -> cell.getStringCellValue().trim();
        case NUMERIC -> {
            double value = cell.getNumericCellValue();
            // Si c'est un nombre entier (ex: 2023.0), on enlève le ".0"
            if (value == Math.floor(value)) {
                yield String.valueOf((long) value);
            }
            yield String.valueOf(value);
        }
        case BLANK -> "";
        default -> cell.toString().trim();
    };
}

private boolean isRowEmpty(Row row) {
    for (int c = 0; c < 8; c++) {
        Cell cell = row.getCell(c);
        if (cell != null && cell.getCellType() != CellType.BLANK
                && !getCellAsString(row, c).isBlank()) {
            return false;
        }
    }
    return true;
}
public byte[] genererFichierErreurs(List<LigneErreurImport> lignesEnErreur) {

    try (Workbook workbook = new XSSFWorkbook();
         ByteArrayOutputStream out = new ByteArrayOutputStream()) {

        Sheet sheet = workbook.createSheet("Erreurs");

        String[] headers = {
                "Immatriculation", "Ville", "Marque", "Modele",
                "AnneeMec", "VIN", "NombreCles", "PrixPlancher", "Erreur"
        };

        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIndex = 1;
        for (LigneErreurImport ligne : lignesEnErreur) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(ligne.getImmatriculation());
            row.createCell(1).setCellValue(ligne.getVille());
            row.createCell(2).setCellValue(ligne.getMarque());
            row.createCell(3).setCellValue(ligne.getModele());
            row.createCell(4).setCellValue(ligne.getAnneeMec());
            row.createCell(5).setCellValue(ligne.getVin());
            row.createCell(6).setCellValue(ligne.getNombreCles());
            row.createCell(7).setCellValue(ligne.getPrixPlancher());
            row.createCell(8).setCellValue(ligne.getMessageErreur());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(out);
        return out.toByteArray();

    } catch (IOException e) {
        throw new RuntimeException("Erreur lors de la génération du fichier d'erreurs : " + e.getMessage());
    }
}
public byte[] genererTemplateVierge() {

    try (Workbook workbook = new XSSFWorkbook();
         ByteArrayOutputStream out = new ByteArrayOutputStream()) {

        Sheet sheet = workbook.createSheet("Vehicules");

        String[] headers = {
                "Immatriculation", "Ville", "Marque", "Modele",
                "AnneeMec", "VIN", "NombreCles", "PrixPlancher"
        };

        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

        // Une ligne d'exemple pour guider l'agent de saisie
        Row exampleRow = sheet.createRow(1);
        exampleRow.createCell(0).setCellValue("12345-A-6");
        exampleRow.createCell(1).setCellValue("Casablanca");
        exampleRow.createCell(2).setCellValue("Hyundai");
        exampleRow.createCell(3).setCellValue("i20");
        exampleRow.createCell(4).setCellValue(2023);
        exampleRow.createCell(5).setCellValue("NLHBN51JBPZ361261");
        exampleRow.createCell(6).setCellValue(1);
        exampleRow.createCell(7).setCellValue(85000);

        workbook.write(out);
        return out.toByteArray();

    } catch (IOException e) {
        throw new RuntimeException("Erreur lors de la génération du template : " + e.getMessage());
    }
}

}