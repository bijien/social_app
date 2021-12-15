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
        if(findUserByUsername(userName)!=null)
            throw new RepositoryException("Utilizator existent!");
        userValidator.validate(utilizator);
        repository.save(utilizator);
    }

    /**
     * @return the list of users
     */
    public Iterable<Utilizator> getAll() {
        return repository.findAll();
    }

    /**
     * delete the user with the given id
     *
     * @param id
     * @throws RepositoryException if the user doesn't exist
     */
    public void deleteUtilizator(Long id) {
        if (repository.findOne(id) == null)
            throw new RepositoryException("Utilizator neexistent");
        repository.delete(id);
    }

    public Utilizator findUser(Long id) {
        return repository.findOne(id);
    }

    public Utilizator findUserByFirstNameAndLastName(String firstName, String lastName) {
        for(Utilizator utilizator: repository.findAll()){
            if(utilizator.getFirstName().equals(firstName) && utilizator.getLastName().equals(lastName))
                return utilizator;
        }
        throw new RepositoryException("Utilizator neexistent!");
    }

    public Utilizator findUserByUsername(String userName) {
        for(Utilizator utilizator: repository.findAll()){
            if(utilizator.getFirstName().equals(userName))
                return utilizator;
        }
        throw new RepositoryException("Utilizator neexistent!");
    }
}
