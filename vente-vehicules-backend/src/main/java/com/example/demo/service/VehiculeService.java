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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
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

        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation(request.getImmatriculation());
        vehicule.setContrat(request.getContrat());
        vehicule.setProduit(request.getProduit());
        vehicule.setSituation(request.getSituation());
        vehicule.setMarqueModele(request.getMarqueModele());
        vehicule.setCarburant(request.getCarburant());
        vehicule.setKilometrage(request.getKilometrage());
        vehicule.setNombreCles(request.getNombreCles());
        vehicule.setDateMec(request.getDateMec());
        vehicule.setDateRecuperation(request.getDateRecuperation());
        vehicule.setDateVente(request.getDateVente());
        vehicule.setSejour(request.getSejour());
        vehicule.setEncImp(request.getEncImp());
        vehicule.setPrixAchat(request.getPrixAchat());
        vehicule.setPrixExpert(request.getPrixExpert());
        vehicule.setVep(request.getVep());
        vehicule.setDifExp(calculerDifExp(request.getPrixExpert(), request.getVep()));
        vehicule.setAcheteur(request.getAcheteur());
        vehicule.setStatut(StatutVehicule.EN_STOCK);

        Vehicule saved = vehiculeRepository.save(vehicule);
        return toResponse(saved);
    }

    private Double calculerDifExp(Double prixExpert, Double vep) {
        if (prixExpert == null || vep == null) return null;
        return prixExpert - vep;
    }

    private VehiculeResponse toResponse(Vehicule vehicule) {
        return new VehiculeResponse(
                vehicule.getId(),
                vehicule.getImmatriculation(),
                vehicule.getContrat(),
                vehicule.getProduit(),
                vehicule.getSituation(),
                vehicule.getMarqueModele(),
                vehicule.getCarburant(),
                vehicule.getKilometrage(),
                vehicule.getNombreCles(),
                vehicule.getDateMec(),
                vehicule.getDateRecuperation(),
                vehicule.getDateVente(),
                vehicule.getSejour(),
                vehicule.getEncImp(),
                vehicule.getPrixAchat(),
                vehicule.getPrixExpert(),
                vehicule.getVep(),
                vehicule.getDifExp(),
                vehicule.getAcheteur(),
                vehicule.getStatut(),
                vehicule.getDocsJustificatifsPath(),
                vehicule.getDateCreation()
        );
    }

    public ImportResultat importFromExcel(MultipartFile file) {

        List<LigneErreurImport> lignesEnErreur = new ArrayList<>();
        int totalLignes = 0;
        int nombreImportes = 0;

        Set<String> immatriculationsVues = new HashSet<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                totalLignes++;
                int numeroLigneExcel = i + 1;

                // Ordre des colonnes attendu dans le fichier Excel :
                // 0 Immatriculation, 1 Contrat, 2 Produit, 3 Situation, 4 MarqueModele,
                // 5 Carburant, 6 Kilometrage, 7 NombreCles, 8 DateMec, 9 DateRecuperation,
                // 10 DateVente, 11 Sejour, 12 EncImp, 13 PrixAchat, 14 PrixExpert,
                // 15 Vep (optionnel), 16 Acheteur (optionnel)

                String immatriculation = getCellAsString(row, 0);
                String contrat = getCellAsString(row, 1);
                String produit = getCellAsString(row, 2);
                String situation = getCellAsString(row, 3);
                String marqueModele = getCellAsString(row, 4);
                String carburant = getCellAsString(row, 5);
                String kilometrage = getCellAsString(row, 6);
                String nombreClesStr = getCellAsString(row, 7);
                String dateMecStr = getCellAsString(row, 8);
                String dateRecuperationStr = getCellAsString(row, 9);
                String dateVenteStr = getCellAsString(row, 10);
                String sejourStr = getCellAsString(row, 11);
                String encImpStr = getCellAsString(row, 12);
                String prixAchatStr = getCellAsString(row, 13);
                String prixExpertStr = getCellAsString(row, 14);
                String vepStr = getCellAsString(row, 15);
                String acheteur = getCellAsString(row, 16);

                List<String> erreurs = new ArrayList<>();

                if (immatriculation.isBlank()) {
                    erreurs.add("Immatriculation vide");
                } else if (vehiculeRepository.existsByImmatriculation(immatriculation)
                        || immatriculationsVues.contains(immatriculation)) {
                    erreurs.add("Immatriculation déjà existante");
                }

                if (contrat.isBlank()) erreurs.add("Contrat vide");
                if (produit.isBlank()) erreurs.add("Produit vide");
                if (situation.isBlank()) erreurs.add("Situation vide");
                if (marqueModele.isBlank()) erreurs.add("Marque/Modèle vide");
                if (carburant.isBlank()) erreurs.add("Carburant vide");
                if (kilometrage.isBlank()) erreurs.add("Kilométrage vide");

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

                LocalDate dateMec = null;
                if (dateMecStr.isBlank()) {
                    erreurs.add("Date MEC vide");
                } else {
                    try {
                        dateMec = parseDate(row, 8, dateMecStr);
                    } catch (Exception e) {
                        erreurs.add("Date MEC invalide");
                    }
                }

                LocalDate dateRecuperation = null;
                if (dateRecuperationStr.isBlank()) {
                    erreurs.add("Date de récupération vide");
                } else {
                    try {
                        dateRecuperation = parseDate(row, 9, dateRecuperationStr);
                    } catch (Exception e) {
                        erreurs.add("Date de récupération invalide");
                    }
                }

                LocalDate dateVente = null;
                if (dateVenteStr.isBlank()) {
                    erreurs.add("Date de vente vide");
                } else {
                    try {
                        dateVente = parseDate(row, 10, dateVenteStr);
                    } catch (Exception e) {
                        erreurs.add("Date de vente invalide");
                    }
                }

                Integer sejour = null;
                if (sejourStr.isBlank()) {
                    erreurs.add("Séjour vide");
                } else {
                    try {
                        sejour = (int) Double.parseDouble(sejourStr);
                    } catch (NumberFormatException e) {
                        erreurs.add("Séjour invalide (doit être un nombre)");
                    }
                }

                Double encImp = null;
                if (encImpStr.isBlank()) {
                    erreurs.add("Enc.Imp vide");
                } else {
                    try {
                        encImp = Double.parseDouble(encImpStr);
                    } catch (NumberFormatException e) {
                        erreurs.add("Enc.Imp invalide (doit être un nombre)");
                    }
                }

                Double prixAchat = null;
                if (prixAchatStr.isBlank()) {
                    erreurs.add("Prix d'achat vide");
                } else {
                    try {
                        prixAchat = Double.parseDouble(prixAchatStr);
                    } catch (NumberFormatException e) {
                        erreurs.add("Prix d'achat invalide (doit être un nombre)");
                    }
                }

                Double prixExpert = null;
                if (prixExpertStr.isBlank()) {
                    erreurs.add("Prix expert vide");
                } else {
                    try {
                        prixExpert = Double.parseDouble(prixExpertStr);
                        if (prixExpert <= 0) {
                            erreurs.add("Prix expert doit être positif");
                        }
                    } catch (NumberFormatException e) {
                        erreurs.add("Prix expert invalide (doit être un nombre)");
                    }
                }

                // Vep est optionnel
                Double vep = null;
                if (!vepStr.isBlank()) {
                    try {
                        vep = Double.parseDouble(vepStr);
                    } catch (NumberFormatException e) {
                        erreurs.add("Vep invalide (doit être un nombre)");
                    }
                }

                if (!erreurs.isEmpty()) {
                    lignesEnErreur.add(new LigneErreurImport(
                            numeroLigneExcel, immatriculation, contrat, produit, situation,
                            marqueModele, carburant, kilometrage, nombreClesStr,
                            dateMecStr, dateRecuperationStr, dateVenteStr, sejourStr,
                            encImpStr, prixAchatStr, prixExpertStr,
                            String.join(" ; ", erreurs)
                    ));
                    continue;
                }

                Vehicule vehicule = new Vehicule();
                vehicule.setImmatriculation(immatriculation);
                vehicule.setContrat(contrat);
                vehicule.setProduit(produit);
                vehicule.setSituation(situation);
                vehicule.setMarqueModele(marqueModele);
                vehicule.setCarburant(carburant);
                vehicule.setKilometrage(kilometrage);
                vehicule.setNombreCles(nombreCles);
                vehicule.setDateMec(dateMec);
                vehicule.setDateRecuperation(dateRecuperation);
                vehicule.setDateVente(dateVente);
                vehicule.setSejour(sejour);
                vehicule.setEncImp(encImp);
                vehicule.setPrixAchat(prixAchat);
                vehicule.setPrixExpert(prixExpert);
                vehicule.setVep(vep);
                vehicule.setDifExp(calculerDifExp(prixExpert, vep));
                vehicule.setAcheteur(acheteur.isBlank() ? null : acheteur);
                vehicule.setStatut(StatutVehicule.EN_STOCK);

                vehiculeRepository.save(vehicule);
                immatriculationsVues.add(immatriculation);
                nombreImportes++;
            }

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier Excel : " + e.getMessage());
        }

        return new ImportResultat(totalLignes, nombreImportes, lignesEnErreur.size(), lignesEnErreur);
    }

    // ----- Méthodes utilitaires -----

    private LocalDate parseDate(Row row, int cellIndex, String fallbackStr) {
        Cell cell = row.getCell(cellIndex);
        if (cell != null && cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        return LocalDate.parse(fallbackStr.trim());
    }

    private String getCellAsString(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                double value = cell.getNumericCellValue();
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
        for (int c = 0; c < 17; c++) {
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
                    "Immatriculation", "Contrat", "Produit", "Situation", "MarqueModele",
                    "Carburant", "Kilometrage", "NombreCles", "DateMec", "DateRecuperation",
                    "DateVente", "Sejour", "EncImp", "PrixAchat", "PrixExpert", "Erreur"
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
                row.createCell(1).setCellValue(ligne.getContrat());
                row.createCell(2).setCellValue(ligne.getProduit());
                row.createCell(3).setCellValue(ligne.getSituation());
                row.createCell(4).setCellValue(ligne.getMarqueModele());
                row.createCell(5).setCellValue(ligne.getCarburant());
                row.createCell(6).setCellValue(ligne.getKilometrage());
                row.createCell(7).setCellValue(ligne.getNombreCles());
                row.createCell(8).setCellValue(ligne.getDateMec());
                row.createCell(9).setCellValue(ligne.getDateRecuperation());
                row.createCell(10).setCellValue(ligne.getDateVente());
                row.createCell(11).setCellValue(ligne.getSejour());
                row.createCell(12).setCellValue(ligne.getEncImp());
                row.createCell(13).setCellValue(ligne.getPrixAchat());
                row.createCell(14).setCellValue(ligne.getPrixExpert());
                row.createCell(15).setCellValue(ligne.getMessageErreur());
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
                    "Immatriculation", "Contrat", "Produit", "Situation", "MarqueModele",
                    "Carburant", "Kilometrage", "NombreCles", "DateMec", "DateRecuperation",
                    "DateVente", "Sejour", "EncImp", "PrixAchat", "PrixExpert", "Vep", "Acheteur"
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

            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("12345-A-6");
            exampleRow.createCell(1).setCellValue("CT-0001");
            exampleRow.createCell(2).setCellValue("Crédit Auto");
            exampleRow.createCell(3).setCellValue("Saisi");
            exampleRow.createCell(4).setCellValue("Hyundai i20");
            exampleRow.createCell(5).setCellValue("Diesel");
            exampleRow.createCell(6).setCellValue("85000");
            exampleRow.createCell(7).setCellValue(1);
            exampleRow.createCell(8).setCellValue("2020-03-15");
            exampleRow.createCell(9).setCellValue("2026-01-10");
            exampleRow.createCell(10).setCellValue("2026-03-01");
            exampleRow.createCell(11).setCellValue(45);
            exampleRow.createCell(12).setCellValue(12000);
            exampleRow.createCell(13).setCellValue(90000);
            exampleRow.createCell(14).setCellValue(85000);
            // Vep et Acheteur laissés vides (optionnels)

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du template : " + e.getMessage());
        }
    }
    public void deleteVehicule(Long id) {
    if (!vehiculeRepository.existsById(id)) {
        throw new RuntimeException("Véhicule introuvable avec l'id : " + id);
    }
    vehiculeRepository.deleteById(id);
}

public VehiculeResponse updateVehicule(Long id, VehiculeRequest request) {
    Vehicule vehicule = vehiculeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Véhicule introuvable avec l'id : " + id));

    // Si l'immatriculation change, on vérifie qu'elle n'est pas déjà utilisée par un AUTRE véhicule
    if (request.getImmatriculation() != null
            && !request.getImmatriculation().equals(vehicule.getImmatriculation())
            && vehiculeRepository.existsByImmatriculation(request.getImmatriculation())) {
        throw new RuntimeException("Un véhicule avec cette immatriculation existe déjà.");
    }

    if (request.getImmatriculation() != null) vehicule.setImmatriculation(request.getImmatriculation());
    if (request.getContrat() != null) vehicule.setContrat(request.getContrat());
    if (request.getProduit() != null) vehicule.setProduit(request.getProduit());
    if (request.getSituation() != null) vehicule.setSituation(request.getSituation());
    if (request.getMarqueModele() != null) vehicule.setMarqueModele(request.getMarqueModele());
    if (request.getCarburant() != null) vehicule.setCarburant(request.getCarburant());
    if (request.getKilometrage() != null) vehicule.setKilometrage(request.getKilometrage());
    if (request.getNombreCles() != null) vehicule.setNombreCles(request.getNombreCles());
    if (request.getDateMec() != null) vehicule.setDateMec(request.getDateMec());
    if (request.getDateRecuperation() != null) vehicule.setDateRecuperation(request.getDateRecuperation());
    if (request.getDateVente() != null) vehicule.setDateVente(request.getDateVente());
    if (request.getSejour() != null) vehicule.setSejour(request.getSejour());
    if (request.getEncImp() != null) vehicule.setEncImp(request.getEncImp());
    if (request.getPrixAchat() != null) vehicule.setPrixAchat(request.getPrixAchat());
    if (request.getPrixExpert() != null) vehicule.setPrixExpert(request.getPrixExpert());
    if (request.getVep() != null) vehicule.setVep(request.getVep());
    if (request.getAcheteur() != null) vehicule.setAcheteur(request.getAcheteur());

    // Recalcul automatique de difExp si prixExpert ou vep ont changé
    vehicule.setDifExp(calculerDifExp(vehicule.getPrixExpert(), vehicule.getVep()));

    Vehicule saved = vehiculeRepository.save(vehicule);
    return toResponse(saved);
}

}