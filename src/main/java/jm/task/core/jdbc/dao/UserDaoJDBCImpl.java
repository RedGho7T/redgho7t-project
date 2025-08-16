package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {

private Connection connection; // стат поле для соединения

public UserDaoJDBCImpl() { // делаем соединение и сохраняем в поле
    this.connection = Util.getConnection();
    System.out.println("UserDaoJDBCImpl: Соединение установлено");
}

@Override
public void createUsersTable() {
    String sql = "CREATE TABLE IF NOT EXISTS users (" +
            "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
            "name VARCHAR(50) NOT NULL, " +
            "lastName VARCHAR(50) NOT NULL, " +
            "age TINYINT NOT NULL)";   // запрос в БД на создание таблицы

    try (Statement statement = connection.createStatement()) {
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

    try (Statement statement = connection.createStatement()) {
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

    try {
        connection.setAutoCommit(false);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, lastName);
            pstmt.setByte(3, age);
            pstmt.executeUpdate();
            connection.commit();
            System.out.println("User с именем — " + name + " добавлен в базу данных");
        }

    } catch (SQLException e) {
        try {
            connection.rollback(); // если ошибка -> откат
            System.err.println("Отмена операции");
        } catch (SQLException rollbackException) {
            rollbackException.printStackTrace();
        }
        System.err.println("Ошибка при сохранении пользователя" + e.getMessage());
        throw new RuntimeException(e);
    } finally {
        try {
            connection.setAutoCommit(true); // включаем автокоммит
        } catch (SQLException e) {
            System.out.println("Ошибка при восстановлении автокоммита" + e.getMessage());
        }
    }
}

@Override
public void removeUserById(long id) {
    String sql = "DELETE FROM users WHERE id = ?";

    try {
        connection.setAutoCommit(false);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int rowsAffected = pstmt.executeUpdate();
            connection.commit(); //подтверждаем изменения

            if (rowsAffected > 0) {
                System.out.println("User с ID " + id + " удален из базы данных");
            } else {
                System.out.println("User с ID " + id + " не найден");
            }
        }
    } catch (SQLException e) {
        try {
            connection.rollback(); //откат
            System.err.println("Операция отменена из-за ошибки");
        } catch (SQLException rollbackException) {
            System.err.println("Ошибка при откате изменений: " + e.getMessage());
        }
        System.err.println("Ошибка при удалении пользователя!: " + e.getMessage());
        throw new RuntimeException(e);
    } finally {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("Ошибка при восстановлении автокоммита: " + e.getMessage());
        }
    }
}

@Override
public List<User> getAllUsers() {   // создание
    List<User> users = new ArrayList<>();
    String sql = "SELECT id, name, lastName, age FROM users";

    try (Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {

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

    try {
        connection.setAutoCommit(false); // Отключаем автокоммит

        try (Statement statement = connection.createStatement()) {
            int rowsAffected = statement.executeUpdate(sql);
            connection.commit(); // Подтверждаем транзакцию
            System.out.println("Таблица users очищена. Удалено записей: " + rowsAffected);
        }

    } catch (SQLException e) {
        try {
            connection.rollback(); // Откатываем изменения при ошибке
            System.err.println("Транзакция отменена из-за ошибки");
        } catch (SQLException rollbackException) {
            System.err.println("Ошибка при откате транзакции: " + rollbackException.getMessage());
        }

        System.err.println("Ошибка при очистке таблицы: " + e.getMessage());
        throw new RuntimeException(e);

    } finally {
        try {
            connection.setAutoCommit(true); // Включаем автокоммит обратно
        } catch (SQLException e) {
            System.err.println("Ошибка при восстановлении автокоммита: " + e.getMessage());
        }
    }
}

public void closeConnection() {
    Util.closeConnection(connection);
}
}
