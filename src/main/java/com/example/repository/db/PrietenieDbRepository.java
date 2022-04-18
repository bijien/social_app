package com.example.repository.db;

import com.example.domain.Prietenie;
import com.example.domain.Tuple;
import com.example.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class PrietenieDbRepository implements Repository<Tuple<Long,Long>, Prietenie> {
    private String url;
    private String username;
    private String password;

    public PrietenieDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public Prietenie findOne(Tuple<Long, Long> longLongTuple) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships where id1 = ? and id2 = ? " +
                     "or id1 = ? and id2 = ?")) {

            statement.setLong(1,longLongTuple.getLeft());
            statement.setLong(2,longLongTuple.getRight());
            statement.setLong(3,longLongTuple.getRight());
            statement.setLong(4,longLongTuple.getLeft());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                Date localDate = resultSet.getDate("data_prietenie");
                String status = resultSet.getString("status");
                Prietenie prietenie = new Prietenie();
                prietenie.setId(new Tuple<>(id1, id2));
                prietenie.setLocalDate(localDate.toLocalDate());
                prietenie.setStatus(status);
                return prietenie;

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                Date localDate = resultSet.getDate("data_prietenie");
                String status = resultSet.getString("status");
                Prietenie prietenie = new Prietenie();
                prietenie.setId(new Tuple<>(id1,id2));
                prietenie.setLocalDate(localDate.toLocalDate());
                prietenie.setStatus(status);
                friendships.add(prietenie);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Prietenie save(Prietenie entity) {
        String sql = "insert into friendships (id1, id2, data_prietenie,status) values (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId().getLeft());
            ps.setLong(2, entity.getId().getRight());
            ps.setDate(3, Date.valueOf(entity.getLocalDate()));
            ps.setString(4,entity.getStatus());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Prietenie delete(Tuple<Long, Long> longLongTuple) {
        String sql = "delete from friendships where id1 = ? and id2 = ? or id1 = ? and id2 = ?";

        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1,longLongTuple.getLeft());
            ps.setLong(2,longLongTuple.getRight());
            ps.setLong(3,longLongTuple.getRight());
            ps.setLong(4,longLongTuple.getLeft());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Prietenie update(Prietenie entity) {
        String sql = "update friendships set status = ? where id1 = ? and id2 = ?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getStatus());
            ps.setLong(2, entity.getId().getLeft());
            ps.setLong(3,entity.getId().getRight());

            ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
