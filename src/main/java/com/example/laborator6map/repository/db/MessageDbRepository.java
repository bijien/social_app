package com.example.laborator6map.repository.db;

import com.example.laborator6map.domain.Message;
import com.example.laborator6map.domain.Utilizator;
import com.example.laborator6map.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageDbRepository implements Repository<Long, Message> {
    private String url;
    private String username;
    private String password;
    private Repository<Long, Utilizator> utilizatorRepository;

    public MessageDbRepository(String url, String username, String password, Repository<Long, Utilizator> utilizatorRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.utilizatorRepository = utilizatorRepository;
    }

    @Override
    public Message findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages, messages_to where messages.id = ? and messages.id = messages_to.id_mesaj")) {

            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Long id = resultSet.getLong("id");
            Long from = resultSet.getLong("from");
            String message = resultSet.getString("mesaj");
            LocalDateTime date = resultSet.getTimestamp("data_trimiterii").toLocalDateTime();
            Long reply = resultSet.getLong("reply");
            Long to = resultSet.getLong("id_to");
            List<Utilizator> userList = new ArrayList<>();
            userList.add(utilizatorRepository.findOne(to));
            Utilizator fromUtilizator = utilizatorRepository.findOne(from);
            while (resultSet.next()) {
                to = resultSet.getLong("id_to");
                userList.add(utilizatorRepository.findOne(to));
            }
            Message message1 = new Message(fromUtilizator, userList, date, message, reply);
            message1.setId(id);
            return message1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Message> findAll() {
        Set<Message> messages = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages, messages_to where messages.id=messages_to.id_mesaj");
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            Long id = resultSet.getLong("id");
            Long from = resultSet.getLong("from");
            String message = resultSet.getString("mesaj");
            LocalDateTime date = resultSet.getTimestamp("data_trimiterii").toLocalDateTime();
            Long reply = resultSet.getLong("reply");
            Long to = resultSet.getLong("id_to");
            Utilizator fromUtilizator = utilizatorRepository.findOne(from);
            List<Utilizator> userList = new ArrayList<>();
            userList.add(utilizatorRepository.findOne(to));
            Long oldMessageId = id;
            while (resultSet.next()) {
                id = resultSet.getLong("id");
                if (id.equals(oldMessageId)) {
                    to = resultSet.getLong("id_to");
                    userList.add(utilizatorRepository.findOne(to));
                } else {
                    Message message1 = new Message(fromUtilizator, new ArrayList<>(userList), date, message, reply);
                    message1.setId(oldMessageId);
                    messages.add(message1);
                    userList.clear();
                    from = resultSet.getLong("from");
                    message = resultSet.getString("mesaj");
                    date = resultSet.getTimestamp("data_trimiterii").toLocalDateTime();
                    reply = resultSet.getLong("reply");
                    to = resultSet.getLong("id_to");
                    fromUtilizator = utilizatorRepository.findOne(from);
                    userList.add(utilizatorRepository.findOne(to));
                }
                oldMessageId = id;
            }
            Message message2 = new Message(fromUtilizator, new ArrayList<>(userList), date, message, reply);
            message2.setId(id);
            messages.add(message2);
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Message save(Message entity) {
        String sql = "insert into messages (\"from\", mesaj, data_trimiterii, reply) values (?, ?, ?, ?)";
        String sql2 = "insert into messages_to (id_mesaj, id_to) values (?, ?)";
        Long idMesaj = 0L;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getFrom().getId());
            ps.setString(2, entity.getMessage());
            ps.setTimestamp(3, Timestamp.valueOf(entity.getData()));
            ps.setLong(4, entity.getReply());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT id from messages where \"from\"= ? and mesaj = ? and data_trimiterii = ? and reply =?")) {

            statement.setLong(1, entity.getFrom().getId());
            statement.setString(2, entity.getMessage());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getData()));
            statement.setLong(4, entity.getReply());
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            idMesaj = resultSet.getLong("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Long finalIdMesaj = idMesaj;
        entity.getTo().forEach(x -> {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement ps = connection.prepareStatement(sql2)) {

                ps.setLong(1, finalIdMesaj);
                ps.setLong(2, x.getId());

                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return null;
    }

    @Override
    public Message delete(Long aLong) {
        String sql = "delete from messages where id = ?";
        String sql2 = "delete from messages_to where id_mesaj = ?";

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
    public Message update(Message entity) {
        return null;
    }

    public Iterable<Message> getConversationPaginated(Long idUser1, Long idUser2, int offset, int limit) {
        Set<Message> messages = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select id, \"from\", mesaj, data_trimiterii, reply, id_to\n" +
                     "from messages m\n" +
                     "inner join messages_to mt on m.id = mt.id_mesaj\n" +
                     "where (m.from = ? and mt.id_to = ?) or (m.from = ? and mt.id_to = ?)\n" +
                     "order by m.data_trimiterii desc\n" +
                     "offset ? limit ?")) {
            statement.setLong(1, idUser1);
            statement.setLong(2, idUser2);
            statement.setLong(3, idUser2);
            statement.setLong(4, idUser1);
            statement.setInt(5, offset);
            statement.setInt(6, limit);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Long id = resultSet.getLong("id");
            Long from = resultSet.getLong("from");
            String message = resultSet.getString("mesaj");
            LocalDateTime date = resultSet.getTimestamp("data_trimiterii").toLocalDateTime();
            Long reply = resultSet.getLong("reply");
            Long to = resultSet.getLong("id_to");
            Utilizator fromUtilizator = utilizatorRepository.findOne(from);
            List<Utilizator> userList = new ArrayList<>();
            userList.add(utilizatorRepository.findOne(to));
            Message message1 = new Message(fromUtilizator, new ArrayList<>(userList), date, message, reply);
            message1.setId(id);
            messages.add(message1);
            userList.clear();
            while (resultSet.next()) {
                id = resultSet.getLong("id");
                from = resultSet.getLong("from");
                message = resultSet.getString("mesaj");
                date = resultSet.getTimestamp("data_trimiterii").toLocalDateTime();
                reply = resultSet.getLong("reply");
                to = resultSet.getLong("id_to");
                fromUtilizator = utilizatorRepository.findOne(from);
                userList.add(utilizatorRepository.findOne(to));
                message1 = new Message(fromUtilizator, new ArrayList<>(userList), date, message, reply);
                message1.setId(id);
                messages.add(message1);
                userList.clear();
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
