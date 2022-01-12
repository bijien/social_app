package com.example.laborator6map.repository.db;

import com.example.laborator6map.domain.Entity;
import com.example.laborator6map.domain.Eveniment;
import com.example.laborator6map.domain.Utilizator;
import com.example.laborator6map.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EventDbRepository implements Repository<Long, Eveniment> {
    private String url;
    private String username;
    private String password;
    private Repository<Long, Utilizator> utilizatorRepository;

    public EventDbRepository(String url, String username, String password, Repository<Long, Utilizator> utilizatorRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.utilizatorRepository = utilizatorRepository;
    }


    @Override
    public Eveniment findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from events, events_users where events.id = ? and events.id = events_users.id_event")) {

            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Long id = resultSet.getLong("id");
            Long creator = resultSet.getLong("id_user_creator");
            String nume = resultSet.getString("nume");
            String locatie = resultSet.getString("locatie");
            String descriere = resultSet.getString("descriere");
            LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();
            Integer abonat = resultSet.getInt("abonatNotificari");
            Long idUserParticipant = resultSet.getLong("id_user");
            List<Utilizator> userList = new ArrayList<>();
            List<Utilizator> useriAbonati = new ArrayList<>();
            userList.add(utilizatorRepository.findOne(idUserParticipant));
            if (abonat.equals(1))
                useriAbonati.add(utilizatorRepository.findOne(idUserParticipant));
            Utilizator creatorUtilizator = utilizatorRepository.findOne(creator);
            while (resultSet.next()) {
                idUserParticipant = resultSet.getLong("id_user");
                abonat = resultSet.getInt("abonatNotificari");
                userList.add(utilizatorRepository.findOne(idUserParticipant));
                if (abonat.equals(1))
                    useriAbonati.add(utilizatorRepository.findOne(idUserParticipant));
            }
            Eveniment eveniment = new Eveniment(creatorUtilizator, nume, locatie, descriere, date, userList, useriAbonati);
            eveniment.setId(id);
            return eveniment;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Eveniment> findAll() {
        Set<Eveniment> eveniments = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from events, events_users where events.id = events_users.id_event");
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            Long id = resultSet.getLong("id");
            Long creator = resultSet.getLong("id_user_creator");
            String nume = resultSet.getString("nume");
            String locatie = resultSet.getString("locatie");
            String descriere = resultSet.getString("descriere");
            LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();
            Integer abonat = resultSet.getInt("abonatNotificari");
            Long idUserParticipant = resultSet.getLong("id_user");
            List<Utilizator> userList = new ArrayList<>();
            List<Utilizator> useriAbonati = new ArrayList<>();
            Utilizator creatorUtilizator = utilizatorRepository.findOne(creator);
            userList.add(utilizatorRepository.findOne(idUserParticipant));
            if (abonat.equals(1))
                useriAbonati.add(utilizatorRepository.findOne(idUserParticipant));
            Long oldMessageId = id;
            while (resultSet.next()) {
                id = resultSet.getLong("id");
                if (id.equals(oldMessageId)) {
                    idUserParticipant = resultSet.getLong("id_user");
                    abonat = resultSet.getInt("abonatNotificari");
                    userList.add(utilizatorRepository.findOne(idUserParticipant));
                    if (abonat.equals(1))
                        useriAbonati.add(utilizatorRepository.findOne(idUserParticipant));
                } else {
                    Eveniment eveniment = new Eveniment(creatorUtilizator, nume, locatie, descriere, date, new ArrayList<>(userList), new ArrayList<>(useriAbonati));
                    eveniment.setId(oldMessageId);
                    eveniments.add(eveniment);
                    userList.clear();
                    useriAbonati.clear();
                    creator = resultSet.getLong("id_user_creator");
                    nume = resultSet.getString("nume");
                    locatie = resultSet.getString("locatie");
                    descriere = resultSet.getString("descriere");
                    date = resultSet.getTimestamp("data").toLocalDateTime();
                    abonat = resultSet.getInt("abonatNotificari");
                    idUserParticipant = resultSet.getLong("id_user");
                    creatorUtilizator = utilizatorRepository.findOne(creator);
                    userList.add(utilizatorRepository.findOne(idUserParticipant));
                    if (abonat.equals(1))
                        useriAbonati.add(utilizatorRepository.findOne(idUserParticipant));
                }
                oldMessageId = id;
            }
            Eveniment eveniment2 = new Eveniment(creatorUtilizator, nume, locatie, descriere, date, new ArrayList<>(userList), new ArrayList<>(useriAbonati));
            eveniment2.setId(id);
            eveniments.add(eveniment2);
            return eveniments;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Eveniment save(Eveniment entity) {
        String sql = "insert into events (id_user_creator, nume, locatie, descriere, data) values (?, ?, ?, ?, ?)";
        String sql2 = "insert into events_users (id_event, id_user, \"abonatNotificari\") values (?, ?, ?)";
        Long idEvent = 0L;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getCreator().getId());
            ps.setString(2, entity.getNume());
            ps.setString(3, entity.getLocatie());
            ps.setString(4, entity.getDescriere());
            ps.setTimestamp(5, Timestamp.valueOf(entity.getData()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT id from events where id_user_creator= ? and nume = ? and locatie = ? and descriere =? and data=?")) {

            statement.setLong(1, entity.getCreator().getId());
            statement.setString(2, entity.getNume());
            statement.setString(3, entity.getLocatie());
            statement.setString(4, entity.getDescriere());
            statement.setTimestamp(5, Timestamp.valueOf(entity.getData()));
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            idEvent = resultSet.getLong("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Long finalIdEvent = idEvent;
        if (entity.getParticipantiList() != null) {
            entity.getParticipantiList().forEach(x -> {
                try (Connection connection = DriverManager.getConnection(url, username, password);
                     PreparedStatement ps = connection.prepareStatement(sql2)) {

                    ps.setLong(1, finalIdEvent);
                    ps.setLong(2, x.getId());
                    if (entity.getParticipantiAbonatiLaNotificariList().stream().map(Entity::getId).collect(Collectors.toList()).contains(x.getId()))
                        ps.setLong(3, 1L);
                    else
                        ps.setLong(3, 0L);

                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
        return null;
    }

    @Override
    public Eveniment delete(Long aLong) {
        String sql = "delete from events where id = ?";
        String sql2 = "delete from events_users where id_event = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, aLong);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql2)) {

            ps.setLong(1, aLong);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Eveniment update(Eveniment entity) {
        return null;
    }

    public void participaLaEveniment(Long eventId, Long userId) {
        String sql2 = "insert into events_users (id_event, id_user, \"abonatNotificari\") values (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql2)) {

            ps.setLong(1, eventId);
            ps.setLong(2, userId);
            ps.setLong(3, 1L);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void nuMaiParticipaLaEveniment(Long eventId, Long userId) {
        String sql = "delete from events_users where id_event = ? and id_user = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, eventId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void abonareLaEveniment(Long eventId, Long userId) {
        String sql2 = "update events_users set \"abonatNotificari\"=1 where id_event=? and id_user=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql2)) {

            ps.setLong(1, eventId);
            ps.setLong(2, userId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dezabonareLaNotificari(Long eventId, Long userId) {
        String sql2 = "update events_users set \"abonatNotificari\"=0 where id_event=? and id_user=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql2)) {

            ps.setLong(1, eventId);
            ps.setLong(2, userId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
