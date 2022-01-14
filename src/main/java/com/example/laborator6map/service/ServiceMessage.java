package com.example.laborator6map.service;

import com.example.laborator6map.domain.*;
import com.example.laborator6map.repository.Repository;
import com.example.laborator6map.repository.db.MessageDbRepository;
import com.example.laborator6map.validators.RepositoryException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ServiceMessage {
    private Repository<Long, Utilizator> utilizatorRepository;
    private MessageDbRepository messageRepository;

    public ServiceMessage(Repository<Long, Utilizator> utilizatorRepository, MessageDbRepository messageRepository) {
        this.utilizatorRepository = utilizatorRepository;
        this.messageRepository = messageRepository;
    }

    public void trimiteMesaj(Long idFrom, List<Long> idTo, String mesaj) {
        if (utilizatorRepository.findOne(idFrom) == null)
            throw new RepositoryException("Utilizator neexistent");
        idTo.forEach(x -> {
            if (utilizatorRepository.findOne(x) == null)
                throw new RepositoryException("Utilizator neexistent");
        });
        Utilizator fromUtilizator = utilizatorRepository.findOne(idFrom);
        List<Utilizator> userList = new ArrayList<>();
        idTo.forEach(x -> {
            userList.add(utilizatorRepository.findOne(x));
        });
        Message message = new Message(fromUtilizator, userList, LocalDateTime.now(), mesaj, 0L);
        messageRepository.save(message);
    }

    public void raspundeMesaj(Long idFrom, Long idMesaj, String mesaj) {
        if (messageRepository.findOne(idMesaj) == null)
            throw new RepositoryException("Mesaj neexistent");
        if (utilizatorRepository.findOne(idFrom) == null)
            throw new RepositoryException("Utilizator neexistent");
        if (!messageRepository.findOne(idMesaj).getTo().stream().map(Entity::getId).collect(Collectors.toList()).contains(idFrom))
            throw new RepositoryException("Nu se poate da reply la aceasta conversatie!");
        Utilizator fromUtilizator = utilizatorRepository.findOne(idFrom);
        List<Utilizator> userList = new ArrayList<>();
        userList.add(messageRepository.findOne(idMesaj).getFrom());
        Message message = new Message(fromUtilizator, userList, LocalDateTime.now(), mesaj, idMesaj);
        messageRepository.save(message);
    }


    public Iterable<Message> conversatieUtilizatori(Long id1, Long id2) {
        if (utilizatorRepository.findOne(id1) == null || utilizatorRepository.findOne(id2) == null)
            throw new RepositoryException("Utilizator neexistent");
        HashSet<Message> messages = (HashSet<Message>) messageRepository.findAll();
        List<Message> messageList = messages.stream().filter(x -> (x.getFrom().getId().equals(id1) &&
                x.getTo().stream().map(Entity::getId).collect(Collectors.toList()).contains(id2)) ||
                (x.getFrom().getId().equals(id2) &&
                        x.getTo().stream().map(Entity::getId).collect(Collectors.toList()).contains(id1))).collect(Collectors.toList());
        Collections.sort(messageList, Comparator.comparing(Message::getData));
        return messageList;
    }

    public Iterable<Message> mesajeUtilizator(Long id) {
        if (utilizatorRepository.findOne(id) == null)
            throw new RepositoryException("Utilizator neexistent");
        HashSet<Message> messages = (HashSet<Message>) messageRepository.findAll();
        List<Message> messageList = messages.stream().filter(x -> x.getTo().stream().map(Entity::getId).collect(Collectors.toList()).contains(id)).collect(Collectors.toList());
        Collections.sort(messageList, Comparator.comparing(Message::getData));
        return messageList;
    }

    public Iterable<Message> getConversationPaginated(Long idUser1, Long idUser2, int offset, int limit) {
        return messageRepository.getConversationPaginated(idUser1, idUser2, offset, limit);
    }
}
