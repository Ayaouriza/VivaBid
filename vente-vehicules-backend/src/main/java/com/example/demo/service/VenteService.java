package com.example.demo.service;

import com.example.demo.dto.VehiculeSimpleResponse;
import com.example.demo.dto.VenteRequest;
import com.example.demo.dto.VenteResponse;
import com.example.demo.entity.StatutVente;
import com.example.demo.entity.Vehicule;
import com.example.demo.entity.Vente;
import com.example.demo.repository.VehiculeRepository;
import com.example.demo.repository.VenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VenteService {

    private final VenteRepository venteRepository;
    private final VehiculeRepository vehiculeRepository;

    public List<VenteResponse> getAllVentes() {
        return venteRepository.findAllByOrderByDateVenteDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public VenteResponse getVenteById(Long id) {
        Vente vente = venteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vente introuvable avec l'id : " + id));
        return toResponse(vente);
    }

    public VenteResponse createVente(VenteRequest request) {
        if (request.getDateVente() == null || request.getDateVente().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("La date de la vente ne peut pas être dans le passé.");
        }

        Vente vente = new Vente();
        vente.setDateVente(request.getDateVente());
        vente.setStatut(StatutVente.PLANIFIEE);

        Vente saved = venteRepository.save(vente);
        return toResponse(saved);
    }

   public List<VehiculeSimpleResponse> getVehiculesDisponibles(LocalDate dateDebut, LocalDate dateFin, Boolean chargeAujourdhui) {
    LocalDateTime debut = null;
    LocalDateTime fin = null;

    if (Boolean.TRUE.equals(chargeAujourdhui)) {
        LocalDate aujourdhui = LocalDate.now();
        debut = aujourdhui.atStartOfDay();
        fin = aujourdhui.atTime(LocalTime.MAX);
    } else {
        if (dateDebut != null) {
            debut = dateDebut.atStartOfDay();
        }
        if (dateFin != null) {
            fin = dateFin.atTime(LocalTime.MAX);
        }
    }

    return vehiculeRepository.findVehiculesDisponiblesPourVente(debut, fin)
            .stream()
            .map(this::toSimpleResponse)
            .toList();
    }
    public VenteResponse ajouterVehicule(Long venteId, Long vehiculeId) {
        Vente vente = venteRepository.findById(venteId)
                .orElseThrow(() -> new RuntimeException("Vente introuvable"));

        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));

        boolean dejaAffecte = venteRepository.findByStatut(StatutVente.PLANIFIEE).stream()
                .anyMatch(v -> !v.getId().equals(venteId) && v.getVehicules().contains(vehicule));

        if (dejaAffecte) {
            throw new RuntimeException("Ce véhicule est déjà affecté à une autre vente active.");
        }

        vente.getVehicules().add(vehicule);
        Vente saved = venteRepository.save(vente);
        return toResponse(saved);
    }

    public VenteResponse retirerVehicule(Long venteId, Long vehiculeId) {
        Vente vente = venteRepository.findById(venteId)
                .orElseThrow(() -> new RuntimeException("Vente introuvable"));

        vente.getVehicules().removeIf(v -> v.getId().equals(vehiculeId));

        Vente saved = venteRepository.save(vente);
        return toResponse(saved);
    }

    private VenteResponse toResponse(Vente vente) {
        List<VehiculeSimpleResponse> vehicules = vente.getVehicules()
                .stream()
                .map(this::toSimpleResponse)
                .toList();

        StatutVente statutAffiche = calculerStatutReel(vente);

        return new VenteResponse(
                vente.getId(),
                vente.getDateVente(),
                statutAffiche,
                vehicules,
                vente.getDateCreation()
        );
    }

    private StatutVente calculerStatutReel(Vente vente) {
        if (vente.getStatut() == StatutVente.TERMINEE) {
            return StatutVente.TERMINEE;
        }

        if (vente.getDateVente().isAfter(java.time.LocalDateTime.now())) {
            return StatutVente.PLANIFIEE;
        } else {
            return StatutVente.EN_COURS;
        }
    }

    private VehiculeSimpleResponse toSimpleResponse(Vehicule vehicule) {
        return new VehiculeSimpleResponse(
                vehicule.getId(),
                vehicule.getImmatriculation(),
                vehicule.getMarqueModele(),
                vehicule.getPrixExpert()
        );
    }
}