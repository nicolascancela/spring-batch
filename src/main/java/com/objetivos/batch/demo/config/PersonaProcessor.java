package com.objetivos.batch.demo.config;

import com.objetivos.batch.demo.domain.Persona;
import org.springframework.batch.item.ItemProcessor;

public class PersonaProcessor implements ItemProcessor<Persona, Persona> {
    @Override
    public Persona process(Persona item) throws Exception {
        //Aca ponemos la lógica de negocio.
        item.setCuit(calcularCUIT(item));
        return item;
    }

    private String calcularCUIT(Persona persona){
        String dni = String.valueOf(persona.getDni());
        String sexo = persona.getSexo();
        String codigoSexo =null;
        int[] coefficients = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
        int sum = 0;

        if (sexo.equalsIgnoreCase("m")) {
            sum += 20;
            codigoSexo = "20";
        } else if (sexo.equalsIgnoreCase("f")) {
            sum += 27;
            codigoSexo = "27";
        }

        String codigoSexoConDNI = codigoSexo + dni;

        for (int i = 0; i < coefficients.length; i++) {
            sum += (Character.getNumericValue(codigoSexoConDNI.charAt(i)) * coefficients[i]);
        }

        int resto = sum % 11;
        int codigoVerificacion = resto == 0 ? 0 : resto == 1 ? 9 : 11 - resto;
        return codigoSexoConDNI + codigoVerificacion;
    }
}
