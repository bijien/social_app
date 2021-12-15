package com.example.laborator6map.repository;
import com.example.laborator6map.domain.Utilizator;
import com.example.laborator6map.repository.AbstractFileRepository;
import com.example.laborator6map.validators.Validator;

import java.util.List;

public class UtilizatorFileRepository extends AbstractFileRepository<Long, Utilizator> {

    public UtilizatorFileRepository(String fileName, Validator<Utilizator> validator) {
        super(fileName, validator);
    }

    @Override
    public Utilizator extractEntity(List<String> attributes) {
        Utilizator user = new Utilizator(attributes.get(1),attributes.get(2),attributes.get(3),attributes.get(4));
        user.setId(Long.parseLong(attributes.get(0)));

        return user;
    }

    @Override
    protected String createEntityAsString(Utilizator entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName();
    }
}
