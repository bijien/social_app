package com.example.laborator6map.service;

import com.example.laborator6map.domain.Message;
import com.example.laborator6map.domain.Prietenie;
import com.example.laborator6map.domain.Tuple;
import com.example.laborator6map.domain.Utilizator;
import com.example.laborator6map.utils.GFG;
import com.example.laborator6map.utils.Graph;


import java.lang.reflect.Member;
import java.time.LocalDate;
import java.util.*;

public class ServiceNetwork {
    private ServiceUser serviceUser;
    private ServicePrietenie servicePrietenie;
    private ServiceMessage serviceMessage;

    public ServiceNetwork(ServiceUser serviceUser, ServicePrietenie servicePrietenie, ServiceMessage serviceMessage) {
        this.serviceUser = serviceUser;
        this.servicePrietenie = servicePrietenie;
        this.serviceMessage = serviceMessage;
    }

    public void addUtilizator(String firstName, String lastName, String userName, String password) {
        serviceUser.addUtilizator(firstName, lastName, userName, password);
    }

    public void deleteUtilizator(Long id) {
        List<Tuple<Long, Long>> listID = new ArrayList<>();
        for (Prietenie prietenie : servicePrietenie.getAll())
            if (prietenie.getId().getLeft().equals(id) || prietenie.getId().getRight().equals(id))
                //servicePrietenie.deletePrietenie(prietenie.getId().getLeft(),prietenie.getId().getRight());
                listID.add(new Tuple<>(prietenie.getId().getLeft(), prietenie.getId().getRight()));
        for (Tuple<Long, Long> tuple : listID)
            servicePrietenie.deletePrietenie(tuple.getLeft(), tuple.getRight());
        serviceUser.deleteUtilizator(id);
    }

    public Iterable<Utilizator> getAllUtilizator() {
        return serviceUser.getAll();
    }

    public void addPrietenie(Long id1, Long id2) {
        servicePrietenie.addPrietenie(id1, id2);
    }

    public void deletePrietenie(Long id1, Long id2) {
        servicePrietenie.deletePrietenie(id1, id2);
    }

    public Iterable<Prietenie> getAllPrietenie() {
        return servicePrietenie.getAll();
    }


    private int getMaxId() {
        int max = -1;
        for (Utilizator utilizator : serviceUser.getAll())
            if (utilizator.getId().intValue() > max)
                max = utilizator.getId().intValue();
        return max;
    }

    /**
     * @return the number of communities
     */
    public int numarComunitati() {
        Graph graph = new Graph(getMaxId());
        for (Prietenie prietenie : servicePrietenie.getAll()) {
            graph.addEdge(prietenie.getId().getLeft().intValue(), prietenie.getId().getRight().intValue());
        }
        graph.DFS();
        return graph.ConnecetedComponents();
    }


    /**
     * @return the longest path in the freindship list
     */
    public int longestPathFriendship() {
        int n = getMaxId();
        Vector<Vector<GFG.pair>> graph = new Vector<Vector<GFG.pair>>();
        for (int i = 0; i < n + 1; i++) {
            graph.add(new Vector<GFG.pair>());
        }
        for (Prietenie prietenie : servicePrietenie.getAll()) {
            graph.get(prietenie.getId().getLeft().intValue()).add(new GFG.pair(prietenie.getId().getRight().intValue(), 1));
            graph.get(prietenie.getId().getRight().intValue()).add(new GFG.pair(prietenie.getId().getLeft().intValue(), 1));
        }

        return GFG.longestCable(graph, n);
    }

    public Iterable<Prietenie> friendListForAUser(Long userId) {
        return servicePrietenie.friendListForAUser(userId);
    }

    public Utilizator findUser(Long userId) {
        return serviceUser.findUser(userId);
    }

    public Iterable<Prietenie> friendListForAUserAndDate(Long userId, Integer month) {
        return servicePrietenie.friendListForAUserAndDate(userId, month);
    }

    public Iterable<Message> mesajeUtilizator(Long id) {
        return serviceMessage.mesajeUtilizator(id);
    }

    public void trimiteMesaj(Long from, List<Long> to, String mesaj) {
        serviceMessage.trimiteMesaj(from, to, mesaj);
    }

    public void raspundeMesaj(Long idFrom, Long idMesaj, String mesaj) {
        serviceMessage.raspundeMesaj(idFrom, idMesaj, mesaj);
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

    public void replyAll(Long idFrom, Long idMesaj, String mesaj) {
        serviceMessage.replyAll(idFrom, idMesaj, mesaj);
    }

    public Utilizator findUserByFirstNameAndLastName(String firstName, String lastName) {
        return serviceUser.findUserByFirstNameAndLastName(firstName, lastName);
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

}
