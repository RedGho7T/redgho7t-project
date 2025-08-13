package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements jm.task.core.jdbc.dao.UserDao {

    public UserDaoJDBCImpl() {

    }

    @Override
    public void createUsersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" + "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, " + "name VARCHAR(50) NOT NULL, " + "lastName VARCHAR(50) NOT NULL, " + "age TINYINT NOT NULL)";

        try (Connection connection = jm.task.core.jdbc.util.Util.getConnection(); Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
            System.out.println("Таблица users создана успешно");

        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблицы: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }

    @Override
    public void dropUsersTable() {
        String sql = "DROP TABLE IF EXISTS users";

        try (Connection connection = jm.task.core.jdbc.util.Util.getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
            System.out.println("Таблица users удалена успешно");

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении таблицы: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        String sql = "INSERT INTO users (name, lastname, age) VALUES (?, ?, ?)";

        try (Connection connection = jm.task.core.jdbc.util.Util.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, lastName);
            pstmt.setByte(3, age);

            pstmt.executeUpdate();
            System.out.println("User с именем — " + name + " добавлен в базу данных");

        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении пользователя: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeUserById(long id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = jm.task.core.jdbc.util.Util.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User с ID " + id + " удален из базы данных");
            } else {
                System.out.println("User с ID " + id + " не найден");
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении пользователя: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, lastName, age FROM users";

        try (Connection connection = jm.task.core.jdbc.util.Util.getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setName(resultSet.getString("name"));
                user.setLastName(resultSet.getString("lastName"));
                user.setAge(resultSet.getByte("age"));

                users.add(user);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении пользователей: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return users;
    }

    @Override
    public void cleanUsersTable() {
        String sql = "DELETE FROM users";

        try (Connection connection = jm.task.core.jdbc.util.Util.getConnection(); Statement statement = connection.createStatement()) {

            int rowsAffected = statement.executeUpdate(sql);
            System.out.println("Таблица users очищена. Удалено записей: " + rowsAffected);

        } catch (SQLException e) {
            System.err.println("Ошибка при очистке таблицы: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
