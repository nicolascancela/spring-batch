package com.objetivos.batch.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Data
@Entity
public class Persona {
    @Id
    @GeneratedValue
    private BigInteger id;
    private Integer dni;
    private String nombre;
    private String apellido;
    private String sexo;
    private String cuit;
}
