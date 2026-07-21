package com.example.demo.repository;

import com.example.demo.entity.Vehicule;
import com.example.demo.entity.StatutVehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    Optional<Vehicule> findByImmatriculation(String immatriculation);

    boolean existsByImmatriculation(String immatriculation);

    List<Vehicule> findByStatut(StatutVehicule statut);

    @Query(
        "SELECT v FROM Vehicule v WHERE v.statut = 'EN_STOCK' " +
        "AND v NOT IN (SELECT veh FROM Vente ve JOIN ve.vehicules veh WHERE ve.statut IN ('PLANIFIEE', 'EN_COURS')) " +
        "AND (:dateDebut IS NULL OR v.dateCreation >= :dateDebut) " +
        "AND (:dateFin IS NULL OR v.dateCreation <= :dateFin)"
    )
    List<Vehicule> findVehiculesDisponiblesPourVente(
        @Param("dateDebut") LocalDateTime dateDebut,
        @Param("dateFin") LocalDateTime dateFin
    );
}