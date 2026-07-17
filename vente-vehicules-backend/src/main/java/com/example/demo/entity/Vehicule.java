package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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

    private String ville;

    @Column(nullable = false)
    private String marque;

    @Column(nullable = false)
    private String modele;

    private Integer anneeMec; // année de mise en circulation

    @Column(unique = true)
    private String vin;

    private Integer nombreCles;

    private Double prixPlancher; // prix minimum fixé par l'expert

    @Enumerated(EnumType.STRING)
    private StatutVehicule statut = StatutVehicule.EN_STOCK;

    private String carteGrisePath;   // chemin du fichier scanné
    private String docsJustificatifsPath;

    // Photos : relation séparée (un véhicule a plusieurs photos)
    // On l'ajoutera dans une V2 avec @OneToMany si besoin

    @Column(updatable = false)
    private java.time.LocalDateTime dateCreation = java.time.LocalDateTime.now();
}