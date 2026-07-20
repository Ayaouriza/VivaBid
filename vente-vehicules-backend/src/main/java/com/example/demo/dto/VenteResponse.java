package com.example.demo.dto;

import com.example.demo.entity.StatutVente;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VenteResponse {

    private Long id;
    private LocalDateTime dateVente;
    private StatutVente statut;
    private List<VehiculeSimpleResponse> vehicules;
    private LocalDateTime dateCreation;
}