package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeRequest {

    private String immatriculation;
    private String ville;
    private String marque;
    private String modele;
    private Integer anneeMec;
    private String vin;
    private Integer nombreCles;
    private Double prixExpert;
}