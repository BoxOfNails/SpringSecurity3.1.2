package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.dto.WebUser;
import ru.kata.spring.boot_security.demo.entity.User;

import java.util.List;

public interface UserService extends UserDetailsService {
    List<User> findAllUsers();

    User findById(int theId);
    void deleteById(int theId);
    User findByUserName(String username);
    void save(User User);
    void register(WebUser webUser);
}
