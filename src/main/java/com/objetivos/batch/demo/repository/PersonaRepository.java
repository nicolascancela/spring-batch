package com.objetivos.batch.demo.repository;

import com.objetivos.batch.demo.domain.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface PersonaRepository extends JpaRepository<Persona, BigInteger> {
}
