package com.example.laborator6map.service;

import com.example.laborator6map.domain.*;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ServiceNetwork {
    private ServiceUser serviceUser;
    private ServicePrietenie servicePrietenie;
    private ServiceMessage serviceMessage;
    private ServiceEveniment serviceEveniment;

    public ServiceNetwork(ServiceUser serviceUser, ServicePrietenie servicePrietenie, ServiceMessage serviceMessage, ServiceEveniment serviceEveniment) {
        this.serviceUser = serviceUser;
        this.servicePrietenie = servicePrietenie;
        this.serviceMessage = serviceMessage;
        this.serviceEveniment = serviceEveniment;
    }

    public void addUtilizator(String firstName, String lastName, String userName, String password) {
        serviceUser.addUtilizator(firstName, lastName, userName, password);
    }



    public void addPrietenie(Long id1, Long id2) {
        servicePrietenie.addPrietenie(id1, id2);
    }

    public void deletePrietenie(Long id1, Long id2) {
        servicePrietenie.deletePrietenie(id1, id2);
    }




    public Iterable<Prietenie> friendListForAUser(Long userId) {
        return servicePrietenie.friendListForAUser(userId);
    }

    public Utilizator findUser(Long userId) {
        return serviceUser.findUser(userId);
    }


    public void trimiteMesaj(Long from, List<Long> to, String mesaj) {
        serviceMessage.trimiteMesaj(from, to, mesaj);
    }


    public Iterable<Message> conversatieUtilizatori(Long id1, Long id2) {
        return serviceMessage.conversatieUtilizatori(id1, id2);
    }

    public void acceptaPrietenie(Long id1, Long id2) {
        servicePrietenie.acceptaPrietenie(id1, id2);
    }

    public void respingePrietenie(Long id1, Long id2) {
        servicePrietenie.respingePrietenie(id1, id2);
    }


    public Utilizator findUserByUsername(String userName) {
        return serviceUser.findUserByUsername(userName);
    }

    public Iterable<Utilizator> listaUtilizatoriCareNusuntPrieteni(Long id1) {
        List<Utilizator> filteredList = new ArrayList<>();
        for(Utilizator utilizator:serviceUser.getAll()) {
            if((!servicePrietenie.suntPrieteni(id1, utilizator.getId())) && !(id1.equals(utilizator.getId()))) {
                filteredList.add(utilizator);
            }
        }
        return filteredList;
    }

    public Iterable<Prietenie> friendRequestForAUser(Long userId){
        return servicePrietenie.friendRequestForAUser(userId);
    }

    public Iterable<Prietenie> sentFriendRequestsForAUser(Long userId) {
        return servicePrietenie.sentFriendRequestsForAUser(userId);
    }

    public List<Message> findMessagesByDateFromAFriend(LocalDate dataInceput, LocalDate dataSfarsit, Long userId, Long friendId) {
        List<Message> messageList = new ArrayList<>();
        Iterable<Message> messages = serviceMessage.conversatieUtilizatori(userId, friendId);
        for (Message message : messages) {
            if (message.getFrom().getId().equals(friendId) && message.getData().toLocalDate().compareTo(dataInceput) >= 0 && message.getData().toLocalDate().compareTo(dataSfarsit) <= 0)
                messageList.add(message);
        }
        return messageList;
    }

    public List<Utilizator> findNewFriendsByDate(LocalDate dataInceput, LocalDate dataSfarsit, Long userId) {
        List<Utilizator> utilizatoriList = new ArrayList<>();
        List<Prietenie> prietenieList = new ArrayList<>();
        Iterable<Prietenie> prietenii = servicePrietenie.friendListForAUser(userId);
        for (Prietenie prietenie : prietenii) {
            if (prietenie.getLocalDate().compareTo(dataInceput) >= 0 && prietenie.getLocalDate().compareTo(dataSfarsit) <= 0 && prietenie.getStatus().equals("approved"))
                prietenieList.add(prietenie);
        }
        for (Prietenie p : prietenieList) {
            if (p.getId().getLeft().equals(userId))
                utilizatoriList.add(findUser(p.getId().getRight()));
            else
                utilizatoriList.add(findUser(p.getId().getLeft()));
        }
        return utilizatoriList;
    }

    public List<Message> findMessagesByDate(LocalDate dataInceput, LocalDate dataSfarsit, Long userId) {
        List<Message> messageList = new ArrayList<>();
        Iterable<Message> messages = serviceMessage.mesajeUtilizator(userId);
        for (Message message : messages) {
            if (message.getData().toLocalDate().compareTo(dataInceput) >= 0 && message.getData().toLocalDate().compareTo(dataSfarsit) <= 0)
                messageList.add(message);
        }
        return messageList;
    }

    public void createEvent(Long idUserCreator, String nume, String locatie, String descriere, LocalDateTime data) {
        serviceEveniment.createEvent(idUserCreator, nume, locatie, descriere, data);
    }

    public Iterable<Eveniment> getAllEvenimente() {
        return serviceEveniment.getAll();
    }


    public Iterable<Eveniment> getAllEvenimenteWhereUserNotParticipating(Long userIdLoggedIn) {
        List<Eveniment> eventList = new ArrayList<>();
        for(Eveniment eveniment : getAllEvenimente()) {
            if(!eveniment.getParticipantiList().stream().map(Entity::getId).collect(Collectors.toList()).contains(userIdLoggedIn)) {
                eventList.add(eveniment);
            }
        }
        return eventList;
    }

    public void participaLaEveniment(Long eventId, Long userId) {
        serviceEveniment.participaLaEveniment(eventId, userId);
    }

    public Iterable<Eveniment> getAllEvenimenteWhereUserParticipating(Long userIdLoggedIn) {
        List<Eveniment> eventList = new ArrayList<>();
        for(Eveniment eveniment : getAllEvenimente()) {
            if(eveniment.getParticipantiList().stream().map(Entity::getId).collect(Collectors.toList()).contains(userIdLoggedIn)) {
                eventList.add(eveniment);
            }
        }
        return eventList;
    }

    public String isParticipating(Long id, Long userIdLoggedIn) {
        Eveniment eveniment = serviceEveniment.findEveniment(id);
        if(eveniment.getParticipantiAbonatiLaNotificariList().stream().map(Entity::getId).collect(Collectors.toList()).contains(userIdLoggedIn))
            return "DA";
        return "NU";

    }

    public void abonareLaEveniment(Long id, Long userIdLoggedIn) {
        serviceEveniment.abonareLaEveniment(id, userIdLoggedIn);
    }

    public void dezabonareLaEveniment(Long id, Long userIdLoggedIn) {
        serviceEveniment.dezabonareEveniment(id, userIdLoggedIn);
    }

    public void nuMaiParticipaLaEveniment(Long id, Long userIdLoggedIn) {
        serviceEveniment.nuMaiParticipaLaEveniment(id , userIdLoggedIn);
    }
}
