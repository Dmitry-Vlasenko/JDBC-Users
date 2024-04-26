package com.dvlasenko.repository.impl;

import com.dvlasenko.database.DBConn;
import com.dvlasenko.entity.User;
import com.dvlasenko.repository.AppRepository;
import com.dvlasenko.utils.Constants;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContactRepository implements AppRepository<User> {

    private final static String TABLE_CONTACTS = "user";

    @Override
    public String create(User user) {
        String sql = "INSERT INTO " + TABLE_CONTACTS +
                " (firstName, lastName, email) VALUES(?, ?, ?)";
        try (Connection connection = DBConn.Connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.executeUpdate();
            return Constants.DATA_INSERT_MSG;

        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    public Optional<List<User>> read() {
        try (Connection connection = DBConn.Connect();
             Statement stmt = connection.createStatement()) {

            List<User> list = new ArrayList<>();
            String sql = "SELECT id, firstName, lastName, email FROM " + TABLE_CONTACTS;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new User(
                                rs.getLong("id"),
                                rs.getString("firstName"),
                                rs.getString("lastName"),
                                rs.getString("email")
                        )
                );
            }

            rs.close();
            return Optional.of(list);

        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @Override
    public String update(User user) {
        if (readById(user.getId()).isEmpty()) {
            return Constants.DATA_ABSENT_MSG;
        } else {
            String sql = "UPDATE " + TABLE_CONTACTS +
                    " SET firstName = ?, lastName = ?, email = ?" +
                    " WHERE id = ?";
            try (Connection connection = DBConn.Connect();
                 PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setString(1, user.getFirstName());
                pst.setString(2, user.getLastName());
                pst.setString(3, user.getEmail());
                pst.setLong(4, user.getId());
                pst.executeUpdate();
                return Constants.DATA_UPDATE_MSG;
            } catch (SQLException e) {
                return e.getMessage();
            }
        }
    }

    @Override
    public String delete(Long id) {
        if (!isIdExists(id)) {
            return Constants.DATA_ABSENT_MSG;
        } else {
            String sql = "DELETE FROM " + TABLE_CONTACTS +
                    " WHERE id = ?";
            try (Connection connection = DBConn.Connect();
                 PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setLong(1, id);
                pst.executeUpdate();
                return Constants.DATA_DELETE_MSG;
            } catch (SQLException e) {
                return e.getMessage();
            }
        }
    }

    @Override
    public Optional<User> readById(Long id) {
        String sql = "SELECT id, firstName, lastName, email FROM " + TABLE_CONTACTS + " WHERE id = ?";
        try (Connection connection = DBConn.Connect();
             PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setLong(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                User contact = new User(
                        rs.getLong("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email")
                );
                return Optional.of(contact);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    private boolean isIdExists(Long id) {
        String sql = "SELECT COUNT(id) FROM " + TABLE_CONTACTS + " WHERE id = ?";
        try (Connection connection = DBConn.Connect();
             PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setLong(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }
}
