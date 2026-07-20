package com.example.demo.repository;

import com.example.demo.entity.Vente;
import com.example.demo.entity.StatutVente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenteRepository extends JpaRepository<Vente, Long> {

    List<Vente> findByStatut(StatutVente statut);

    List<Vente> findAllByOrderByDateVenteDesc();
}