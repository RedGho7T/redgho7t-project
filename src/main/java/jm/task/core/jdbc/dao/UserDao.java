package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;

import java.util.List;

public interface UserDao {  // Интерфейс доступа к данным
void createUsersTable(); // создаем таблицу

void dropUsersTable(); // удаляем таблицу

void saveUser(String name, String lastName, byte age); // сохраняем нового юзера

void removeUserById(long id); // удаляет по ID

List<User> getAllUsers(); // получаем всех юзеров из БД

void cleanUsersTable(); // сносим фулл таблицу, но с сохранением структуры
}
