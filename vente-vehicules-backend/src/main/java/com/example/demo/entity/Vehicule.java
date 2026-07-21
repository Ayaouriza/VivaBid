package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String immatriculation;

    @Column(nullable = false)
    private String contrat;

    @Column(nullable = false)
    private String produit;

    @Column(nullable = false)
    private String situation;

    @Column(nullable = false)
    private String marqueModele;

    @Column(nullable = false)
    private String carburant;

    @Column(nullable = false)
    private String kilometrage; // texte libre : nombre ou "sans clé"

    @Column(nullable = false)
    private Integer nombreCles;

    @Column(nullable = false)
    private LocalDate dateMec;

    @Column(nullable = false)
    private LocalDate dateRecuperation;

    @Column(nullable = false)
    private LocalDate dateVente;

    @Column(nullable = false)
    private Integer sejour;

    @Column(nullable = false)
    private Double encImp;

    @Column(nullable = false)
    private Double prixAchat;

    @Column(nullable = false)
    private Double prixExpert;

    private Double vep; // optionnel à l'import, rempli après la 1ère tentative de vente

    private Double difExp; // calculé automatiquement (prixExpert - vep)

    private String acheteur; // optionnel, rempli après vente

    @Enumerated(EnumType.STRING)
    private StatutVehicule statut = StatutVehicule.EN_STOCK;

    private String docsJustificatifsPath;

    @Column(updatable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();
}