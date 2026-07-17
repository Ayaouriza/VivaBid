package com.example.demo.repository;

import com.example.demo.entity.Vehicule;
import com.example.demo.entity.StatutVehicule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    Optional<Vehicule> findByImmatriculation(String immatriculation);

    boolean existsByImmatriculation(String immatriculation);

    boolean existsByVin(String vin);

    List<Vehicule> findByStatut(StatutVehicule statut);

    List<Vehicule> findByVille(String ville);
}