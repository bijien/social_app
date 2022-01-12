package com.example.laborator6map.repository.db;

import com.example.laborator6map.domain.Utilizator;
import com.example.laborator6map.repository.Repository;
import com.example.laborator6map.validators.Validator;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class UtilizatorDbRepository implements Repository<Long, Utilizator> {
    private String url;
    private String username;
    private String password;


    public UtilizatorDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    @Override
    public Utilizator findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users where id = ?")) {

            statement.setLong(1,aLong);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userName = resultSet.getString("user_name");
                String password = resultSet.getString("password");

                Utilizator utilizator = new Utilizator(firstName, lastName, userName, password);
                utilizator.setId(id);
                return utilizator;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userName = resultSet.getString("user_name");
                String password = resultSet.getString("password");

                Utilizator utilizator = new Utilizator(firstName, lastName,userName,password);
                utilizator.setId(id);
                users.add(utilizator);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Utilizator save(Utilizator entity) {

        String sql = "insert into users (first_name, last_name, user_name, password) values (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setString(3, entity.getUserName());
            ps.setString(4, entity.getPassword());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Utilizator delete(Long aLong) {
        String sql = "delete from users where id = ?";

        try (Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1,aLong);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Utilizator update(Utilizator entity) {
        String sql = "update users set first_name = ?, last_name = ? where id = ?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setLong(3,entity.getId());

            ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
