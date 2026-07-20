package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ventes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateVente;

    @Enumerated(EnumType.STRING)
    private StatutVente statut = StatutVente.PLANIFIEE;

    @ManyToMany
    @JoinTable(
            name = "vente_vehicule",
            joinColumns = @JoinColumn(name = "vente_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicule_id")
    )
    private Set<Vehicule> vehicules = new HashSet<>();

    @Column(updatable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();
}