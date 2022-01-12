package com.example.laborator6map.service;

import com.example.laborator6map.domain.Prietenie;
import com.example.laborator6map.domain.Tuple;
import com.example.laborator6map.domain.Utilizator;
import com.example.laborator6map.repository.Repository;
import com.example.laborator6map.validators.RepositoryException;
import com.example.laborator6map.validators.ValidationException;

import java.util.*;
import java.util.stream.Collectors;

public class ServicePrietenie {
    private Repository<Long, Utilizator> repositoryUser;
    private Repository<Tuple<Long, Long>, Prietenie> repositoryPrietenie;

    public ServicePrietenie(Repository<Long, Utilizator> repositoryUser, Repository<Tuple<Long, Long>, Prietenie> repositoryPrietenie) {
        this.repositoryUser = repositoryUser;
        this.repositoryPrietenie = repositoryPrietenie;
    }

    /**
     * create a frendship between user with id1 and user with id2 and saves it
     *
     * @param id1
     * @param id2
     * @throws RepositoryException if one of the users doesn't exist or if the friendship exists
     */
    public void addPrietenie(Long id1, Long id2) {
        if (repositoryUser.findOne(id1) == null || repositoryUser.findOne(id2) == null)
            throw new RepositoryException("Utilizator neexistent");
        if (repositoryPrietenie.findOne(new Tuple<Long, Long>(id1, id2)) != null)
            throw new RepositoryException("Prietenie existenta");
        Prietenie prietenie = new Prietenie();
        prietenie.setId(new Tuple<Long, Long>(id1, id2));
        prietenie.setStatus("pending");
        repositoryPrietenie.save(prietenie);
    }

    /**
     * delete the friendship between user with id1 and user with id2
     *
     * @param id1
     * @param id2
     * @throws RepositoryException if one of the users doesn't exist
     */
    public void deletePrietenie(Long id1, Long id2) {
        if (repositoryUser.findOne(id1) == null || repositoryUser.findOne(id2) == null)
            throw new RepositoryException("Utilizator neexistent");
        repositoryPrietenie.delete(new Tuple<>(id1, id2));
    }

    /**
     * @return the list of all friendships
     */
    public Iterable<Prietenie> getAll() {
        return repositoryPrietenie.findAll();
    }

    public Iterable<Prietenie> friendListForAUser(Long userId) {
        if (repositoryUser.findOne(userId) == null)
            throw new RepositoryException("Utilizator neexistent");

        Set<Prietenie> friendList = new HashSet<>();
        for (Prietenie prietenie : this.getAll()) {
            friendList.add(prietenie);
        }

        return friendList.stream()
                .filter(x -> (x.getId().getLeft().equals(userId) || x.getId().getRight().equals(userId))
                        && x.getStatus().equals("approved"))
                .collect(Collectors.toList());
    }

    public Iterable<Prietenie> friendListForAUserAndDate(Long userId, Integer month) {
        if (repositoryUser.findOne(userId) == null)
            throw new RepositoryException("Utilizator neexistent");
        if (month < 1 || month > 12)
            throw new ValidationException("Luna invalida");
        HashSet<Prietenie> friendList = (HashSet<Prietenie>) this.getAll();
        List<Prietenie> finaList = friendList.stream()
                .filter(x -> (x.getId().getLeft().equals(userId) || x.getId().getRight().equals(userId))
                && x.getLocalDate().getMonth().getValue() == month && x.getStatus().equals("approved"))
                .collect(Collectors.toList());
        return finaList;
    }

    public void acceptaPrietenie(Long idSender, Long idRecever) {
        Prietenie prietenie = repositoryPrietenie.findOne(new Tuple<>(idSender, idRecever));
        if (prietenie == null)
            throw new RepositoryException("Cerere de prietenie neexistenta");
        if (prietenie.getStatus().equals("pending")) {
            prietenie.setStatus("approved");
            repositoryPrietenie.update(prietenie);
        }
    }

    public void respingePrietenie(Long idSender, Long idRecever) {
        Prietenie prietenie = repositoryPrietenie.findOne(new Tuple<>(idSender, idRecever));
        if (prietenie == null)
            throw new RepositoryException("Cerere de prietenie neexistenta");
        if (prietenie.getStatus().equals("pending")) {
            prietenie.setStatus("declined");
            repositoryPrietenie.update(prietenie);
        }
    }

    public boolean suntPrieteni(Long id1, Long id2) {
        return repositoryPrietenie.findOne(new Tuple<Long, Long>(id1, id2)) != null;
    }

    public Iterable<Prietenie> friendRequestForAUser(Long userId) {
        if (repositoryUser.findOne(userId) == null)
            throw new RepositoryException("Utilizator neexistent");

        Set<Prietenie> friendList = new HashSet<>();
        for (Prietenie prietenie : this.getAll()) {
            friendList.add(prietenie);
        }

        return friendList.stream()
                .filter(x -> (x.getId().getRight().equals(userId) && (x.getStatus().equals("pending") || x.getStatus().equals("declined"))))
                .collect(Collectors.toList());
    }

    public Iterable<Prietenie> sentFriendRequestsForAUser(Long userId) {
        if (repositoryUser.findOne(userId) == null)
            throw new RepositoryException("Utilizator neexistent");

        Set<Prietenie> friendList = new HashSet<>();
        for (Prietenie prietenie : this.getAll()) {
            friendList.add(prietenie);
        }

        return friendList.stream()
                .filter(x -> (x.getId().getLeft().equals(userId) && x.getStatus().equals("pending")))
                .collect(Collectors.toList());
    }


}
