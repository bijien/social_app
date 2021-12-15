package com.example.laborator6map.validators;

import com.example.laborator6map.domain.Utilizator;

public class UserValidator implements Validator<Utilizator>{
    /**
     * validates an entity
     * @param entity
     * @throws ValidationException if the firstName or the lastName are empty
     */
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        String errors = "";
        if(entity.getFirstName().equals(""))
            errors=(new StringBuilder()).append(errors).append("Prenume invalid!\n").toString();
        if(entity.getLastName().equals(""))
            errors=(new StringBuilder()).append(errors).append("Nume invalid!\n").toString();
        if(entity.getUserName().equals(""))
            errors=(new StringBuilder()).append(errors).append("Username invalid!\n").toString();
        if(entity.getPassword().equals(""))
            errors=(new StringBuilder()).append(errors).append("Parola invalida!\n").toString();
        if(errors.length()>0)
            throw new ValidationException(errors);
    }
}
