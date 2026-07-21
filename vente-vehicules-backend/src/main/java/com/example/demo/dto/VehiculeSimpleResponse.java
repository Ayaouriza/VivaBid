package com.example.demo.dto;
import com.example.demo.entity.StatutVehicule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeSimpleResponse {

    private Long id;
    private String immatriculation;
    private String marqueModele;
    private Double prixExpert;
    private StatutVehicule statut;
    private LocalDateTime dateCreation;
}