package com.example.laborator6map.service;

import com.example.laborator6map.domain.*;
import com.example.laborator6map.repository.Repository;
import com.example.laborator6map.repository.db.EventDbRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceEveniment {
    private Repository<Long, Utilizator> utilizatorRepository;
    private EventDbRepository evenimentRepository;

    public ServiceEveniment(Repository<Long, Utilizator> utilizatorRepository, EventDbRepository evenimentRepository) {
        this.utilizatorRepository = utilizatorRepository;
        this.evenimentRepository = evenimentRepository;
    }

    public void createEvent(Long idUserCreator, String nume, String locatie, String descriere, LocalDateTime data) {
        List<Utilizator> utilizatorList = new ArrayList<>();
        utilizatorList.add(utilizatorRepository.findOne(idUserCreator));
        Eveniment eveniment = new Eveniment(utilizatorRepository.findOne(idUserCreator), nume, locatie, descriere, data, new ArrayList<>(utilizatorList), new ArrayList<>(utilizatorList));
        evenimentRepository.save(eveniment);
    }

    public Iterable<Eveniment> getAll() {
        return evenimentRepository.findAll();
    }

    public void participaLaEveniment(Long eventId, Long userId) {
        evenimentRepository.participaLaEveniment(eventId, userId);
    }

    public Eveniment findEveniment(Long id) {
        return evenimentRepository.findOne(id);
    }

    public void abonareLaEveniment(Long id, Long userIdLoggedIn) {
        evenimentRepository.abonareLaEveniment(id, userIdLoggedIn);
    }

    public void dezabonareEveniment(Long id, Long userIdLoggedIn) {
        evenimentRepository.dezabonareLaNotificari(id, userIdLoggedIn);
    }

    public void nuMaiParticipaLaEveniment(Long id, Long userIdLoggedIn) {
        evenimentRepository.nuMaiParticipaLaEveniment(id, userIdLoggedIn);
    }

    public void stergeEveniment(Long id) {
        evenimentRepository.delete(id);
    }
}
