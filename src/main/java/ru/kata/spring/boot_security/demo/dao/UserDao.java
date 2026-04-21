package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.entity.User;

import java.util.List;

public interface UserDao {
    List<User> findAllUsers();

    User findById(int theId);

    User save(User theUser);

    void deleteById(int theId);

    User findByUsername(String username);
}
