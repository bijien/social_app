package com.example.laborator6map.service;

import com.example.laborator6map.domain.Utilizator;
import com.example.laborator6map.repository.Repository;
import com.example.laborator6map.validators.RepositoryException;
import com.example.laborator6map.validators.UserValidator;

public class ServiceUser {
    private Repository<Long, Utilizator> repository;
    private UserValidator userValidator;

    public ServiceUser(Repository<Long, Utilizator> repository, UserValidator userValidator) {
        this.repository = repository;
        this.userValidator = userValidator;
    }

    /**
     * create an user with firstName and lastName and saves it
     *
     * @param firstName
     * @param lastName
     */
    public void addUtilizator(String firstName, String lastName, String userName, String password) {
        Utilizator utilizator = new Utilizator(firstName, lastName, userName, password);
        boolean utilizatorExistent = false;
        for(Utilizator utilizator1 : repository.findAll()) {
            if(utilizator.getUserName().equals(utilizator1.getUserName())) {
                if(utilizator.getUserName().equals(utilizator1.getUserName())){
                    utilizatorExistent = true;
                    break;
                }

            }
        }
        if(utilizatorExistent) {
            throw new RepositoryException("Utilizator existent!");
        }
        userValidator.validate(utilizator);
        repository.save(utilizator);
    }

    /**
     * @return the list of users
     */
    public Iterable<Utilizator> getAll() {
        return repository.findAll();
    }


    public Utilizator findUser(Long id) {
        return repository.findOne(id);
    }


    public Utilizator findUserByUsername(String userName) {
        for(Utilizator utilizator: repository.findAll()){
            if(utilizator.getUserName().equals(userName))
                return utilizator;
        }
        throw new RepositoryException("Utilizator neexistent!");
    }
}
